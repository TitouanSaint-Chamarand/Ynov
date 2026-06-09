Feature: Notification sent on success

  Scenario: A confirmation is sent when the reservation is accepted
    Given a room "ROOM-G" named "Room G" with a capacity of 6
    And no existing reservation for room "ROOM-G"
    When user "notify@example.com" reserves room "ROOM-G" for 2 participants from "2025-06-10 16:00" to "2025-06-10 17:00"
    Then the reservation is accepted
    And a confirmation is sent to "notify@example.com" for room "ROOM-G"
