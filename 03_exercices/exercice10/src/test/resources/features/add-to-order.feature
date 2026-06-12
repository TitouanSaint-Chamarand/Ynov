Feature: Add product to order

  Scenario: Add a new product to an order
    Given an order "ORDER-001" exists
    And a product "PROD-020" named "Keyboard" priced at 49.99 euros in category "Electronics"
    When the user adds product "PROD-020" to order "ORDER-001"
    Then the product is added to the order with confirmation "Product added to order"
    And order "ORDER-001" contains 1 unit(s) of product "PROD-020"
    And the order repository should have been consulted for id "ORDER-001"

  Scenario: Increase quantity when product is already in the order
    Given an order "ORDER-002" exists
    And a product "PROD-021" named "Mouse" priced at 29.99 euros in category "Electronics"
    And order "ORDER-002" already contains 1 unit(s) of product "PROD-021"
    When the user adds product "PROD-021" to order "ORDER-002"
    Then the product is added to the order with confirmation "Product added to order"
    And order "ORDER-002" contains 2 unit(s) of product "PROD-021"

  Scenario: Add product to a non-existent order
    Given a product "PROD-022" named "Webcam" priced at 39.99 euros in category "Electronics"
    And no order exists with id "ORDER-999"
    When the user adds product "PROD-022" to order "ORDER-999"
    Then adding the product fails with message "Order not found"
