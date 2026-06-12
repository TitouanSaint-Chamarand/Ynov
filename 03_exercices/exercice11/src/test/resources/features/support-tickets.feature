Feature: Gestion des tickets de support
  En tant qu'utilisateur du service support,
  je veux pouvoir creer et suivre des tickets
  afin de traiter les demandes d'assistance.

  Scenario: Creation d un ticket valide
    Given aucun ticket n existe dans l API
    When je cree un ticket avec le titre "Imprimante en panne" et la priorite "MEDIUM"
    Then la reponse HTTP doit etre 201
    And la reponse contient le titre "Imprimante en panne"
    And la reponse contient le statut "OPEN"

  Scenario: Resolution d un ticket
    Given aucun ticket n existe dans l API
    And un ticket existe avec le titre "Bug critique" et la priorite "HIGH"
    When je modifie le statut du ticket cree vers "IN_PROGRESS"
    And je modifie le statut du ticket cree vers "RESOLVED"
    Then la reponse HTTP doit etre 200
    And la reponse contient le statut "RESOLVED"

  Scenario: Refus de modification d un ticket deja resolu
    Given aucun ticket n existe dans l API
    And un ticket resolu existe avec le titre "Ticket clos"
    When je modifie le statut du ticket cree vers "IN_PROGRESS"
    Then la reponse HTTP doit etre 409
    And la reponse contient un message d erreur

  Scenario: Consultation d un ticket inexistant
    Given aucun ticket n existe dans l API
    When je demande le ticket avec l identifiant 42
    Then la reponse HTTP doit etre 404
    And la reponse contient un message d erreur
