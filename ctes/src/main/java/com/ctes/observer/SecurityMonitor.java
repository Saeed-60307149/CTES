package com.ctes.observer;

import com.ctes.core.ScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER PATTERN (Behavioural) - Subject
 *
 * Maintains a list of SecurityAuditor observers and notifies all of
 * them whenever a scan produces a result worth reporting. The scanner
 * code only has to call publish() - it doesn't know or care which
 * auditors are listening.
 */
public class SecurityMonitor {

    private final List<SecurityAuditor> auditors = new ArrayList<>();

    public void register(SecurityAuditor auditor) {
        auditors.add(auditor);
        System.out.println("[Monitor] Registered auditor: " + auditor.getAuditorName());
    }

    public void unregister(SecurityAuditor auditor) {
        auditors.remove(auditor);
    }

    /** Notify every registered auditor about a scan result. */
    public void publish(ScanResult result) {
        if (!result.isThreatFound()) return;
        for (SecurityAuditor auditor : auditors) {
            auditor.onThreatDetected(result);
        }
    }

    public int getAuditorCount() {
        return auditors.size();
    }
}
