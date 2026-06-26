package com.example.mediacity.service;

import com.example.mediacity.LoanPolicy;
import com.example.mediacity.model.Loan;
import com.example.mediacity.model.LoanResult;
import com.example.mediacity.model.MajorDelayEvent;
import com.example.mediacity.model.Member;
import com.example.mediacity.model.ReturnResult;
import com.example.mediacity.model.Work;
import com.example.mediacity.repository.LoanRepository;
import com.example.mediacity.repository.MemberRepository;
import com.example.mediacity.repository.ReservationRepository;
import com.example.mediacity.repository.WorkRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoanService {

    private static final int SUSPENSION_THRESHOLD = 3;

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final WorkRepository workRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    public LoanService(
            LoanRepository loanRepository,
            MemberRepository memberRepository,
            WorkRepository workRepository,
            ReservationRepository reservationRepository,
            ReservationService reservationService
    ) {
        this.loanRepository = loanRepository;
        this.memberRepository = memberRepository;
        this.workRepository = workRepository;
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
    }

    public LoanResult borrow(String memberId, String workId, LocalDate loanDate) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return new LoanResult.Rejected("Adhérent inconnu");
        }

        if (member.suspended()) {
            return new LoanResult.Rejected("Adhérent suspendu");
        }

        Work work = workRepository.findById(workId).orElse(null);
        if (work == null) {
            return new LoanResult.Rejected("Ouvrage inconnu");
        }

        if (work.heldForMemberId() != null && !work.heldForMemberId().equals(memberId)) {
            return new LoanResult.Rejected("Ouvrage réservé pour un autre adhérent");
        }

        if (loanRepository.findActiveByWorkId(workId).isPresent()) {
            return new LoanResult.Rejected("Ouvrage déjà emprunté");
        }

        LocalDate dueDate = loanDate.plusDays(LoanPolicy.LOAN_DURATION_DAYS);
        Loan loan = loanRepository.save(new Loan(0, memberId, workId, loanDate, dueDate, null, 0.0));

        if (work.heldForMemberId() != null) {
            reservationService.fulfillReadyReservation(workId, memberId);
            workRepository.save(work.withoutHold());
        }

        return new LoanResult.Accepted(loan);
    }

    public ReturnResult returnWork(long loanId, LocalDate returnDate) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Prêt introuvable"));

        if (!loan.isActive()) {
            throw new IllegalStateException("Ce prêt est déjà clôturé");
        }

        long daysLate = ChronoUnit.DAYS.between(loan.dueDate(), returnDate);
        if (daysLate < 0) {
            daysLate = 0;
        }

        double penalty = LoanPolicy.calculatePenalty(daysLate);
        Loan returnedLoan = loanRepository.save(loan.withReturn(returnDate, penalty));

        boolean majorDelayRecorded = false;
        if (LoanPolicy.isMajorDelay(daysLate)) {
            reservationRepository.saveMajorDelay(new MajorDelayEvent(loan.memberId(), returnDate, daysLate));
            majorDelayRecorded = true;
            updateSuspensionStatus(loan.memberId(), returnDate);
        }

        reservationService.onWorkReturned(loan.workId(), returnDate);

        return new ReturnResult.Completed(returnedLoan, daysLate, majorDelayRecorded);
    }

    public boolean isMemberSuspended(String memberId) {
        return memberRepository.findById(memberId)
                .map(Member::suspended)
                .orElse(false);
    }

    public boolean isWorkOnLoan(String workId) {
        return loanRepository.findActiveByWorkId(workId).isPresent();
    }

    private void updateSuspensionStatus(String memberId, LocalDate referenceDate) {
        LocalDate since = referenceDate.minusYears(1);
        long majorDelays = reservationRepository.countMajorDelaysForMemberSince(memberId, since);

        if (majorDelays >= SUSPENSION_THRESHOLD) {
            Member member = memberRepository.findById(memberId).orElseThrow();
            memberRepository.save(new Member(member.id(), member.name(), true));
        }
    }
}
