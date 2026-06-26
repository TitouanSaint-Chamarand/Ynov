Feature: Restitution d'un ouvrage réservé

  Scenario: Le premier réservataire peut emprunter l'ouvrage après restitution
    Given un adhérent "M1" nommé "Alice"
    And un adhérent "M2" nommé "Bob"
    And un ouvrage "W1" intitulé "Dune"
    And l'ouvrage "W1" est emprunté par "M1" depuis le "2026-06-01"
    And "M2" réserve l'ouvrage "W1" le "2026-06-02"
    When l'ouvrage "W1" est restitué le "2026-06-10"
    Then l'ouvrage "W1" n'est plus emprunté
    And l'ouvrage "W1" est réservé pour "M2"
    When "M2" emprunte l'ouvrage "W1" le "2026-06-11"
    Then l'emprunt est accepté pour "M2"
