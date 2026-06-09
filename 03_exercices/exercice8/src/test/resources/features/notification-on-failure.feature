Feature: Notification not sent on failure

  Scenario: No confirmation is sent when the reservation is rejected
    Given a room "ROOM-H" named "Room H" with a capacity of 5
    And no existing reservation for room "ROOM-H"
    When user "notify@example.com" reserves room "ROOM-H" for 8 participants from "2025-06-10 09:00" to "2025-06-10 10:00"
    Then the reservation is rejected
    And the rejection reason is "Insufficient capacity"
    And no confirmation is sent
