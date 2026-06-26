Feature: Refus de réservation pour adhérent suspendu

  Scenario: Un adhérent suspendu ne peut pas réserver un ouvrage
    Given un adhérent "M1" nommé "Alice"
    And un adhérent suspendu "M2" nommé "Bob"
    And un ouvrage "W1" intitulé "Les Misérables"
    And l'ouvrage "W1" est emprunté par "M1" depuis le "2026-06-01"
    When "M2" réserve l'ouvrage "W1" le "2026-06-02"
    Then la réservation est refusée
    And le motif de refus est "Adhérent suspendu"
