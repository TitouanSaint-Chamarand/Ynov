Feature: Accepted order for a PREMIUM customer

  Scenario: Accepted order with a 10 percent discount
    Given a PREMIUM customer with email "premium@example.com"
    And a product "REF-002" named "Mouse" priced at 50.0 euros with a stock of 20
    When the customer places an order for 4 units of product "REF-002"
    Then the order is accepted
    And the receipt contains reference "REF-002"
    And the receipt contains quantity 4
    And the total amount is 180.0
    And the receipt contains message "Order confirmed"
    And the product repository should have been consulted for reference "REF-002"
