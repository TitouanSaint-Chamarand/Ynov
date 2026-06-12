package com.example.steps;

import com.example.AuthResult;
import com.example.AuthService;
import com.example.CartOperationResult;
import com.example.CartService;
import com.example.CheckoutResult;
import com.example.Order;
import com.example.OrderItem;
import com.example.OrderRepository;
import com.example.Product;
import com.example.ProductRepository;
import com.example.ProductService;
import com.example.User;
import com.example.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShopSteps {
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private AuthService authService;
    private ProductService productService;
    private CartService cartService;

    private final Map<String, User> usersByUsername = new HashMap<>();
    private final Map<String, Order> ordersById = new HashMap<>();
    private final List<Product> catalog = new ArrayList<>();

    private Object lastResult;
    private List<Product> lastSearchResults;
    private boolean registrationFormAccessible;
    private boolean loginFormAccessible;
    private boolean searchBarAccessible;
    private boolean categoriesPageAccessible;
    private boolean checkoutFormAccessible;

    @Given("the registration form is accessible")
    public void theRegistrationFormIsAccessible() {
        initMocks();
        registrationFormAccessible = true;
    }

    @Given("no account exists with username {string}")
    public void noAccountExistsWithUsername(String username) {
        initMocks();
    }

    @Given("an account already exists with username {string}")
    public void anAccountAlreadyExistsWithUsername(String username) {
        initMocks();
        usersByUsername.put(username, new User("existing@example.com", username, "secret123"));
    }

    @Given("an account exists with username {string} and password {string}")
    public void anAccountExistsWithUsernameAndPassword(String username, String password) {
        initMocks();
        usersByUsername.put(username, new User(username + "@example.com", username, password));
    }

    @When("the user registers with email {string}, username {string} and password {string}")
    public void theUserRegistersWithEmailUsernameAndPassword(
            String email,
            String username,
            String password
    ) {
        assertTrue(registrationFormAccessible, "Registration form should be accessible");
        lastResult = authService.register(email, username, password);
    }

    @Then("registration is confirmed with message {string}")
    public void registrationIsConfirmedWithMessage(String expectedMessage) {
        assertInstanceOf(AuthResult.RegistrationSuccess.class, lastResult);
        AuthResult.RegistrationSuccess success = (AuthResult.RegistrationSuccess) lastResult;
        assertEquals(expectedMessage, success.message());
        verify(userRepository).save(any(User.class));
    }

    @Then("registration is rejected with message {string}")
    public void registrationIsRejectedWithMessage(String expectedMessage) {
        assertInstanceOf(AuthResult.RegistrationFailure.class, lastResult);
        AuthResult.RegistrationFailure failure = (AuthResult.RegistrationFailure) lastResult;
        assertEquals(expectedMessage, failure.error());
    }

    @Given("the login form is accessible")
    public void theLoginFormIsAccessible() {
        initMocks();
        loginFormAccessible = true;
    }

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsInWithUsernameAndPassword(String username, String password) {
        assertTrue(loginFormAccessible, "Login form should be accessible");
        lastResult = authService.login(username, password);
    }

    @Then("the user is redirected to the home page")
    public void theUserIsRedirectedToTheHomePage() {
        assertInstanceOf(AuthResult.LoginSuccess.class, lastResult);
        AuthResult.LoginSuccess success = (AuthResult.LoginSuccess) lastResult;
        assertEquals("home", success.redirectPage());
    }

    @Then("login fails with message {string}")
    public void loginFailsWithMessage(String expectedMessage) {
        assertInstanceOf(AuthResult.LoginFailure.class, lastResult);
        AuthResult.LoginFailure failure = (AuthResult.LoginFailure) lastResult;
        assertEquals(expectedMessage, failure.errorMessage());
    }

    @Given("the search bar is accessible")
    public void theSearchBarIsAccessible() {
        initMocks();
        searchBarAccessible = true;
    }

    @Given("the categories page is accessible")
    public void theCategoriesPageIsAccessible() {
        initMocks();
        categoriesPageAccessible = true;
    }

    @Given("a product {string} named {string} priced at {double} euros in category {string}")
    public void aProductNamedPricedAtEurosInCategory(
            String id,
            String name,
            double price,
            String category
    ) {
        initMocks();
        Product product = new Product(id, name, price, category);
        catalog.add(product);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
    }

    @When("the user searches for keyword {string}")
    public void theUserSearchesForKeyword(String keyword) {
        assertTrue(searchBarAccessible, "Search bar should be accessible");
        lastSearchResults = productService.searchByKeyword(keyword);
    }

    @When("the user searches for products with a maximum price of {double} euros")
    public void theUserSearchesForProductsWithAMaximumPriceOfEuros(double maxPrice) {
        assertTrue(searchBarAccessible, "Search bar should be accessible");
        lastSearchResults = productService.searchByMaxPrice(maxPrice);
    }

    @When("the user selects category {string}")
    public void theUserSelectsCategory(String category) {
        assertTrue(categoriesPageAccessible, "Categories page should be accessible");
        lastSearchResults = productService.findByCategory(category);
    }

    @Then("the search results contain product {string}")
    public void theSearchResultsContainProduct(String productId) {
        assertTrue(
                lastSearchResults.stream().anyMatch(product -> product.id().equals(productId)),
                "Expected product " + productId + " in search results"
        );
    }

    @Then("the search results contain {int} product\\(s)")
    public void theSearchResultsContainProducts(int expectedCount) {
        assertEquals(expectedCount, lastSearchResults.size());
    }

    @Then("the product repository should have been consulted for keyword {string}")
    public void theProductRepositoryShouldHaveBeenConsultedForKeyword(String keyword) {
        verify(productRepository).searchByKeyword(keyword);
    }

    @Then("the product repository should have been consulted for category {string}")
    public void theProductRepositoryShouldHaveBeenConsultedForCategory(String category) {
        verify(productRepository).findByCategory(category);
    }

    @Then("the product repository should have been consulted for max price {double}")
    public void theProductRepositoryShouldHaveBeenConsultedForMaxPrice(double maxPrice) {
        verify(productRepository).findByMaxPrice(maxPrice);
    }

    @Given("an order {string} exists")
    public void anOrderExists(String orderId) {
        initMocks();
        ordersById.put(orderId, new Order(orderId, List.of()));
    }

    @Given("no order exists with id {string}")
    public void noOrderExistsWithId(String orderId) {
        initMocks();
    }

    @Given("order {string} already contains {int} unit\\(s) of product {string}")
    public void orderAlreadyContainsUnitsOfProduct(String orderId, int quantity, String productId) {
        initMocks();
        ordersById.put(orderId, new Order(orderId, List.of(new OrderItem(productId, quantity))));
    }

    @Given("the checkout form is accessible")
    public void theCheckoutFormIsAccessible() {
        initMocks();
        checkoutFormAccessible = true;
    }

    @Given("the checkout form is accessible for order {string}")
    public void theCheckoutFormIsAccessibleForOrder(String orderId) {
        initMocks();
        checkoutFormAccessible = true;
        ordersById.put(orderId, new Order(orderId, List.of(new OrderItem("PROD-001", 1))));
    }

    @When("the user adds product {string} to order {string}")
    public void theUserAddsProductToOrder(String productId, String orderId) {
        lastResult = cartService.addProduct(orderId, productId);
    }

    @When("the user removes product {string} from order {string}")
    public void theUserRemovesProductFromOrder(String productId, String orderId) {
        lastResult = cartService.removeProduct(orderId, productId);
    }

    @When("the user validates order {string}")
    public void theUserValidatesOrder(String orderId) {
        assertTrue(checkoutFormAccessible, "Checkout form should be accessible");
        lastResult = cartService.checkout(orderId);
    }

    @Then("the product is added to the order with confirmation {string}")
    public void theProductIsAddedToTheOrderWithConfirmation(String expectedMessage) {
        assertInstanceOf(CartOperationResult.Success.class, lastResult);
        CartOperationResult.Success success = (CartOperationResult.Success) lastResult;
        assertEquals(expectedMessage, success.message());
    }

    @Then("order {string} contains {int} unit\\(s) of product {string}")
    public void orderContainsUnitsOfProductAssertion(String orderId, int expectedQuantity, String productId) {
        assertEquals(expectedQuantity, cartService.getProductQuantity(orderId, productId).orElse(0));
    }

    @Then("order {string} does not contain product {string}")
    public void orderDoesNotContainProduct(String orderId, String productId) {
        assertTrue(cartService.getProductQuantity(orderId, productId).isEmpty());
    }

    @Then("adding the product fails with message {string}")
    public void addingTheProductFailsWithMessage(String expectedMessage) {
        assertInstanceOf(CartOperationResult.Failure.class, lastResult);
        CartOperationResult.Failure failure = (CartOperationResult.Failure) lastResult;
        assertEquals(expectedMessage, failure.error());
    }

    @Then("removing the product fails with message {string}")
    public void removingTheProductFailsWithMessage(String expectedMessage) {
        assertInstanceOf(CartOperationResult.Failure.class, lastResult);
        CartOperationResult.Failure failure = (CartOperationResult.Failure) lastResult;
        assertEquals(expectedMessage, failure.error());
    }

    @Then("the product is removed from the order with confirmation {string}")
    public void theProductIsRemovedFromTheOrderWithConfirmation(String expectedMessage) {
        assertInstanceOf(CartOperationResult.Success.class, lastResult);
        CartOperationResult.Success success = (CartOperationResult.Success) lastResult;
        assertEquals(expectedMessage, success.message());
    }

    @Then("the order is confirmed with message {string}")
    public void theOrderIsConfirmedWithMessage(String expectedMessage) {
        assertInstanceOf(CheckoutResult.Success.class, lastResult);
        CheckoutResult.Success success = (CheckoutResult.Success) lastResult;
        assertEquals(expectedMessage, success.confirmationMessage());
    }

    @Then("checkout fails with message {string}")
    public void checkoutFailsWithMessage(String expectedMessage) {
        assertInstanceOf(CheckoutResult.Failure.class, lastResult);
        CheckoutResult.Failure failure = (CheckoutResult.Failure) lastResult;
        assertEquals(expectedMessage, failure.error());
    }

    @Then("the order repository should have been consulted for id {string}")
    public void theOrderRepositoryShouldHaveBeenConsultedForId(String orderId) {
        verify(orderRepository, atLeastOnce()).findById(orderId);
    }

    private void initMocks() {
        if (authService != null) {
            return;
        }

        userRepository = mock(UserRepository.class);
        productRepository = mock(ProductRepository.class);
        orderRepository = mock(OrderRepository.class);

        authService = new AuthService(userRepository);
        productService = new ProductService(productRepository);
        cartService = new CartService(orderRepository, productRepository);

        when(userRepository.existsByUsername(anyString()))
                .thenAnswer(invocation -> usersByUsername.containsKey(invocation.getArgument(0)));

        when(userRepository.findByUsername(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(usersByUsername.get(invocation.getArgument(0))));

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            usersByUsername.put(user.username(), user);
            return null;
        }).when(userRepository).save(any(User.class));

        when(orderRepository.findById(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(ordersById.get(invocation.getArgument(0))));

        doAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            ordersById.put(order.id(), order);
            return null;
        }).when(orderRepository).save(any(Order.class));

        when(productRepository.searchByKeyword(anyString()))
                .thenAnswer(invocation -> {
                    String keyword = invocation.getArgument(0, String.class).toLowerCase();
                    return catalog.stream()
                            .filter(product -> product.name().toLowerCase().contains(keyword)
                                    || product.category().toLowerCase().contains(keyword))
                            .toList();
                });

        when(productRepository.findByCategory(anyString()))
                .thenAnswer(invocation -> {
                    String category = invocation.getArgument(0, String.class);
                    return catalog.stream()
                            .filter(product -> product.category().equalsIgnoreCase(category))
                            .toList();
                });

        when(productRepository.findByMaxPrice(anyDouble()))
                .thenAnswer(invocation -> {
                    double maxPrice = invocation.getArgument(0, Double.class);
                    return catalog.stream()
                            .filter(product -> product.price() <= maxPrice)
                            .toList();
                });
    }
}
