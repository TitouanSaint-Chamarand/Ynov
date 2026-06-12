Feature: Order checkout

  Scenario: Successful order validation
    Given the checkout form is accessible for order "ORDER-100"
    When the user validates order "ORDER-100"
    Then the order is confirmed with message "Order confirmed"
    And the order repository should have been consulted for id "ORDER-100"

  Scenario: Checkout fails for non-existent order
    Given the checkout form is accessible
    And no order exists with id "ORDER-404"
    When the user validates order "ORDER-404"
    Then checkout fails with message "Order not found"
