Feature: User login

  Scenario: Successful login
    Given the login form is accessible
    And an account exists with username "carol" and password "mypassword"
    When the user logs in with username "carol" and password "mypassword"
    Then the user is redirected to the home page

  Scenario: Failed login with invalid credentials
    Given the login form is accessible
    And an account exists with username "dave" and password "correct"
    When the user logs in with username "dave" and password "wrong"
    Then login fails with message "Invalid credentials"
