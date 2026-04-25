package com.ctes.scanner;

import com.ctes.core.ScanResult;

import java.util.regex.Pattern;

/**
 * STRATEGY PATTERN (Behavioural) - Concrete Strategy
 *
 * Detects likely Cross-Site Scripting (XSS) vulnerabilities.
 * Looks for direct writing of request parameters / user input into
 * the HTML response without escaping.
 */
public class XssScanner implements ThreatScanner {

    // Direct single-line sinks: innerHTML assignment, document.write, or
    // getWriter().print(...) that contains getParameter on the same line.
    private static final Pattern DIRECT_SINK = Pattern.compile(
            "(?i)(innerHTML\\s*=|document\\.write\\s*\\(|response\\.getWriter\\(\\)\\.print(ln)?\\s*\\([^)]*getParameter)"
    );

    // Indirect: request.getParameter anywhere AND a write-to-response sink
    // anywhere in the snippet. Catches the common two-line pattern:
    //   String x = request.getParameter("x");
    //   out.println("hi " + x);
    private static final Pattern USER_INPUT = Pattern.compile("(?i)request\\.getParameter\\s*\\(");
    private static final Pattern WRITE_SINK = Pattern.compile(
            "(?i)(response\\.getWriter\\(\\)\\.print(ln)?|out\\.print(ln)?|innerHTML\\s*=|document\\.write)"
    );

    @Override
    public ScanResult scan(String fileName, String codeSnippet) {
        boolean direct   = DIRECT_SINK.matcher(codeSnippet).find();
        boolean indirect = USER_INPUT.matcher(codeSnippet).find()
                        && WRITE_SINK.matcher(codeSnippet).find();
        if (direct || indirect) {
            return new ScanResult(
                    getName(),
                    fileName,
                    ScanResult.Severity.HIGH,
                    "Possible Cross-Site Scripting: untrusted input written directly to HTML output.",
                    "Escape user input before rendering it (e.g., use an HTML-escape utility "
                  + "or a templating engine with auto-escaping enabled)."
            );
        }
        return new ScanResult(getName(), fileName, ScanResult.Severity.NONE,
                "No XSS pattern detected.", "");
    }

    @Override
    public String getName() {
        return "XSS Scanner";
    }
}
