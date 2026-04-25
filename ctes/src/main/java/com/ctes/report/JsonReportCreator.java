package com.ctes.report;

import com.ctes.core.ScanResult;

import java.util.List;

public class JsonReportCreator extends ReportCreator {
    @Override
    public Report createReport(List<ScanResult> results) {
        return new JsonReport(results);
    }
}
