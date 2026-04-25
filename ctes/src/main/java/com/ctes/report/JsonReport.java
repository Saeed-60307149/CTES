package com.ctes.report;

import com.ctes.core.ScanResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class JsonReport extends Report {
    public JsonReport(List<ScanResult> results) { super(results); }

    @Override
    public String render() {
        JSONObject root = new JSONObject();
        root.put("total_scans", results.size());
        JSONArray arr = new JSONArray();
        for (ScanResult r : results) {
            JSONObject o = new JSONObject();
            o.put("scanner", r.getScannerName());
            o.put("file", r.getFileName());
            o.put("severity", r.getSeverity().name());
            o.put("threat", r.getThreatDescription());
            o.put("fix", r.getSuggestedFix());
            arr.put(o);
        }
        root.put("results", arr);
        return root.toString(2);
    }
}
