package com.example.mediacity.service;

import com.example.mediacity.model.LoanResult;
import com.example.mediacity.model.Reservation;
import com.example.mediacity.model.ReservationResult;
import com.example.mediacity.model.ReturnResult;
import com.example.mediacity.repository.InMemoryLoanRepository;
import com.example.mediacity.repository.InMemoryMemberRepository;
import com.example.mediacity.repository.InMemoryReservationRepository;
import com.example.mediacity.repository.InMemoryWorkRepository;
import com.example.mediacity.repository.LoanRepository;
import com.example.mediacity.repository.MemberRepository;
import com.example.mediacity.repository.ReservationRepository;
import com.example.mediacity.repository.WorkRepository;
import com.example.mediacity.model.Member;
import com.example.mediacity.model.Work;

import java.time.LocalDate;
import java.util.List;

public class MediaCityService {

    private final LoanService loanService;
    private final ReservationService reservationService;
    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final WorkRepository workRepository;

    public MediaCityService(
            LoanService loanService,
            ReservationService reservationService,
            LoanRepository loanRepository,
            MemberRepository memberRepository,
            WorkRepository workRepository
    ) {
        this.loanService = loanService;
        this.reservationService = reservationService;
        this.loanRepository = loanRepository;
        this.memberRepository = memberRepository;
        this.workRepository = workRepository;
    }

    public static MediaCityService createInMemory() {
        LoanRepository loanRepository = new InMemoryLoanRepository();
        MemberRepository memberRepository = new InMemoryMemberRepository();
        WorkRepository workRepository = new InMemoryWorkRepository();
        ReservationRepository reservationRepository = new InMemoryReservationRepository();

        ReservationService reservationService = new ReservationService(
                reservationRepository,
                memberRepository,
                workRepository,
                loanRepository
        );
        LoanService loanService = new LoanService(
                loanRepository,
                memberRepository,
                workRepository,
                reservationRepository,
                reservationService
        );

        return new MediaCityService(
                loanService,
                reservationService,
                loanRepository,
                memberRepository,
                workRepository
        );
    }

    public void registerMember(String memberId, String name) {
        memberRepository.save(new Member(memberId, name, false));
    }

    public void registerWork(String workId, String title) {
        workRepository.save(new Work(workId, title));
    }

    public LoanResult borrow(String memberId, String workId, LocalDate loanDate) {
        return loanService.borrow(memberId, workId, loanDate);
    }

    public ReturnResult returnWork(long loanId, LocalDate returnDate) {
        return loanService.returnWork(loanId, returnDate);
    }

    public ReservationResult reserve(String memberId, String workId, LocalDate reservationDate) {
        return reservationService.reserve(memberId, workId, reservationDate);
    }

    public boolean isMemberSuspended(String memberId) {
        return loanService.isMemberSuspended(memberId);
    }

    public boolean isWorkOnLoan(String workId) {
        return loanService.isWorkOnLoan(workId);
    }

    public String getHeldForMember(String workId) {
        return reservationService.getHeldForMember(workId);
    }

    public List<Reservation> getPendingReservations(String workId) {
        return reservationService.getPendingReservations(workId);
    }

    public long getActiveLoanId(String workId) {
        return loanRepository.findActiveByWorkId(workId)
                .map(loan -> loan.id())
                .orElseThrow(() -> new IllegalStateException("Aucun prêt actif pour l'ouvrage " + workId));
    }
}
