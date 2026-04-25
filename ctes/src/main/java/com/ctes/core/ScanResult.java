package com.ctes.core;

/**
 * Simple value object representing the outcome of a single scan.
 */
public class ScanResult {

    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL, NONE }

    private final String scannerName;
    private final String fileName;
    private final Severity severity;
    private final String threatDescription;
    private final String suggestedFix;

    public ScanResult(String scannerName, String fileName, Severity severity,
                      String threatDescription, String suggestedFix) {
        this.scannerName = scannerName;
        this.fileName = fileName;
        this.severity = severity;
        this.threatDescription = threatDescription;
        this.suggestedFix = suggestedFix;
    }

    public String   getScannerName()       { return scannerName; }
    public String   getFileName()          { return fileName; }
    public Severity getSeverity()          { return severity; }
    public String   getThreatDescription() { return threatDescription; }
    public String   getSuggestedFix()      { return suggestedFix; }

    public boolean isThreatFound() {
        return severity != Severity.NONE;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s on %s: %s",
                severity, scannerName, fileName, threatDescription);
    }
}
