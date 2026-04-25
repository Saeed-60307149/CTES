package com.ctes.report;

import com.ctes.core.ScanResult;

import java.util.List;

public class TextReport extends Report {
    public TextReport(List<ScanResult> results) { super(results); }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== CTES Text Report =====\n");
        sb.append("Total scans: ").append(results.size()).append('\n');
        long threats = results.stream().filter(ScanResult::isThreatFound).count();
        sb.append("Threats found: ").append(threats).append("\n\n");
        for (ScanResult r : results) {
            sb.append("- ").append(r.toString()).append('\n');
            if (!r.getSuggestedFix().isBlank()) {
                sb.append("    Fix: ").append(r.getSuggestedFix()).append('\n');
            }
        }
        return sb.toString();
    }
}
