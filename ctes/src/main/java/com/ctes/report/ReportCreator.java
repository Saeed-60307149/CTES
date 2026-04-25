package com.ctes.report;

import com.ctes.core.ScanResult;

import java.util.List;

/**
 * FACTORY METHOD PATTERN (Creational) - Creator
 *
 * Each subclass overrides createReport() to return a different concrete
 * Report type. The calling code just gets back a Report and calls render().
 */
public abstract class ReportCreator {

    public abstract Report createReport(List<ScanResult> results);

    public String generate(List<ScanResult> results) {
        Report report = createReport(results);
        return report.render();
    }
}
