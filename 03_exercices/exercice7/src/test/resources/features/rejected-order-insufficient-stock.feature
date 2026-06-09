Feature: Rejected order for insufficient stock

  Scenario: Rejection when quantity exceeds available stock
    Given a STANDARD customer with email "client@example.com"
    And a product "REF-004" named "Monitor" priced at 300.0 euros with a stock of 3
    When the customer places an order for 5 units of product "REF-004"
    Then the order is rejected
    And the rejection reason is "Insufficient stock"
    And the product repository should have been consulted for reference "REF-004"
