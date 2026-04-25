package com.ctes.report;

import com.ctes.core.ScanResult;

import java.util.List;

/**
 * FACTORY METHOD PATTERN (Creational) - Product interface
 *
 * Abstract report that different concrete subclasses format differently
 * (plain text summary, JSON, etc). Subclasses are created by the
 * ReportCreator factory method.
 */
public abstract class Report {
    protected final List<ScanResult> results;

    protected Report(List<ScanResult> results) {
        this.results = results;
    }

    public abstract String render();
}
