Feature: Plusieurs réservations sur le même ouvrage

  Scenario: Deux adhérents réservent le même ouvrage emprunté
    Given un adhérent "M1" nommé "Alice"
    And un adhérent "M2" nommé "Bob"
    And un adhérent "M3" nommé "Charlie"
    And un ouvrage "W1" intitulé "1984"
    And l'ouvrage "W1" est emprunté par "M1" depuis le "2026-06-01"
    When "M2" réserve l'ouvrage "W1" le "2026-06-02"
    And "M3" réserve l'ouvrage "W1" le "2026-06-03"
    Then la réservation est acceptée
    And il y a 2 réservation en attente pour l'ouvrage "W1"
