package com.bajaj.test;

import com.bajaj.test.model.SolutionRequest;
import com.bajaj.test.model.WebhookRequest;
import com.bajaj.test.model.WebhookResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ChallengeRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Starting Bajaj Finserv Health Challenge ---");

        WebhookResponse webhookResponse = generateWebhook();

        if (webhookResponse == null || webhookResponse.getWebhookUrl() == null) {
            System.err.println("Failed to get webhook URL. Exiting.");
            return;
        }

        System.out.println("Successfully received webhook URL: " + webhookResponse.getWebhookUrl());
        System.out.println("Successfully received access token.");

        String myFinalQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT FROM EMPLOYEE e1 JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT AND e1.DOB < e2.DOB GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME ORDER BY e1.EMP_ID DESC;";

        submitSolution(webhookResponse.getWebhookUrl(), webhookResponse.getAccessToken(), myFinalQuery);
    }

    private WebhookResponse generateWebhook() {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        WebhookRequest requestBody = new WebhookRequest(
                "John Doe",
                "1rf23is402",
                "john@example.com"
        );

        System.out.println("Sending registration details to: " + url);

        try {
            ResponseEntity<String> responseAsString = restTemplate.postForEntity(url, requestBody, String.class);
            System.out.println("Status Code from Webhook Generation: " + responseAsString.getStatusCode());
            System.out.println("RAW Server Response: " + responseAsString.getBody());


            WebhookResponse response = objectMapper.readValue(responseAsString.getBody(), WebhookResponse.class);
            return response;

        } catch (Exception e) {
            System.err.println("Error while generating webhook or parsing the response: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        System.out.println("Submitting final query to: " + webhookUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        headers.set("Content-Type", "application/json");

        SolutionRequest requestBody = new SolutionRequest(finalQuery);

        HttpEntity<SolutionRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    webhookUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            System.out.println("Status Code from Solution Submission: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
            System.out.println("--- Challenge Completed Successfully! ---");
        } catch (Exception e) {
            System.err.println("Error while submitting solution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
