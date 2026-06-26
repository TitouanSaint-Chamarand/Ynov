Feature: Réservation d'un ouvrage indisponible

  Scenario: Un adhérent réserve un ouvrage déjà emprunté
    Given un adhérent "M1" nommé "Alice"
    And un adhérent "M2" nommé "Bob"
    And un ouvrage "W1" intitulé "Le Petit Prince"
    And l'ouvrage "W1" est emprunté par "M1" depuis le "2026-06-01"
    When "M2" réserve l'ouvrage "W1" le "2026-06-02"
    Then la réservation est acceptée
