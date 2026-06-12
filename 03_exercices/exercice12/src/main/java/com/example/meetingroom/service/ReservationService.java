package com.example.meetingroom.service;

import com.example.meetingroom.exception.AlreadyCancelledException;
import com.example.meetingroom.exception.ReservationConflictException;
import com.example.meetingroom.exception.ReservationNotFoundException;
import com.example.meetingroom.exception.RoomNotFoundException;
import com.example.meetingroom.model.Reservation;
import com.example.meetingroom.model.ReservationStatus;
import com.example.meetingroom.repository.ReservationRepository;
import com.example.meetingroom.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public Reservation create(Long roomId, String reserverName, LocalDateTime startTime, LocalDateTime endTime) {
        // Vérifier que la salle existe
        roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        if (reserverName == null || reserverName.isBlank()) {
            throw new IllegalArgumentException("Le nom de la personne qui réserve est obligatoire");
        }

        // La fin doit être strictement après le début
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("La date/heure de fin doit être strictement après la date/heure de début");
        }

        // Vérifier qu'il n'y a pas de chevauchement avec une réservation confirmée
        boolean hasOverlap = reservationRepository.findConfirmedByRoomId(roomId)
                .stream()
                .anyMatch(existing -> overlaps(existing.startTime(), existing.endTime(), startTime, endTime));

        if (hasOverlap) {
            throw new ReservationConflictException("Le créneau chevauche une réservation existante pour cette salle");
        }

        return reservationRepository.save(roomId, reserverName.trim(), startTime, endTime, ReservationStatus.CONFIRMED);
    }

    public Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    public Reservation cancel(Long id) {
        Reservation reservation = getById(id);

        if (reservation.status() == ReservationStatus.CANCELLED) {
            throw new AlreadyCancelledException(id);
        }

        return reservationRepository.updateStatus(id, ReservationStatus.CANCELLED);
    }

    public void deleteAll() {
        reservationRepository.deleteAll();
    }

    // Deux créneaux se chevauchent si l'un commence avant la fin de l'autre
    private boolean overlaps(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
