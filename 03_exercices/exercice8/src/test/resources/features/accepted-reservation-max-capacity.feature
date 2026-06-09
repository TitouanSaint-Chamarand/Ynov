Feature: Accepted reservation at maximum capacity

  Scenario: Accepted reservation with the exact number of allowed participants
    Given a room "ROOM-B" named "Meeting B" with a capacity of 8
    And no existing reservation for room "ROOM-B"
    When user "max@example.com" reserves room "ROOM-B" for 8 participants from "2025-06-10 14:00" to "2025-06-10 15:00"
    Then the reservation is accepted
