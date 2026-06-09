Feature: Rejected reservation for insufficient capacity

  Scenario: Rejection when the number of participants exceeds capacity
    Given a room "ROOM-C" named "Small room" with a capacity of 4
    And no existing reservation for room "ROOM-C"
    When user "user@example.com" reserves room "ROOM-C" for 6 participants from "2025-06-10 11:00" to "2025-06-10 12:00"
    Then the reservation is rejected
    And the rejection reason is "Insufficient capacity"
