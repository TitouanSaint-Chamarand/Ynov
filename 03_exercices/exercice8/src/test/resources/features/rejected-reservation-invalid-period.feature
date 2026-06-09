Feature: Rejected reservation for invalid period

  Scenario: Rejection when the end date is before the start date
    Given a room "ROOM-D" named "Room D" with a capacity of 10
    And no existing reservation for room "ROOM-D"
    When user "user@example.com" reserves room "ROOM-D" for 3 participants from "2025-06-10 15:00" to "2025-06-10 14:00"
    Then the reservation is rejected
    And the rejection reason is "Invalid period"
