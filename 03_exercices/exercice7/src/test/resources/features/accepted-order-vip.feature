Feature: Accepted order for a VIP customer

  Scenario: Accepted order with a 20 percent discount
    Given a VIP customer with email "vip@example.com"
    And a product "REF-003" named "Keyboard" priced at 80.0 euros with a stock of 15
    When the customer places an order for 5 units of product "REF-003"
    Then the order is accepted
    And the receipt contains reference "REF-003"
    And the receipt contains quantity 5
    And the total amount is 320.0
    And the receipt contains message "Order confirmed"
    And the product repository should have been consulted for reference "REF-003"
