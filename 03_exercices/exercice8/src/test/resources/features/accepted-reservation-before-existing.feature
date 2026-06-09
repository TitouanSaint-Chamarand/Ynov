Feature: Accepted reservation before an existing booking

  Scenario: Acceptance when the new slot ends before an existing reservation starts
    Given a room "ROOM-I" named "Room I" with a capacity of 10
    And an existing reservation for room "ROOM-I" from "2025-06-10 09:00" to "2025-06-10 10:00"
    When user "user@example.com" reserves room "ROOM-I" for 3 participants from "2025-06-10 08:00" to "2025-06-10 08:30"
    Then the reservation is accepted
