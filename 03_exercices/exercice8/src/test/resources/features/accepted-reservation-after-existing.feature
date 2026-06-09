Feature: Accepted reservation after an existing booking

  Scenario: Acceptance when the new slot starts after an existing reservation
    Given a room "ROOM-F" named "Room F" with a capacity of 10
    And an existing reservation for room "ROOM-F" from "2025-06-10 09:00" to "2025-06-10 10:00"
    When user "user@example.com" reserves room "ROOM-F" for 5 participants from "2025-06-10 10:00" to "2025-06-10 11:00"
    Then the reservation is accepted
