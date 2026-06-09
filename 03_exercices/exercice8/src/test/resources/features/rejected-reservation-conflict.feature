Feature: Rejected reservation for booking conflict

  Scenario: Rejection when the room is already reserved for the requested slot
    Given a room "ROOM-E" named "Room E" with a capacity of 12
    And an existing reservation for room "ROOM-E" from "2025-06-10 09:00" to "2025-06-10 10:00"
    When user "user@example.com" reserves room "ROOM-E" for 4 participants from "2025-06-10 09:30" to "2025-06-10 10:30"
    Then the reservation is rejected
    And the rejection reason is "Reservation conflict"
