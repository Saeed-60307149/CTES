package com.ctes.adapter;

import com.ctes.config.Settings;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * ADAPTER PATTERN (Structural) - Concrete Adapter
 *
 * Wraps the Azure OpenAI REST API behind the simple GenAIClient interface.
 * The rest of the application has no idea this exists - it only knows
 * GenAIClient.askAI(). If we ever swap Azure for OpenAI, Anthropic, or a
 * local model, only this class changes.
 */
public class AzureOpenAIAdapter implements GenAIClient {

    private static final MediaType JSON_TYPE = MediaType.get("application/json");
    private final OkHttpClient httpClient;
    private final Settings settings;

    public AzureOpenAIAdapter() {
        this.settings = Settings.getInstance();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(settings.getRequestTimeoutSeconds(), TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .callTimeout(settings.getRequestTimeoutSeconds() + 30, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String askAI(String systemPrompt, String userPrompt) {
        if (!settings.hasApiKey()) {
            return "[ERROR] AZURE_API_KEY environment variable is not set. "
                 + "Set it and re-run the application.";
        }

        // Build request body: Azure OpenAI chat/completions format
        JSONObject body = new JSONObject();
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
        messages.put(new JSONObject().put("role", "user").put("content", userPrompt));
        body.put("messages", messages);
        body.put("max_completion_tokens", 2000);

        Request request = new Request.Builder()
                .url(settings.getChatCompletionsUrl())
                .addHeader("api-key", settings.getApiKey())
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), JSON_TYPE))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "[ERROR] Azure OpenAI returned HTTP " + response.code()
                     + ": " + (response.body() != null ? response.body().string() : "");
            }
            String raw = response.body() != null ? response.body().string() : "";
            return extractContent(raw);
        } catch (IOException e) {
            return "[ERROR] Failed to call Azure OpenAI: " + e.getMessage();
        }
    }

    /** Translate Azure's nested JSON response into a plain String. */
    private String extractContent(String rawJson) {
        try {
            JSONObject json = new JSONObject(rawJson);
            JSONArray choices = json.optJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                return "[ERROR] No choices returned by model. Raw: " + rawJson;
            }
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            return message.optString("content", "[ERROR] Empty content");
        } catch (Exception e) {
            return "[ERROR] Failed to parse AI response: " + e.getMessage();
        }
    }
}
