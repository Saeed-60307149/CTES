package com.ctes.adapter;

/**
 * ADAPTER PATTERN (Structural) - Target interface
 *
 * This is the simple interface the rest of the CTES application depends on.
 * It hides all details of whatever GenAI provider is actually used behind it.
 * Implementations wrap third-party SDKs / raw HTTP clients and translate
 * their calls into this clean askAI() method.
 */
public interface GenAIClient {

    /**
     * Send a prompt to the AI model and return the text response.
     * @param systemPrompt the system / instruction prompt
     * @param userPrompt   the user prompt (e.g., the code snippet to analyse)
     * @return the model's text response
     */
    String askAI(String systemPrompt, String userPrompt);
}
