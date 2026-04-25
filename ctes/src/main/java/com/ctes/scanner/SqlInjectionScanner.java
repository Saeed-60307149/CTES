package com.ctes.scanner;

import com.ctes.core.ScanResult;

import java.util.regex.Pattern;

/**
 * STRATEGY PATTERN (Behavioural) - Concrete Strategy
 *
 * Detects likely SQL injection vulnerabilities using pattern matching.
 * Looks for string concatenation into SQL statements - a classic SQLi
 * anti-pattern. Fast, offline, deterministic.
 */
public class SqlInjectionScanner implements ThreatScanner {

    // Matches things like:  "SELECT ... " + something   or   executeQuery("..." + x)
    private static final Pattern SQLI_PATTERN = Pattern.compile(
            "(?i)(select|insert|update|delete|drop)\\s.*\"\\s*\\+"
    );

    @Override
    public ScanResult scan(String fileName, String codeSnippet) {
        if (SQLI_PATTERN.matcher(codeSnippet).find()) {
            return new ScanResult(
                    getName(),
                    fileName,
                    ScanResult.Severity.CRITICAL,
                    "Possible SQL injection: user input concatenated into SQL statement.",
                    "Use PreparedStatement with parameterized queries (e.g. '?' placeholders) "
                  + "instead of string concatenation."
            );
        }
        return new ScanResult(getName(), fileName, ScanResult.Severity.NONE,
                "No SQL injection pattern detected.", "");
    }

    @Override
    public String getName() {
        return "SQL Injection Scanner";
    }
}
