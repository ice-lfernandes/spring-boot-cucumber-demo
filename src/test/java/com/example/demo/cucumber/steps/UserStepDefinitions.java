package com.example.demo.cucumber.steps;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for User Management feature.
 * Contains the implementation of Gherkin steps.
 */
public class UserStepDefinitions {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private User currentUser;
    private Long currentUserId;
    private ResponseEntity<?> response;
    private HttpHeaders headers;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/users";
    }

    @Before
    public void setup() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        userService.clearAllUsers();
    }

    @After
    public void cleanup() {
        userService.clearAllUsers();
    }

    @Given("the user service is running")
    public void theUserServiceIsRunning() {
        // Service is running as part of the Spring Boot test context
        assertNotNull(restTemplate);
    }

    @Given("I have user details with name {string} and email {string}")
    public void iHaveUserDetailsWithNameAndEmail(String name, String email) {
        currentUser = new User();
        currentUser.setName(name);
        currentUser.setEmail(email);
    }

    @Given("a user exists with name {string} and email {string}")
    public void aUserExistsWithNameAndEmail(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        User createdUser = userService.createUser(user);
        currentUserId = createdUser.getId();
    }

    @Given("the following users exist:")
    public void theFollowingUsersExist(DataTable dataTable) {
        List<Map<String, String>> users = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> userData : users) {
            User user = new User();
            user.setName(userData.get("name"));
            user.setEmail(userData.get("email"));
            userService.createUser(user);
        }
    }

    @When("I send a POST request to create the user")
    public void iSendAPostRequestToCreateTheUser() {
        HttpEntity<User> request = new HttpEntity<>(currentUser, headers);
        response = restTemplate.postForEntity(getBaseUrl(), request, String.class);
    }

    @When("I send a GET request to retrieve the user by ID")
    public void iSendAGetRequestToRetrieveTheUserById() {
        response = restTemplate.getForEntity(getBaseUrl() + "/" + currentUserId, String.class);
    }

    @When("I send a GET request to retrieve user with ID {long}")
    public void iSendAGetRequestToRetrieveUserWithId(Long id) {
        response = restTemplate.getForEntity(getBaseUrl() + "/" + id, String.class);
    }

    @When("I send a GET request to retrieve all users")
    public void iSendAGetRequestToRetrieveAllUsers() {
        response = restTemplate.getForEntity(getBaseUrl(), String.class);
    }

    @When("I update the user with name {string} and email {string}")
    public void iUpdateTheUserWithNameAndEmail(String name, String email) {
        User updatedUser = new User();
        updatedUser.setName(name);
        updatedUser.setEmail(email);
        HttpEntity<User> request = new HttpEntity<>(updatedUser, headers);
        response = restTemplate.exchange(
                getBaseUrl() + "/" + currentUserId,
                HttpMethod.PUT,
                request,
                String.class
        );
    }

    @When("I send a DELETE request to delete the user")
    public void iSendADeleteRequestToDeleteTheUser() {
        response = restTemplate.exchange(
                getBaseUrl() + "/" + currentUserId,
                HttpMethod.DELETE,
                null,
                String.class
        );
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        assertNotNull(response);
        assertEquals(expectedStatusCode, response.getStatusCode().value());
    }

    @And("the response should contain user with name {string}")
    public void theResponseShouldContainUserWithName(String expectedName) throws JsonProcessingException {
        String body = (String) response.getBody();
        assertNotNull(body);
        JsonNode jsonNode = objectMapper.readTree(body);
        String actualName = jsonNode.has("name") ? jsonNode.get("name").asText() : null;
        assertEquals(expectedName, actualName, "Response should contain user with name: " + expectedName);
    }

    @And("the response should contain user with email {string}")
    public void theResponseShouldContainUserWithEmail(String expectedEmail) throws JsonProcessingException {
        String body = (String) response.getBody();
        assertNotNull(body);
        JsonNode jsonNode = objectMapper.readTree(body);
        String actualEmail = jsonNode.has("email") ? jsonNode.get("email").asText() : null;
        assertEquals(expectedEmail, actualEmail, "Response should contain user with email: " + expectedEmail);
    }

    @And("the response should contain {int} users")
    public void theResponseShouldContainUsers(int expectedCount) throws JsonProcessingException {
        String body = (String) response.getBody();
        assertNotNull(body);
        JsonNode jsonArray = objectMapper.readTree(body);
        assertTrue(jsonArray.isArray(), "Response should be a JSON array");
        assertEquals(expectedCount, jsonArray.size(), "Response should contain " + expectedCount + " users");
    }
}
