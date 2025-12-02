package com.example.demo.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Cucumber configuration class that integrates Cucumber with Spring Boot.
 * This class enables dependency injection in Cucumber step definitions.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @LocalServerPort
    protected int port;

    /**
     * Gets the base URL for the running test server.
     *
     * @return the base URL including the random port
     */
    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}
