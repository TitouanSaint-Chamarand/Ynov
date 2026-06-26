Feature: Règle métier complémentaire

  Scenario: Un adhérent ne peut pas emprunter un ouvrage réservé pour un autre
    Given un adhérent "M1" nommé "Alice"
    And un adhérent "M2" nommé "Bob"
    And un adhérent "M3" nommé "Charlie"
    And un ouvrage "W1" intitulé "L'Étranger"
    And l'ouvrage "W1" est emprunté par "M1" depuis le "2026-06-01"
    And "M2" réserve l'ouvrage "W1" le "2026-06-02"
    When l'ouvrage "W1" est restitué le "2026-06-10"
    And "M3" emprunte l'ouvrage "W1" le "2026-06-11"
    Then l'emprunt est refusé
