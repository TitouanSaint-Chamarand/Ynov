package com.example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RoomReservationService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;

    public RoomReservationService(
            RoomRepository roomRepository,
            ReservationRepository reservationRepository,
            NotificationService notificationService
    ) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.notificationService = notificationService;
    }

    public ReservationResult reserve(
            String userEmail,
            String roomCode,
            int participants,
            LocalDateTime start,
            LocalDateTime end
    ) {
        Optional<Room> roomOptional = roomRepository.findByCode(roomCode);

        if (roomOptional.isEmpty()) {
            return new ReservationResult.Rejected("Unknown room");
        }

        Room room = roomOptional.get();

        if (participants > room.maxCapacity()) {
            return new ReservationResult.Rejected("Insufficient capacity");
        }

        if (!end.isAfter(start)) {
            return new ReservationResult.Rejected("Invalid period");
        }

        List<Reservation> existingReservations = reservationRepository.findByRoomCode(roomCode);
        for (Reservation existing : existingReservations) {
            if (overlaps(start, end, existing.start(), existing.end())) {
                return new ReservationResult.Rejected("Reservation conflict");
            }
        }

        Reservation reservation = new Reservation(userEmail, roomCode, participants, start, end);
        reservationRepository.save(reservation);
        notificationService.sendConfirmation(userEmail, roomCode, start, end);

        return new ReservationResult.Accepted(reservation);
    }

    private boolean overlaps(
            LocalDateTime start,
            LocalDateTime end,
            LocalDateTime otherStart,
            LocalDateTime otherEnd
    ) {
        return start.isBefore(otherEnd) && otherStart.isBefore(end);
    }
}
