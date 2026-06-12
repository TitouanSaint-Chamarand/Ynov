package com.example.meetingroom.repository;

import com.example.meetingroom.model.Reservation;
import com.example.meetingroom.model.ReservationStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryReservationRepository implements ReservationRepository {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, Reservation> reservations = new ConcurrentHashMap<>();

    @Override
    public Reservation save(Long roomId, String reserverName, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status) {
        Long id = sequence.incrementAndGet();
        Reservation reservation = new Reservation(id, roomId, reserverName, startTime, endTime, status);
        reservations.put(id, reservation);
        return reservation;
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(reservations.get(id));
    }

    @Override
    public List<Reservation> findConfirmedByRoomId(Long roomId) {
        return reservations.values()
                .stream()
                .filter(reservation -> reservation.roomId().equals(roomId))
                .filter(reservation -> reservation.status() == ReservationStatus.CONFIRMED)
                .toList();
    }

    @Override
    public Reservation updateStatus(Long id, ReservationStatus status) {
        Reservation current = reservations.get(id);
        Reservation updated = new Reservation(
                current.id(),
                current.roomId(),
                current.reserverName(),
                current.startTime(),
                current.endTime(),
                status
        );
        reservations.put(id, updated);
        return updated;
    }

    @Override
    public void deleteAll() {
        reservations.clear();
        sequence.set(0);
    }
}
