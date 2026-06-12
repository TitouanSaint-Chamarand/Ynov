package com.example.meetingroom.repository;

import com.example.meetingroom.model.Reservation;
import com.example.meetingroom.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Reservation save(Long roomId, String reserverName, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status);

    Optional<Reservation> findById(Long id);

    List<Reservation> findConfirmedByRoomId(Long roomId);

    Reservation updateStatus(Long id, ReservationStatus status);

    void deleteAll();
}
