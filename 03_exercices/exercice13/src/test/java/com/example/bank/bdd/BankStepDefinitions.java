package com.example.bank.bdd;

import com.example.bank.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BankStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions lastResult;

    @Given("aucun compte n existe dans l API")
    public void noAccountExists() {
        accountRepository.deleteAll();
    }

    @Given("un compte existe avec le numero {string} et le titulaire {string}")
    public void accountExists(String number, String holder) throws Exception {
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "number": "%s",
                          "holder": "%s"
                        }
                        """.formatted(number, holder)));
    }

    @Given("le solde du compte {string} est initialise a {int}")
    public void accountHasBalance(String number, int balance) throws Exception {
        mockMvc.perform(post("/api/accounts/" + number + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "amount": %d
                        }
                        """.formatted(balance)));
    }

    @When("je cree un compte avec le numero {string} et le titulaire {string}")
    public void createAccount(String number, String holder) throws Exception {
        lastResult = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "number": "%s",
                          "holder": "%s"
                        }
                        """.formatted(number, holder)));
    }

    @When("je depose {int} sur le compte {string}")
    public void deposit(int amount, String number) throws Exception {
        lastResult = mockMvc.perform(post("/api/accounts/" + number + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "amount": %d
                        }
                        """.formatted(amount)));
    }

    @When("je retire {int} sur le compte {string}")
    public void withdraw(int amount, String number) throws Exception {
        lastResult = mockMvc.perform(post("/api/accounts/" + number + "/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "amount": %d
                        }
                        """.formatted(amount)));
    }

    @When("je vire {int} du compte {string} vers le compte {string}")
    public void transfer(int amount, String fromNumber, String toNumber) throws Exception {
        lastResult = mockMvc.perform(post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "fromNumber": "%s",
                          "toNumber": "%s",
                          "amount": %d
                        }
                        """.formatted(fromNumber, toNumber, amount)));
    }

    @Then("la reponse HTTP doit etre {int}")
    public void responseStatusShouldBe(int expectedStatus) throws Exception {
        lastResult.andExpect(status().is(expectedStatus));
    }

    @Then("le compte {string} a un solde de {int}")
    public void accountShouldHaveBalance(String number, int expectedBalance) throws Exception {
        mockMvc.perform(get("/api/accounts/" + number))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(new BigDecimal(expectedBalance)));
    }

    @Then("la reponse contient un message d erreur")
    public void responseShouldContainErrorMessage() throws Exception {
        lastResult.andExpect(jsonPath("$.message").exists());
    }
}
