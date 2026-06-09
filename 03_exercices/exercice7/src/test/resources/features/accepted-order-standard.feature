Feature: Accepted order for a STANDARD customer

  Scenario: Accepted order with no discount
    Given a STANDARD customer with email "standard@example.com"
    And a product "REF-001" named "Headset" priced at 100.0 euros with a stock of 10
    When the customer places an order for 2 units of product "REF-001"
    Then the order is accepted
    And the receipt contains reference "REF-001"
    And the receipt contains quantity 2
    And the total amount is 200.0
    And the receipt contains message "Order confirmed"
    And the product repository should have been consulted for reference "REF-001"
