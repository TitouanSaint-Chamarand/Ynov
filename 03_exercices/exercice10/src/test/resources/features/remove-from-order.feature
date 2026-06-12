Feature: Remove product from order

  Scenario: Decrease quantity when more than one unit is present
    Given an order "ORDER-010" exists
    And a product "PROD-030" named "USB Cable" priced at 9.99 euros in category "Electronics"
    And order "ORDER-010" already contains 3 unit(s) of product "PROD-030"
    When the user removes product "PROD-030" from order "ORDER-010"
    Then the product is removed from the order with confirmation "Product removed from order"
    And order "ORDER-010" contains 2 unit(s) of product "PROD-030"

  Scenario: Remove product when only one unit remains
    Given an order "ORDER-011" exists
    And a product "PROD-031" named "Phone Case" priced at 14.99 euros in category "Accessories"
    And order "ORDER-011" already contains 1 unit(s) of product "PROD-031"
    When the user removes product "PROD-031" from order "ORDER-011"
    Then the product is removed from the order with confirmation "Product removed from order"
    And order "ORDER-011" does not contain product "PROD-031"

  Scenario: Remove product not present in the order
    Given an order "ORDER-012" exists
    And a product "PROD-032" named "Charger" priced at 19.99 euros in category "Electronics"
    When the user removes product "PROD-032" from order "ORDER-012"
    Then removing the product fails with message "Product not in order"

  Scenario: Remove product from a non-existent order
    Given a product "PROD-033" named "Adapter" priced at 11.99 euros in category "Electronics"
    And no order exists with id "ORDER-888"
    When the user removes product "PROD-033" from order "ORDER-888"
    Then removing the product fails with message "Order not found"
