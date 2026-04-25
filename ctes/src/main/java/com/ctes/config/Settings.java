package com.ctes.config;

/**
 * SINGLETON PATTERN (Creational)
 *
 * Ensures a single point of configuration for the whole application.
 * Holds the Azure OpenAI endpoint, deployment name, and API key
 * (loaded from the AZURE_API_KEY environment variable, never hardcoded).
 *
 * Every part of the app that needs config calls Settings.getInstance().
 */
public class Settings {

    private static Settings instance;

    private final String azureEndpoint;
    private final String azureDeployment;
    private final String azureApiVersion;
    private final String apiKey;
    private final int requestTimeoutSeconds;

    private Settings() {
        this.azureEndpoint   = "https://60099-m1xc2jq0-australiaeast.openai.azure.com/";
        this.azureDeployment = "gpt-5-mini-vanilson";
        this.azureApiVersion = "2024-02-15-preview";
        this.apiKey          = System.getenv("AZURE_API_KEY");
        this.requestTimeoutSeconds = 120;
    }

    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public String getAzureEndpoint()     { return azureEndpoint; }
    public String getAzureDeployment()   { return azureDeployment; }
    public String getAzureApiVersion()   { return azureApiVersion; }
    public String getApiKey()            { return apiKey; }
    public int    getRequestTimeoutSeconds() { return requestTimeoutSeconds; }

    public String getChatCompletionsUrl() {
        return azureEndpoint + "openai/deployments/" + azureDeployment
                + "/chat/completions?api-version=" + azureApiVersion;
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }
}
