package com.example.mediacity.service;

import com.example.mediacity.model.Member;
import com.example.mediacity.model.Reservation;
import com.example.mediacity.model.ReservationResult;
import com.example.mediacity.model.ReservationStatus;
import com.example.mediacity.model.Work;
import com.example.mediacity.repository.LoanRepository;
import com.example.mediacity.repository.MemberRepository;
import com.example.mediacity.repository.ReservationRepository;
import com.example.mediacity.repository.WorkRepository;

import java.time.LocalDate;
import java.util.List;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final WorkRepository workRepository;
    private final LoanRepository loanRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            MemberRepository memberRepository,
            WorkRepository workRepository,
            LoanRepository loanRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.workRepository = workRepository;
        this.loanRepository = loanRepository;
    }

    public ReservationResult reserve(String memberId, String workId, LocalDate reservationDate) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return new ReservationResult.Rejected("Adhérent inconnu");
        }

        if (member.suspended()) {
            return new ReservationResult.Rejected("Adhérent suspendu");
        }

        Work work = workRepository.findById(workId).orElse(null);
        if (work == null) {
            return new ReservationResult.Rejected("Ouvrage inconnu");
        }

        if (loanRepository.findActiveByWorkId(workId).isEmpty()) {
            return new ReservationResult.Rejected("L'ouvrage est disponible, la réservation n'est pas nécessaire");
        }

        boolean alreadyReserved = reservationRepository.findByMemberId(memberId).stream()
                .anyMatch(reservation -> reservation.workId().equals(workId)
                        && (reservation.status() == ReservationStatus.PENDING
                        || reservation.status() == ReservationStatus.READY));

        if (alreadyReserved) {
            return new ReservationResult.Rejected("Une réservation existe déjà pour cet adhérent sur cet ouvrage");
        }

        Reservation reservation = reservationRepository.save(
                new Reservation(0, memberId, workId, reservationDate, ReservationStatus.PENDING)
        );

        return new ReservationResult.Accepted(reservation);
    }

    public void onWorkReturned(String workId, LocalDate returnDate) {
        List<Reservation> pendingReservations = reservationRepository.findPendingByWorkId(workId);
        if (pendingReservations.isEmpty()) {
            return;
        }

        Reservation firstInQueue = pendingReservations.getFirst();
        reservationRepository.save(firstInQueue.withStatus(ReservationStatus.READY));

        Work work = workRepository.findById(workId).orElseThrow();
        workRepository.save(work.withHeldForMember(firstInQueue.memberId()));
    }

    public void fulfillReadyReservation(String workId, String memberId) {
        reservationRepository.findReadyByWorkId(workId)
                .filter(reservation -> reservation.memberId().equals(memberId))
                .ifPresent(reservation -> reservationRepository.save(reservation.withStatus(ReservationStatus.FULFILLED)));
    }

    public List<Reservation> getPendingReservations(String workId) {
        return reservationRepository.findPendingByWorkId(workId);
    }

    public String getHeldForMember(String workId) {
        return workRepository.findById(workId)
                .map(Work::heldForMemberId)
                .orElse(null);
    }
}
