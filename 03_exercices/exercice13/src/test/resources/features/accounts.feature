Feature: Gestion des comptes bancaires
  En tant qu'utilisateur,
  je veux gerer mes comptes bancaires
  afin de realiser des operations financieres.

  Scenario: Creation d'un nouveau compte
    Given aucun compte n existe dans l API
    When je cree un compte avec le numero "FR001" et le titulaire "Alice Martin"
    Then la reponse HTTP doit etre 201
    And le compte "FR001" a un solde de 0

  Scenario: Depot d'argent sur un compte
    Given aucun compte n existe dans l API
    And un compte existe avec le numero "FR001" et le titulaire "Alice Martin"
    When je depose 150 sur le compte "FR001"
    Then la reponse HTTP doit etre 200
    And le compte "FR001" a un solde de 150

  Scenario: Retrait avec fonds suffisants
    Given aucun compte n existe dans l API
    And un compte existe avec le numero "FR001" et le titulaire "Alice Martin"
    And le solde du compte "FR001" est initialise a 200
    When je retire 50 sur le compte "FR001"
    Then la reponse HTTP doit etre 200
    And le compte "FR001" a un solde de 150

  Scenario: Retrait avec fonds insuffisants
    Given aucun compte n existe dans l API
    And un compte existe avec le numero "FR001" et le titulaire "Alice Martin"
    And le solde du compte "FR001" est initialise a 30
    When je retire 100 sur le compte "FR001"
    Then la reponse HTTP doit etre 409
    And la reponse contient un message d erreur

  Scenario: Virement entre deux comptes
    Given aucun compte n existe dans l API
    And un compte existe avec le numero "FR001" et le titulaire "Alice Martin"
    And un compte existe avec le numero "FR002" et le titulaire "Bob Dupont"
    And le solde du compte "FR001" est initialise a 300
    And le solde du compte "FR002" est initialise a 50
    When je vire 100 du compte "FR001" vers le compte "FR002"
    Then la reponse HTTP doit etre 204
    And le compte "FR001" a un solde de 200
    And le compte "FR002" a un solde de 150

  Scenario: Virement refuse pour solde insuffisant
    Given aucun compte n existe dans l API
    And un compte existe avec le numero "FR001" et le titulaire "Alice Martin"
    And un compte existe avec le numero "FR002" et le titulaire "Bob Dupont"
    And le solde du compte "FR001" est initialise a 40
    When je vire 100 du compte "FR001" vers le compte "FR002"
    Then la reponse HTTP doit etre 409
    And la reponse contient un message d erreur
