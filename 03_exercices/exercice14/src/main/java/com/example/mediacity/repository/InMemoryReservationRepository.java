package com.example.mediacity.repository;

import com.example.mediacity.model.MajorDelayEvent;
import com.example.mediacity.model.Reservation;
import com.example.mediacity.model.ReservationStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryReservationRepository implements ReservationRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<MajorDelayEvent> majorDelays = new ArrayList<>();

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation.id() == 0) {
            Reservation created = new Reservation(
                    sequence.getAndIncrement(),
                    reservation.memberId(),
                    reservation.workId(),
                    reservation.createdAt(),
                    reservation.status()
            );
            reservations.add(created);
            return created;
        }

        reservations.removeIf(existing -> existing.id() == reservation.id());
        reservations.add(reservation);
        return reservation;
    }

    @Override
    public Optional<Reservation> findById(long id) {
        return reservations.stream().filter(reservation -> reservation.id() == id).findFirst();
    }

    @Override
    public List<Reservation> findPendingByWorkId(String workId) {
        return reservations.stream()
                .filter(reservation -> reservation.workId().equals(workId)
                        && reservation.status() == ReservationStatus.PENDING)
                .sorted(Comparator.comparing(Reservation::createdAt).thenComparingLong(Reservation::id))
                .toList();
    }

    @Override
    public Optional<Reservation> findReadyByWorkId(String workId) {
        return reservations.stream()
                .filter(reservation -> reservation.workId().equals(workId)
                        && reservation.status() == ReservationStatus.READY)
                .findFirst();
    }

    @Override
    public List<Reservation> findByMemberId(String memberId) {
        return reservations.stream()
                .filter(reservation -> reservation.memberId().equals(memberId))
                .toList();
    }

    @Override
    public void saveMajorDelay(MajorDelayEvent event) {
        majorDelays.add(event);
    }

    @Override
    public long countMajorDelaysForMemberSince(String memberId, LocalDate since) {
        return majorDelays.stream()
                .filter(event -> event.memberId().equals(memberId))
                .filter(event -> !event.returnDate().isBefore(since))
                .count();
    }
}
