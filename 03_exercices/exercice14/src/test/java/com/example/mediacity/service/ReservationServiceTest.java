package com.example.mediacity.service;

import com.example.mediacity.model.Loan;
import com.example.mediacity.model.Member;
import com.example.mediacity.model.ReservationResult;
import com.example.mediacity.model.ReservationStatus;
import com.example.mediacity.model.Work;
import com.example.mediacity.repository.LoanRepository;
import com.example.mediacity.repository.MemberRepository;
import com.example.mediacity.repository.ReservationRepository;
import com.example.mediacity.repository.WorkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock private ReservationRepository reservationRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private WorkRepository workRepository;
  @Mock private LoanRepository loanRepository;

  private ReservationService reservationService;

  private final LocalDate today = LocalDate.of(2026, 3, 1);

  @BeforeEach
  void setUp() {
    reservationService = new ReservationService(
        reservationRepository, memberRepository, workRepository, loanRepository);
  }

  @Test
  void shouldAcceptReservationWhenWorkIsOnLoan() {
    when(memberRepository.findById("M2")).thenReturn(Optional.of(new Member("M2", "Bob", false)));
    when(workRepository.findById("W1")).thenReturn(Optional.of(new Work("W1", "Dune")));
    when(loanRepository.findActiveByWorkId("W1"))
        .thenReturn(Optional.of(new Loan(1L, "M1", "W1", today, today.plusDays(21), null, 0.0)));
    when(reservationRepository.findByMemberId("M2")).thenReturn(List.of());
    when(reservationRepository.save(any()))
        .thenAnswer(invocation -> {
          var reservation = invocation.getArgument(0, com.example.mediacity.model.Reservation.class);
          return new com.example.mediacity.model.Reservation(
              10L, reservation.memberId(), reservation.workId(), reservation.createdAt(), reservation.status());
        });

    ReservationResult result = reservationService.reserve("M2", "W1", today);

    assertThat(result).isInstanceOf(ReservationResult.Accepted.class);
  }

  @Test
  void shouldRejectReservationWhenWorkIsAvailable() {
    when(memberRepository.findById("M2")).thenReturn(Optional.of(new Member("M2", "Bob", false)));
    when(workRepository.findById("W1")).thenReturn(Optional.of(new Work("W1", "Dune")));
    when(loanRepository.findActiveByWorkId("W1")).thenReturn(Optional.empty());

    ReservationResult result = reservationService.reserve("M2", "W1", today);

    assertThat(result).isInstanceOf(ReservationResult.Rejected.class);
    assertThat(((ReservationResult.Rejected) result).reason())
        .isEqualTo("L'ouvrage est disponible, la réservation n'est pas nécessaire");
  }

  @Test
  void shouldRejectReservationWhenMemberSuspended() {
    when(memberRepository.findById("M2")).thenReturn(Optional.of(new Member("M2", "Bob", true)));

    ReservationResult result = reservationService.reserve("M2", "W1", today);

    assertThat(result).isInstanceOf(ReservationResult.Rejected.class);
    assertThat(((ReservationResult.Rejected) result).reason()).isEqualTo("Adhérent suspendu");
  }

  @Test
  void shouldHoldWorkForFirstReservationOnReturn() {
    var pending = new com.example.mediacity.model.Reservation(
        1L, "M2", "W1", today, ReservationStatus.PENDING);

    when(reservationRepository.findPendingByWorkId("W1")).thenReturn(List.of(pending));
    when(workRepository.findById("W1")).thenReturn(Optional.of(new Work("W1", "Dune")));

    reservationService.onWorkReturned("W1", today);

    verify(reservationRepository).save(pending.withStatus(ReservationStatus.READY));
    verify(workRepository).save(new Work("W1", "Dune", "M2"));
  }
}
