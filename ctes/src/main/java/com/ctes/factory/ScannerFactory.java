package com.ctes.factory;

import com.ctes.adapter.AzureOpenAIAdapter;
import com.ctes.adapter.GenAIClient;
import com.ctes.scanner.AiRemediationScanner;
import com.ctes.scanner.SqlInjectionScanner;
import com.ctes.scanner.ThreatScanner;
import com.ctes.scanner.XssScanner;

/**
 * FACTORY METHOD PATTERN (Creational)
 *
 * Centralises all ThreatScanner creation logic. The rest of the app
 * asks the factory for a scanner by type or by file extension, and
 * the factory decides which concrete Strategy class to instantiate.
 *
 * This is where the Factory and Strategy patterns meet: the factory
 * returns objects typed as the Strategy interface (ThreatScanner).
 */
public class ScannerFactory {

    public enum ScannerType { SQL_INJECTION, XSS, AI_REMEDIATION }

    private final GenAIClient sharedAiClient;

    public ScannerFactory() {
        // Adapter is created once and reused across AI scanners.
        this.sharedAiClient = new AzureOpenAIAdapter();
    }

    /** Create a scanner by explicit type. */
    public ThreatScanner create(ScannerType type) {
        switch (type) {
            case SQL_INJECTION: return new SqlInjectionScanner();
            case XSS:           return new XssScanner();
            case AI_REMEDIATION:return new AiRemediationScanner(sharedAiClient);
            default:
                throw new IllegalArgumentException("Unknown scanner type: " + type);
        }
    }

    /** Create a scanner based on file extension. */
    public ThreatScanner createForFile(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".sql")) {
            return create(ScannerType.SQL_INJECTION);
        } else if (lower.endsWith(".html") || lower.endsWith(".jsp")) {
            return create(ScannerType.XSS);
        } else {
            // Default: use the AI scanner for general-purpose code (.java, .py, etc.)
            return create(ScannerType.AI_REMEDIATION);
        }
    }
}
