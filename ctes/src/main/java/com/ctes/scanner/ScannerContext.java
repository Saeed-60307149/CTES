package com.ctes.scanner;

import com.ctes.core.ScanResult;

/**
 * STRATEGY PATTERN (Behavioural) - Context
 *
 * Holds a reference to a ThreatScanner strategy and delegates the
 * actual scanning to it. The active strategy can be swapped at runtime
 * with setScanner() without changing any calling code.
 */
public class ScannerContext {

    private ThreatScanner scanner;

    public ScannerContext(ThreatScanner initialScanner) {
        this.scanner = initialScanner;
    }

    public void setScanner(ThreatScanner scanner) {
        this.scanner = scanner;
    }

    public ThreatScanner getScanner() {
        return scanner;
    }

    public ScanResult runScan(String fileName, String codeSnippet) {
        return scanner.scan(fileName, codeSnippet);
    }
}
