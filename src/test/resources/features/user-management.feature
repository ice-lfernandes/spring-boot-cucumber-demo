Feature: User Management
  As an API consumer
  I want to manage users through REST API
  So that I can create, read, update, and delete user information

  Background:
    Given the user service is running

  Scenario: Create a new user successfully
    Given I have user details with name "John Doe" and email "john.doe@example.com"
    When I send a POST request to create the user
    Then the response status code should be 201
    And the response should contain user with name "John Doe"
    And the response should contain user with email "john.doe@example.com"

  Scenario: Get user by ID
    Given a user exists with name "Jane Doe" and email "jane.doe@example.com"
    When I send a GET request to retrieve the user by ID
    Then the response status code should be 200
    And the response should contain user with name "Jane Doe"
    And the response should contain user with email "jane.doe@example.com"

  Scenario: Get all users
    Given the following users exist:
      | name       | email                  |
      | Alice      | alice@example.com      |
      | Bob        | bob@example.com        |
    When I send a GET request to retrieve all users
    Then the response status code should be 200
    And the response should contain 2 users

  Scenario: Update an existing user
    Given a user exists with name "Charlie" and email "charlie@example.com"
    When I update the user with name "Charlie Updated" and email "charlie.updated@example.com"
    Then the response status code should be 200
    And the response should contain user with name "Charlie Updated"
    And the response should contain user with email "charlie.updated@example.com"

  Scenario: Delete a user
    Given a user exists with name "David" and email "david@example.com"
    When I send a DELETE request to delete the user
    Then the response status code should be 204

  Scenario: Get non-existing user returns 404
    When I send a GET request to retrieve user with ID 9999
    Then the response status code should be 404

  Scenario: Create user with invalid email
    Given I have user details with name "Invalid User" and email "invalid-email"
    When I send a POST request to create the user
    Then the response status code should be 400
