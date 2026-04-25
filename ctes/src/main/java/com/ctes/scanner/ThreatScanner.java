package com.ctes.scanner;

import com.ctes.core.ScanResult;

/**
 * STRATEGY PATTERN (Behavioural) - Strategy interface
 *
 * Each concrete scanner is a different algorithm for finding threats.
 * The Scanner context holds a reference to one of these and delegates
 * the actual scanning work to it. New scan types can be added without
 * modifying any existing scanner.
 */
public interface ThreatScanner {

    /**
     * Analyse the given code snippet and return the result.
     * @param fileName    the name of the file being scanned (for reporting)
     * @param codeSnippet the source code to scan
     */
    ScanResult scan(String fileName, String codeSnippet);

    /** Human-readable name for this scanner, used in logs and reports. */
    String getName();
}
