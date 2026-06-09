package com.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationSteps {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private RoomRepository roomRepository;
    private ReservationRepository reservationRepository;
    private NotificationService notificationService;
    private RoomReservationService roomReservationService;
    private ReservationResult reservationResult;
    private final Map<String, List<Reservation>> reservationsByRoom = new HashMap<>();

    @Given("a room {string} named {string} with a capacity of {int}")
    public void aRoomNamedWithACapacityOf(String code, String name, int maxCapacity) {
        initMocks();
        Room room = new Room(code, name, maxCapacity);
        when(roomRepository.findByCode(code)).thenReturn(Optional.of(room));
    }

    @Given("no room exists with code {string}")
    public void noRoomExistsWithCode(String code) {
        initMocks();
        when(roomRepository.findByCode(code)).thenReturn(Optional.empty());
    }

    @Given("no existing reservation for room {string}")
    public void noExistingReservationForRoom(String roomCode) {
        reservationsByRoom.put(roomCode, new ArrayList<>());
        when(reservationRepository.findByRoomCode(roomCode)).thenAnswer(invocation -> {
            String code = invocation.getArgument(0);
            return List.copyOf(reservationsByRoom.getOrDefault(code, List.of()));
        });
    }

    @Given("an existing reservation for room {string} from {string} to {string}")
    public void anExistingReservationForRoomFromTo(String roomCode, String start, String end) {
        LocalDateTime startDateTime = parseDateTime(start);
        LocalDateTime endDateTime = parseDateTime(end);
        Reservation existing = new Reservation("existing@example.com", roomCode, 1, startDateTime, endDateTime);

        reservationsByRoom.computeIfAbsent(roomCode, key -> new ArrayList<>()).add(existing);
        when(reservationRepository.findByRoomCode(roomCode)).thenAnswer(invocation -> {
            String code = invocation.getArgument(0);
            return List.copyOf(reservationsByRoom.getOrDefault(code, List.of()));
        });
    }

    @When("user {string} reserves room {string} for {int} participants from {string} to {string}")
    public void userReservesRoomForParticipantsFromTo(
            String userEmail,
            String roomCode,
            int participants,
            String start,
            String end
    ) {
        reservationResult = roomReservationService.reserve(
                userEmail,
                roomCode,
                participants,
                parseDateTime(start),
                parseDateTime(end)
        );
    }

    @Then("the reservation is accepted")
    public void theReservationIsAccepted() {
        assertInstanceOf(ReservationResult.Accepted.class, reservationResult);
    }

    @Then("the reservation is rejected")
    public void theReservationIsRejected() {
        assertInstanceOf(ReservationResult.Rejected.class, reservationResult);
    }

    @Then("the rejection reason is {string}")
    public void theRejectionReasonIs(String expectedReason) {
        ReservationResult.Rejected rejected = (ReservationResult.Rejected) reservationResult;
        assertEquals(expectedReason, rejected.reason());
    }

    @Then("a confirmation is sent to {string} for room {string}")
    public void aConfirmationIsSentToForRoom(String userEmail, String roomCode) {
        verify(notificationService).sendConfirmation(
                eq(userEmail),
                eq(roomCode),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }

    @Then("no confirmation is sent")
    public void noConfirmationIsSent() {
        verify(notificationService, never()).sendConfirmation(
                anyString(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }

    @Then("the room repository should have been consulted for code {string}")
    public void theRoomRepositoryShouldHaveBeenConsultedForCode(String roomCode) {
        verify(roomRepository).findByCode(roomCode);
    }

    private void initMocks() {
        roomRepository = mock(RoomRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        notificationService = mock(NotificationService.class);
        roomReservationService = new RoomReservationService(
                roomRepository,
                reservationRepository,
                notificationService
        );
        reservationsByRoom.clear();

        when(reservationRepository.findByRoomCode(anyString())).thenReturn(List.of());
        doAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservationsByRoom
                    .computeIfAbsent(reservation.roomCode(), key -> new ArrayList<>())
                    .add(reservation);
            return null;
        }).when(reservationRepository).save(any(Reservation.class));
    }

    private LocalDateTime parseDateTime(String value) {
        return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }
}
