package com.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderSteps {
    private ProductRepository productRepository;
    private CustomerRepository customerRepository;
    private OrderService orderService;
    private OrderResult orderResult;
    private String customerEmail;

    @Given("a STANDARD customer with email {string}")
    public void aStandardCustomerWithEmail(String email) {
        initMocks();
        customerEmail = email;
        when(customerRepository.getProfileByEmail(email)).thenReturn(ClientProfile.STANDARD);
    }

    @Given("a PREMIUM customer with email {string}")
    public void aPremiumCustomerWithEmail(String email) {
        initMocks();
        customerEmail = email;
        when(customerRepository.getProfileByEmail(email)).thenReturn(ClientProfile.PREMIUM);
    }

    @Given("a VIP customer with email {string}")
    public void aVipCustomerWithEmail(String email) {
        initMocks();
        customerEmail = email;
        when(customerRepository.getProfileByEmail(email)).thenReturn(ClientProfile.VIP);
    }

    @Given("a product {string} named {string} priced at {double} euros with a stock of {int}")
    public void aProductNamedPricedAtEurosWithAStockOf(
            String reference,
            String name,
            double unitPrice,
            int stock
    ) {
        Product product = new Product(reference, name, unitPrice, stock);
        when(productRepository.findByReference(reference)).thenReturn(Optional.of(product));
    }

    @Given("no product exists with reference {string}")
    public void noProductExistsWithReference(String reference) {
        when(productRepository.findByReference(reference)).thenReturn(Optional.empty());
    }

    @When("the customer places an order for {int} units of product {string}")
    public void theCustomerPlacesAnOrderForUnitsOfProduct(int quantity, String productReference) {
        orderResult = orderService.placeOrder(customerEmail, productReference, quantity);
    }

    @Then("the order is accepted")
    public void theOrderIsAccepted() {
        assertInstanceOf(OrderResult.Accepted.class, orderResult);
    }

    @Then("the order is rejected")
    public void theOrderIsRejected() {
        assertInstanceOf(OrderResult.Rejected.class, orderResult);
    }

    @Then("the receipt contains reference {string}")
    public void theReceiptContainsReference(String expectedReference) {
        OrderReceipt receipt = getAcceptedReceipt();
        assertEquals(expectedReference, receipt.productReference());
    }

    @Then("the receipt contains quantity {int}")
    public void theReceiptContainsQuantity(int expectedQuantity) {
        OrderReceipt receipt = getAcceptedReceipt();
        assertEquals(expectedQuantity, receipt.quantity());
    }

    @Then("the total amount is {double}")
    public void theTotalAmountIs(double expectedTotal) {
        OrderReceipt receipt = getAcceptedReceipt();
        assertEquals(expectedTotal, receipt.totalAmount(), 0.001);
    }

    @Then("the receipt contains message {string}")
    public void theReceiptContainsMessage(String expectedMessage) {
        OrderReceipt receipt = getAcceptedReceipt();
        assertEquals(expectedMessage, receipt.confirmationMessage());
    }

    @Then("the rejection reason is {string}")
    public void theRejectionReasonIs(String expectedReason) {
        OrderResult.Rejected rejected = (OrderResult.Rejected) orderResult;
        assertEquals(expectedReason, rejected.reason());
    }

    @Then("the product repository should have been consulted for reference {string}")
    public void theProductRepositoryShouldHaveBeenConsultedForReference(String reference) {
        verify(productRepository).findByReference(reference);
    }

    private void initMocks() {
        productRepository = mock(ProductRepository.class);
        customerRepository = mock(CustomerRepository.class);
        orderService = new OrderService(productRepository, customerRepository);
    }

    private OrderReceipt getAcceptedReceipt() {
        assertInstanceOf(OrderResult.Accepted.class, orderResult);
        return ((OrderResult.Accepted) orderResult).receipt();
    }
}
