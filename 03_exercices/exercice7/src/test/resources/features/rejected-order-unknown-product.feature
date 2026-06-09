Feature: Rejected order for unknown product

  Scenario: Rejection when the product does not exist
    Given a STANDARD customer with email "client@example.com"
    And no product exists with reference "REF-UNKNOWN"
    When the customer places an order for 1 units of product "REF-UNKNOWN"
    Then the order is rejected
    And the rejection reason is "Unknown product"
    And the product repository should have been consulted for reference "REF-UNKNOWN"
