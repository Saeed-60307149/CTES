package com.ctes.observer;

import com.ctes.core.ScanResult;

/**
 * OBSERVER PATTERN (Behavioural) - Concrete Observer
 *
 * Logs threats to the console with a severity-coloured prefix.
 */
public class ConsoleLoggerAuditor implements SecurityAuditor {

    @Override
    public void onThreatDetected(ScanResult result) {
        System.out.println();
        System.out.println("==================== THREAT ALERT ====================");
        System.out.println(" Severity : " + result.getSeverity());
        System.out.println(" Scanner  : " + result.getScannerName());
        System.out.println(" File     : " + result.getFileName());
        System.out.println(" Threat   : " + result.getThreatDescription());
        if (!result.getSuggestedFix().isBlank()) {
            System.out.println(" Fix      : " + result.getSuggestedFix());
        }
        System.out.println("======================================================");
    }

    @Override
    public String getAuditorName() {
        return "Console Logger";
    }
}
