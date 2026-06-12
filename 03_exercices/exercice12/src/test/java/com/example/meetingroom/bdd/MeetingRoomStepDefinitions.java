package com.example.meetingroom.bdd;

import com.example.meetingroom.repository.ReservationRepository;
import com.example.meetingroom.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MeetingRoomStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions lastResult;
    private Long createdRoomId;

    @Given("aucune salle ni reservation n existe dans l API")
    public void noDataExists() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        createdRoomId = null;
    }

    @Given("une salle existe avec le nom {string} et la capacite {int}")
    public void roomExists(String name, int capacity) throws Exception {
        lastResult = mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + name + "\",\"capacity\":" + capacity + "}"));

        var responseBody = lastResult.andReturn().getResponse().getContentAsString();
        createdRoomId = objectMapper.readTree(responseBody).get("id").asLong();
    }

    @Given("une reservation confirmee existe pour la salle de {string} a {string}")
    public void confirmedReservationExists(String start, String end) throws Exception {
        createReservation("Bob", start, end);
    }

    @When("je cree une reservation pour {string} de {string} a {string}")
    public void createReservation(String reserverName, String start, String end) throws Exception {
        lastResult = mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "roomId": %d,
                          "reserverName": "%s",
                          "startTime": "%s",
                          "endTime": "%s"
                        }
                        """.formatted(createdRoomId != null ? createdRoomId : 1L, reserverName, start, end)));
    }

    @When("je cree une reservation pour une salle inexistante")
    public void createReservationForUnknownRoom() throws Exception {
        lastResult = mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "roomId": 99,
                          "reserverName": "Alice",
                          "startTime": "2026-06-12T10:00:00",
                          "endTime": "2026-06-12T11:00:00"
                        }
                        """));
    }

    @Then("la reponse HTTP doit etre {int}")
    public void responseStatusShouldBe(int expectedStatus) throws Exception {
        lastResult.andExpect(status().is(expectedStatus));
    }

    @Then("la reservation est confirmee pour {string}")
    public void reservationIsConfirmedFor(String reserverName) throws Exception {
        lastResult.andExpect(jsonPath("$.reserverName").value(reserverName))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Then("la reponse contient un message d erreur")
    public void responseShouldContainErrorMessage() throws Exception {
        lastResult.andExpect(jsonPath("$.message").exists());
    }
}
