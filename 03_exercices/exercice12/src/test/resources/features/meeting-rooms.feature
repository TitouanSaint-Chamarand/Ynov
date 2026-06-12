Feature: Gestion des reservations de salles
  En tant qu'utilisateur,
  je veux reserver une salle de reunion
  afin d'organiser mes reunions.

  Scenario: Reservation acceptee quand la salle existe et le creneau est libre
    Given aucune salle ni reservation n existe dans l API
    And une salle existe avec le nom "Salle A" et la capacite 10
    When je cree une reservation pour "Alice" de "2026-06-12T10:00:00" a "2026-06-12T11:00:00"
    Then la reponse HTTP doit etre 201
    And la reservation est confirmee pour "Alice"

  Scenario: Reservation refusee quand la salle n existe pas
    Given aucune salle ni reservation n existe dans l API
    When je cree une reservation pour une salle inexistante
    Then la reponse HTTP doit etre 404
    And la reponse contient un message d erreur

  Scenario: Reservation refusee quand le creneau chevauche une reservation existante
    Given aucune salle ni reservation n existe dans l API
    And une salle existe avec le nom "Salle B" et la capacite 6
    And une reservation confirmee existe pour la salle de "2026-06-12T10:00:00" a "2026-06-12T11:00:00"
    When je cree une reservation pour "Alice" de "2026-06-12T10:30:00" a "2026-06-12T11:30:00"
    Then la reponse HTTP doit etre 409
    And la reponse contient un message d erreur
