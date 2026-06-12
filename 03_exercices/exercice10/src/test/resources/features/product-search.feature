Feature: Product search

  Scenario: Search by keyword
    Given the search bar is accessible
    And a product "PROD-001" named "Wireless Headset" priced at 79.99 euros in category "Electronics"
    And a product "PROD-002" named "Coffee Mug" priced at 12.5 euros in category "Kitchen"
    When the user searches for keyword "headset"
    Then the search results contain product "PROD-001"
    And the search results contain 1 product(s)
    And the product repository should have been consulted for keyword "headset"

  Scenario: Search by maximum price
    Given the search bar is accessible
    And a product "PROD-003" named "Notebook" priced at 5.0 euros in category "Stationery"
    And a product "PROD-004" named "Monitor" priced at 250.0 euros in category "Electronics"
    When the user searches for products with a maximum price of 50.0 euros
    Then the search results contain product "PROD-003"
    And the search results contain 1 product(s)
    And the product repository should have been consulted for max price 50.0
