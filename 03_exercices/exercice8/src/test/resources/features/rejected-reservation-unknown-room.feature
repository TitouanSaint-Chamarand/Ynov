Feature: Rejected reservation for unknown room

  Scenario: Rejection when the room does not exist
    Given no room exists with code "ROOM-UNKNOWN"
    And no existing reservation for room "ROOM-UNKNOWN"
    When user "user@example.com" reserves room "ROOM-UNKNOWN" for 3 participants from "2025-06-10 09:00" to "2025-06-10 10:00"
    Then the reservation is rejected
    And the rejection reason is "Unknown room"
    And the room repository should have been consulted for code "ROOM-UNKNOWN"
