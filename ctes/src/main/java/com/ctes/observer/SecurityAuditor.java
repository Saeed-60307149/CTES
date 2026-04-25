package com.ctes.observer;

import com.ctes.core.ScanResult;

/**
 * OBSERVER PATTERN (Behavioural) - Observer interface
 *
 * Anything that wants to react to a detected threat implements this
 * interface and registers itself with the SecurityMonitor (Subject).
 */
public interface SecurityAuditor {
    void onThreatDetected(ScanResult result);
    String getAuditorName();
}
