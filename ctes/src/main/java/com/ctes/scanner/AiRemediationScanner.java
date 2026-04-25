package com.ctes.scanner;

import com.ctes.adapter.GenAIClient;
import com.ctes.core.ScanResult;

/**
 * STRATEGY PATTERN (Behavioural) - Concrete Strategy
 * + AI-POWERED FEATURE (R2)
 *
 * Uses the Azure OpenAI endpoint (via the GenAIClient Adapter) to perform
 * a deeper, more general security review that regex-based scanners cannot.
 * It asks the model to act as a security auditor and return a short report
 * plus a concrete fix suggestion.
 *
 * This is where the Strategy, Adapter, and Singleton patterns meet:
 * the scanner depends on GenAIClient (Adapter), which internally uses
 * Settings.getInstance() (Singleton) to get the endpoint and key.
 */
public class AiRemediationScanner implements ThreatScanner {

    private static final String SYSTEM_PROMPT =
            "You are a senior application security auditor. "
          + "The user will give you a short code snippet. "
          + "Analyse it for security vulnerabilities (injection, XSS, insecure "
          + "deserialization, hardcoded secrets, weak crypto, path traversal, etc). "
          + "Respond in EXACTLY this format, with no extra text:\n"
          + "SEVERITY: <NONE|LOW|MEDIUM|HIGH|CRITICAL>\n"
          + "THREAT: <one-sentence description, or 'none' if no issue>\n"
          + "FIX: <one concrete code-level suggestion, or 'none'>";

    private final GenAIClient aiClient;

    public AiRemediationScanner(GenAIClient aiClient) {
        this.aiClient = aiClient;
    }

    @Override
    public ScanResult scan(String fileName, String codeSnippet) {
        String userPrompt = "File: " + fileName + "\n\nCode:\n" + codeSnippet;
        String response = aiClient.askAI(SYSTEM_PROMPT, userPrompt);

        System.out.println("    [AI raw response]");
        for (String line : response.split("\\r?\\n")) {
            System.out.println("      " + line);
        }

        if (response.startsWith("[ERROR]")) {
            return new ScanResult(getName(), fileName, ScanResult.Severity.NONE,
                    response, "");
        }
        return parseResponse(fileName, response);
    }

    private ScanResult parseResponse(String fileName, String response) {
        String severityStr = "NONE";
        String threat = "No threat detected by AI.";
        String fix = "";

        for (String line : response.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (trimmed.toUpperCase().startsWith("SEVERITY:")) {
                severityStr = trimmed.substring("SEVERITY:".length()).trim().toUpperCase();
            } else if (trimmed.toUpperCase().startsWith("THREAT:")) {
                threat = trimmed.substring("THREAT:".length()).trim();
            } else if (trimmed.toUpperCase().startsWith("FIX:")) {
                fix = trimmed.substring("FIX:".length()).trim();
            }
        }

        ScanResult.Severity severity;
        try {
            severity = ScanResult.Severity.valueOf(severityStr);
        } catch (IllegalArgumentException e) {
            severity = ScanResult.Severity.NONE;
        }
        return new ScanResult(getName(), fileName, severity, threat, fix);
    }

    @Override
    public String getName() {
        return "AI Remediation Scanner";
    }
}
