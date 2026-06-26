package com.example.mediacity.service;

import com.example.mediacity.LoanPolicy;
import com.example.mediacity.model.Loan;
import com.example.mediacity.model.LoanResult;
import com.example.mediacity.model.Member;
import com.example.mediacity.model.ReturnResult;
import com.example.mediacity.model.Work;
import com.example.mediacity.repository.LoanRepository;
import com.example.mediacity.repository.MemberRepository;
import com.example.mediacity.repository.ReservationRepository;
import com.example.mediacity.repository.WorkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

  @Mock private LoanRepository loanRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private WorkRepository workRepository;
  @Mock private ReservationRepository reservationRepository;
  @Mock private ReservationService reservationService;

  @InjectMocks private LoanService loanService;

  private final LocalDate loanDate = LocalDate.of(2026, 1, 10);

  @Test
  void shouldCreateLoanWithDueDateIn21Days() {
    when(memberRepository.findById("M1")).thenReturn(Optional.of(new Member("M1", "Alice", false)));
    when(workRepository.findById("W1")).thenReturn(Optional.of(new Work("W1", "Dune")));
    when(loanRepository.findActiveByWorkId("W1")).thenReturn(Optional.empty());
    when(loanRepository.save(any(Loan.class)))
        .thenAnswer(invocation -> {
          Loan loan = invocation.getArgument(0);
          return new Loan(1L, loan.memberId(), loan.workId(), loan.loanDate(), loan.dueDate(), null, 0.0);
        });

    LoanResult result = loanService.borrow("M1", "W1", loanDate);

    assertThat(result).isInstanceOf(LoanResult.Accepted.class);
    Loan loan = ((LoanResult.Accepted) result).loan();
    assertThat(loan.dueDate()).isEqualTo(loanDate.plusDays(LoanPolicy.LOAN_DURATION_DAYS));
    assertThat(loan.memberId()).isEqualTo("M1");
    assertThat(loan.workId()).isEqualTo("W1");
  }

  @Test
  void shouldRejectLoanWhenWorkAlreadyBorrowed() {
    when(memberRepository.findById("M1")).thenReturn(Optional.of(new Member("M1", "Alice", false)));
    when(workRepository.findById("W1")).thenReturn(Optional.of(new Work("W1", "Dune")));
    when(loanRepository.findActiveByWorkId("W1"))
        .thenReturn(Optional.of(new Loan(9L, "M2", "W1", loanDate, loanDate.plusDays(21), null, 0.0)));

    LoanResult result = loanService.borrow("M1", "W1", loanDate);

    assertThat(result).isInstanceOf(LoanResult.Rejected.class);
    assertThat(((LoanResult.Rejected) result).reason()).isEqualTo("Ouvrage déjà emprunté");
    verify(loanRepository, never()).save(any());
  }

  @Test
  void shouldRejectLoanWhenMemberSuspended() {
    when(memberRepository.findById("M1")).thenReturn(Optional.of(new Member("M1", "Alice", true)));

    LoanResult result = loanService.borrow("M1", "W1", loanDate);

    assertThat(result).isInstanceOf(LoanResult.Rejected.class);
    assertThat(((LoanResult.Rejected) result).reason()).isEqualTo("Adhérent suspendu");
    verify(loanRepository, never()).save(any());
  }

  @Test
  void shouldCalculatePenaltyWhenReturnedLate() {
    Loan activeLoan = new Loan(1L, "M1", "W1", loanDate, loanDate.plusDays(21), null, 0.0);
    LocalDate returnDate = activeLoan.dueDate().plusDays(10);

    when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
    when(loanRepository.save(any(Loan.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ReturnResult result = loanService.returnWork(1L, returnDate);

    assertThat(result).isInstanceOf(ReturnResult.Completed.class);
    ReturnResult.Completed completed = (ReturnResult.Completed) result;
    assertThat(completed.daysLate()).isEqualTo(10);
    assertThat(completed.loan().penaltyAmount()).isEqualTo(1.5);
    verify(reservationService).onWorkReturned("W1", returnDate);
  }

  @Test
  void shouldReturnZeroPenaltyWhenOnTime() {
    Loan activeLoan = new Loan(1L, "M1", "W1", loanDate, loanDate.plusDays(21), null, 0.0);
    LocalDate returnDate = activeLoan.dueDate();

    when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
    when(loanRepository.save(any(Loan.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ReturnResult result = loanService.returnWork(1L, returnDate);

    ReturnResult.Completed completed = (ReturnResult.Completed) result;
    assertThat(completed.daysLate()).isZero();
    assertThat(completed.loan().penaltyAmount()).isZero();
    assertThat(completed.majorDelayRecorded()).isFalse();
    verify(reservationRepository, never()).saveMajorDelay(any());
  }

  @Test
  void shouldSuspendMemberAfterThreeMajorDelaysInYear() {
    Loan activeLoan = new Loan(1L, "M1", "W1", loanDate, loanDate.plusDays(21), null, 0.0);
    LocalDate returnDate = activeLoan.dueDate().plusDays(LoanPolicy.MAJOR_DELAY_DAYS);

    when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
    when(loanRepository.save(any(Loan.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(reservationRepository.countMajorDelaysForMemberSince(eq("M1"), any(LocalDate.class)))
        .thenReturn(3L);
    when(memberRepository.findById("M1")).thenReturn(Optional.of(new Member("M1", "Alice", false)));

    loanService.returnWork(1L, returnDate);

    ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
    verify(memberRepository).save(memberCaptor.capture());
    assertThat(memberCaptor.getValue().suspended()).isTrue();
  }

  @Test
  void shouldRejectLoanWhenWorkReservedForAnotherMember() {
    when(memberRepository.findById("M1")).thenReturn(Optional.of(new Member("M1", "Alice", false)));
    when(workRepository.findById("W1")).thenReturn(Optional.of(new Work("W1", "Dune", "M2")));

    LoanResult result = loanService.borrow("M1", "W1", loanDate);

    assertThat(result).isInstanceOf(LoanResult.Rejected.class);
    assertThat(((LoanResult.Rejected) result).reason()).isEqualTo("Ouvrage réservé pour un autre adhérent");
  }

  @Test
  void shouldThrowWhenReturningUnknownLoan() {
    when(loanRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> loanService.returnWork(99L, loanDate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Prêt introuvable");
  }
}
