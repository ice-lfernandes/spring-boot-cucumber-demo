# Spring Boot Cucumber BDD Demo

A comprehensive demonstration of Behavior Driven Development (BDD) using Spring Boot and Cucumber. This project showcases how to integrate Cucumber with Spring Boot for writing expressive, human-readable tests, including Kafka messaging scenarios.

## Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Understanding BDD and Cucumber](#understanding-bdd-and-cucumber)
- [Feature Files](#feature-files)
- [Kafka Messaging](#kafka-messaging)
- [Step Definitions](#step-definitions)
- [Running Tests](#running-tests)
- [Test Reports](#test-reports)

## Overview

This demo project demonstrates:

- Spring Boot REST API implementation
- Cucumber BDD integration with Spring Boot
- **Kafka messaging with publish and consume scenarios**
- Writing Gherkin feature files
- Creating step definitions
- Using data tables in scenarios
- Test configuration and runners
- **Testcontainers for Kafka integration testing**

## Project Structure

```
spring-boot-cucumber-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/demo/
│   │   │       ├── DemoApplication.java          # Main application class
│   │   │       ├── controller/
│   │   │       │   └── UserController.java       # REST controller
│   │   │       ├── kafka/
│   │   │       │   ├── KafkaConfig.java          # Kafka configuration
│   │   │       │   ├── KafkaProducerService.java # Kafka producer
│   │   │       │   └── KafkaConsumerService.java # Kafka consumer
│   │   │       ├── model/
│   │   │       │   └── User.java                 # User model
│   │   │       └── service/
│   │   │           └── UserService.java          # User service
│   │   └── resources/
│   │       └── application.properties            # Application config
│   └── test/
│       ├── java/
│       │   └── com/example/demo/cucumber/
│       │       ├── config/
│       │       │   └── CucumberSpringConfiguration.java  # Cucumber-Spring config with Testcontainers
│       │       ├── runner/
│       │       │   └── CucumberTestRunner.java   # Test runner
│       │       └── steps/
│       │           ├── UserStepDefinitions.java  # User step definitions
│       │           └── KafkaStepDefinitions.java # Kafka step definitions
│       └── resources/
│           ├── application-test.properties      # Test config
│           └── features/
│               ├── user-management.feature      # User management scenarios
│               └── kafka-messaging.feature      # Kafka messaging scenarios
└── pom.xml                                      # Maven configuration
```

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker (for running Kafka Testcontainers)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/ice-lfernandes/spring-boot-cucumber-demo.git
cd spring-boot-cucumber-demo
```

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

### API Endpoints

| Method | Endpoint           | Description          |
|--------|-------------------|----------------------|
| GET    | /api/users        | Get all users        |
| GET    | /api/users/{id}   | Get user by ID       |
| POST   | /api/users        | Create a new user    |
| PUT    | /api/users/{id}   | Update a user        |
| DELETE | /api/users/{id}   | Delete a user        |

## Understanding BDD and Cucumber

### What is BDD?

Behavior Driven Development (BDD) is an agile software development approach that encourages collaboration between developers, QA, and non-technical stakeholders. BDD focuses on:

- **Shared understanding**: All team members use a common language to describe software behavior
- **Living documentation**: Tests serve as up-to-date documentation
- **User-centric**: Focus on user behavior and business value

### What is Cucumber?

Cucumber is a BDD testing framework that allows you to write tests in plain English using Gherkin syntax. Key concepts:

- **Feature**: A high-level description of a software feature
- **Scenario**: A concrete example of how a feature should behave
- **Given/When/Then**: Steps that describe preconditions, actions, and expected outcomes

### Gherkin Keywords

| Keyword    | Purpose                                    |
|-----------|-------------------------------------------|
| Feature   | Describes the feature being tested        |
| Scenario  | Describes a specific test case            |
| Given     | Sets up the initial context               |
| When      | Describes the action taken                |
| Then      | Describes the expected outcome            |
| And/But   | Adds additional steps                     |
| Background| Steps executed before each scenario       |

## Feature Files

Feature files are written in Gherkin syntax and located in `src/test/resources/features/`.

### Example Feature File

```gherkin
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
```

### Data Tables

Cucumber supports data tables for providing structured test data:

```gherkin
Scenario: Get all users
  Given the following users exist:
    | name  | email              |
    | Alice | alice@example.com  |
    | Bob   | bob@example.com    |
  When I send a GET request to retrieve all users
  Then the response should contain 2 users
```

## Kafka Messaging

This project demonstrates Kafka message publishing and consuming with BDD scenarios.

### Kafka Feature File Example

```gherkin
Feature: Kafka Messaging
  As a message producer and consumer
  I want to publish and consume messages through Kafka
  So that I can verify asynchronous message processing

  Background:
    Given the Kafka service is running

  Scenario: Publish a message to Kafka topic
    Given I have a message with content "Hello Kafka!"
    When I publish the message to topic "user-events"
    Then the message should be published successfully

  Scenario: Consume a message from Kafka topic
    Given I have a message with content "User created event"
    When I publish the message to topic "user-events"
    Then the consumer should receive the message "User created event"

  Scenario: Publish user event to Kafka
    Given I have a user event with name "John Doe" and email "john@example.com"
    When I publish the user event to topic "user-events"
    Then the message should be published successfully
    And the consumer should receive a message containing "John Doe"
```

### Kafka Components

| Component             | Purpose                                    |
|----------------------|-------------------------------------------|
| KafkaProducerService | Publishes messages to Kafka topics        |
| KafkaConsumerService | Consumes messages from Kafka topics       |
| KafkaConfig          | Kafka producer and consumer configuration |

### Testcontainers Integration

The project uses Testcontainers to spin up a real Kafka instance during tests:

```java
@Container
static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
);

@DynamicPropertySource
static void kafkaProperties(DynamicPropertyRegistry registry) {
    kafka.start();
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
}
```

## Step Definitions

Step definitions connect Gherkin steps to Java code. They are located in `src/test/java/com/example/demo/cucumber/steps/`.

### Example Step Definitions

```java
@Given("I have user details with name {string} and email {string}")
public void iHaveUserDetailsWithNameAndEmail(String name, String email) {
    currentUser = new User();
    currentUser.setName(name);
    currentUser.setEmail(email);
}

@When("I send a POST request to create the user")
public void iSendAPostRequestToCreateTheUser() {
    HttpEntity<User> request = new HttpEntity<>(currentUser, headers);
    response = restTemplate.postForEntity(getBaseUrl(), request, String.class);
}

@Then("the response status code should be {int}")
public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
    assertEquals(expectedStatusCode, response.getStatusCode().value());
}
```

## Running Tests

### Run All Cucumber Tests

```bash
mvn test
```

### Run with Specific Tags

You can add tags to scenarios and run specific ones:

```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

### Run from IDE

Run the `CucumberTestRunner` class directly from your IDE (IntelliJ, Eclipse, etc.).

## Test Reports

After running tests, reports are generated in `target/cucumber-reports/`:

- **HTML Report**: `target/cucumber-reports/cucumber.html`
- **JSON Report**: `target/cucumber-reports/cucumber.json`

## Key Dependencies

| Dependency                      | Purpose                            |
|--------------------------------|-----------------------------------|
| spring-boot-starter-web        | Spring Boot web framework         |
| spring-boot-starter-test       | Testing utilities                 |
| spring-kafka                   | Spring Kafka integration          |
| spring-kafka-test              | Kafka testing utilities           |
| testcontainers-kafka           | Kafka Testcontainers              |
| cucumber-java                  | Cucumber for Java                 |
| cucumber-spring                | Cucumber-Spring integration       |
| cucumber-junit-platform-engine | JUnit 5 integration               |
| junit-platform-suite           | JUnit Platform Suite              |

## Best Practices

1. **Write scenarios from user perspective**: Focus on what the user wants to achieve
2. **Keep scenarios independent**: Each scenario should be self-contained
3. **Use Background for common setup**: Avoid repeating the same Given steps
4. **Use meaningful step names**: Make steps readable and reusable
5. **One assertion per Then step**: Keep verification focused
6. **Use data tables for multiple data**: Makes scenarios more readable

## License

This project is for demonstration purposes