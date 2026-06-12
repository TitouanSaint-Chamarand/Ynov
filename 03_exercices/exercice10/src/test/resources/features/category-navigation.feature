Feature: Category navigation

  Scenario: Browse products by category
    Given the categories page is accessible
    And a product "PROD-010" named "T-Shirt" priced at 25.0 euros in category "Clothing"
    And a product "PROD-011" named "Jeans" priced at 60.0 euros in category "Clothing"
    And a product "PROD-012" named "Blender" priced at 45.0 euros in category "Kitchen"
    When the user selects category "Clothing"
    Then the search results contain product "PROD-010"
    And the search results contain product "PROD-011"
    And the search results contain 2 product(s)
    And the product repository should have been consulted for category "Clothing"
