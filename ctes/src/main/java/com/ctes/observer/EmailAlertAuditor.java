package com.ctes.observer;

import com.ctes.core.ScanResult;

/**
 * OBSERVER PATTERN (Behavioural) - Concrete Observer
 *
 * Simulates sending an email alert for HIGH / CRITICAL threats.
 * In a real system this would call an SMTP server; here it just
 * prints a formatted message so we can demo the pattern.
 */
public class EmailAlertAuditor implements SecurityAuditor {

    private final String recipient;

    public EmailAlertAuditor(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public void onThreatDetected(ScanResult result) {
        if (result.getSeverity() == ScanResult.Severity.HIGH
         || result.getSeverity() == ScanResult.Severity.CRITICAL) {
            System.out.println("[EmailAlert] -> " + recipient
                    + " | " + result.getSeverity()
                    + " threat in " + result.getFileName()
                    + ": " + result.getThreatDescription());
        }
    }

    @Override
    public String getAuditorName() {
        return "Email Alert (" + recipient + ")";
    }
}
