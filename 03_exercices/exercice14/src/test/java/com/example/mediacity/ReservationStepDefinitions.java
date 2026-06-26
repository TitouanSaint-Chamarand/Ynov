package com.example.mediacity;

import com.example.mediacity.model.LoanResult;
import com.example.mediacity.model.ReservationResult;
import com.example.mediacity.service.MediaCityService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationStepDefinitions {

    private MediaCityService mediaCityService;
    private LocalDate today;
    private ReservationResult reservationResult;
    private LoanResult loanResult;
    private long activeLoanId;

    @Before
    public void resetContext() {
        mediaCityService = MediaCityService.createInMemory();
        today = LocalDate.of(2026, 6, 1);
        reservationResult = null;
        loanResult = null;
        activeLoanId = 0L;
    }

    @Given("un adhérent {string} nommé {string}")
    public void unAdherentNomme(String memberId, String name) {
        mediaCityService.registerMember(memberId, name);
    }

    @Given("un adhérent suspendu {string} nommé {string}")
    public void unAdherentSuspenduNomme(String memberId, String name) {
        mediaCityService.registerMember(memberId, name);
        createMajorDelaysUntilSuspended(memberId);
    }

    @Given("un ouvrage {string} intitulé {string}")
    public void unOuvrageIntitule(String workId, String title) {
        mediaCityService.registerWork(workId, title);
    }

    @Given("l'ouvrage {string} est emprunté par {string} depuis le {string}")
    public void lOuvrageEstEmprunteParDepuisLe(String workId, String memberId, String loanDate) {
        loanResult = mediaCityService.borrow(memberId, workId, LocalDate.parse(loanDate));
        assertThat(loanResult).isInstanceOf(LoanResult.Accepted.class);
        activeLoanId = mediaCityService.getActiveLoanId(workId);
    }

    @When("{string} réserve l'ouvrage {string} le {string}")
    public void reserveOuvrageLe(String memberId, String workId, String reservationDate) {
        reservationResult = mediaCityService.reserve(memberId, workId, LocalDate.parse(reservationDate));
    }

    @When("{string} emprunte l'ouvrage {string} le {string}")
    public void emprunteOuvrageLe(String memberId, String workId, String loanDate) {
        loanResult = mediaCityService.borrow(memberId, workId, LocalDate.parse(loanDate));
    }

    @When("l'ouvrage {string} est restitué le {string}")
    public void lOuvrageEstRestitueLe(String workId, String returnDate) {
        long loanId = mediaCityService.getActiveLoanId(workId);
        mediaCityService.returnWork(loanId, LocalDate.parse(returnDate));
    }

    @Then("la réservation est acceptée")
    public void laReservationEstAcceptee() {
        assertThat(reservationResult).isInstanceOf(ReservationResult.Accepted.class);
    }

    @Then("la réservation est refusée")
    public void laReservationEstRefusee() {
        assertThat(reservationResult).isInstanceOf(ReservationResult.Rejected.class);
    }

    @Then("le motif de refus est {string}")
    public void leMotifDeRefusEst(String reason) {
        assertThat(reservationResult).isInstanceOf(ReservationResult.Rejected.class);
        assertThat(((ReservationResult.Rejected) reservationResult).reason()).isEqualTo(reason);
    }

    @Then("il y a {int} réservation en attente pour l'ouvrage {string}")
    public void ilYAReservationEnAttentePourOuvrage(int count, String workId) {
        assertThat(mediaCityService.getPendingReservations(workId)).hasSize(count);
    }

    @Then("l'ouvrage {string} est réservé pour {string}")
    public void lOuvrageEstReservePour(String workId, String memberId) {
        assertThat(mediaCityService.getHeldForMember(workId)).isEqualTo(memberId);
    }

    @Then("l'emprunt est accepté pour {string}")
    public void lEmpruntEstAcceptePour(String memberId) {
        assertThat(loanResult).isInstanceOf(LoanResult.Accepted.class);
        assertThat(((LoanResult.Accepted) loanResult).loan().memberId()).isEqualTo(memberId);
    }

    @Then("l'emprunt est refusé")
    public void lEmpruntEstRefuse() {
        assertThat(loanResult).isInstanceOf(LoanResult.Rejected.class);
    }

    @Then("l'ouvrage {string} n'est plus emprunté")
    public void lOuvrageNestPlusEmprunte(String workId) {
        assertThat(mediaCityService.isWorkOnLoan(workId)).isFalse();
    }

    private void createMajorDelaysUntilSuspended(String memberId) {
        LocalDate baseDate = LocalDate.of(2026, 1, 1);
        for (int i = 0; i < 3; i++) {
            String workId = "SUSP-" + i;
            mediaCityService.registerWork(workId, "Ouvrage suspension " + i);
            LoanResult borrowResult = mediaCityService.borrow(memberId, workId, baseDate.plusMonths(i));
            assertThat(borrowResult).isInstanceOf(LoanResult.Accepted.class);
            long loanId = ((LoanResult.Accepted) borrowResult).loan().id();
            LocalDate dueDate = ((LoanResult.Accepted) borrowResult).loan().dueDate();
            mediaCityService.returnWork(loanId, dueDate.plusDays(10));
        }
        assertThat(mediaCityService.isMemberSuspended(memberId)).isTrue();
    }
}
