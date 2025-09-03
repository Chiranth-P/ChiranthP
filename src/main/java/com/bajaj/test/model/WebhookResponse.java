package com.bajaj.test.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebhookResponse {

    @JsonProperty("webhook")
    private String webhookUrl;

    @JsonProperty("accessToken")
    private String accessToken;
}
