Feature: Account registration

  Scenario: Successful registration
    Given the registration form is accessible
    And no account exists with username "alice"
    When the user registers with email "alice@example.com", username "alice" and password "secret123"
    Then registration is confirmed with message "Account created successfully"

  Scenario: Registration rejected for existing username
    Given the registration form is accessible
    And an account already exists with username "bob"
    When the user registers with email "bob2@example.com", username "bob" and password "secret123"
    Then registration is rejected with message "Username already exists"
