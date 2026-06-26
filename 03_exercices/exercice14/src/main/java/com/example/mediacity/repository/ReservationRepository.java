package com.example.mediacity.repository;

import com.example.mediacity.model.MajorDelayEvent;
import com.example.mediacity.model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(long id);

    List<Reservation> findPendingByWorkId(String workId);

    Optional<Reservation> findReadyByWorkId(String workId);

    List<Reservation> findByMemberId(String memberId);

    void saveMajorDelay(MajorDelayEvent event);

    long countMajorDelaysForMemberSince(String memberId, LocalDate since);
}
