package com.bajaj.test.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebhookResponse {
   
    @JsonProperty("webhook_url")
    private String webhookUrl;

    @JsonProperty("access_token")
    private String accessToken;
}
