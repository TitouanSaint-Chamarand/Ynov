Feature: Accepted reservation

  Scenario: Accepted reservation for an available room
    Given a room "ROOM-A" named "Meeting A" with a capacity of 10
    And no existing reservation for room "ROOM-A"
    When user "user@example.com" reserves room "ROOM-A" for 5 participants from "2025-06-10 09:00" to "2025-06-10 10:00"
    Then the reservation is accepted
    And the room repository should have been consulted for code "ROOM-A"
