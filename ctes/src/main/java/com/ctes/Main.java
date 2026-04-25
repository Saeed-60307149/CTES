package com.ctes;

import com.ctes.config.Settings;
import com.ctes.core.ScanResult;
import com.ctes.factory.ScannerFactory;
import com.ctes.observer.ConsoleLoggerAuditor;
import com.ctes.observer.EmailAlertAuditor;
import com.ctes.observer.SecurityMonitor;
import com.ctes.report.JsonReportCreator;
import com.ctes.report.ReportCreator;
import com.ctes.report.TextReportCreator;
import com.ctes.scanner.ScannerContext;
import com.ctes.scanner.ThreatScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * CTES - Cyber Threat Evaluation System
 *
 * Entry point. Wires together every design pattern and demonstrates
 * them via a simple interactive console menu:
 *
 *   - SINGLETON      : Settings.getInstance() for config + API key
 *   - ADAPTER        : AzureOpenAIAdapter wraps Azure OpenAI REST API
 *   - STRATEGY       : ScannerContext swaps ThreatScanner at runtime
 *   - FACTORY METHOD : ScannerFactory + ReportCreator
 *   - OBSERVER       : SecurityMonitor notifies all SecurityAuditors
 */
public class Main {

    private static final SecurityMonitor monitor = new SecurityMonitor();
    private static final ScannerFactory factory = new ScannerFactory();
    private static final List<ScanResult> history = new ArrayList<>();

    public static void main(String[] args) {
        printBanner();

        // Singleton check
        Settings settings = Settings.getInstance();
        System.out.println("[Config] Endpoint   : " + settings.getAzureEndpoint());
        System.out.println("[Config] Deployment : " + settings.getAzureDeployment());
        System.out.println("[Config] API key set: " + settings.hasApiKey());
        if (!settings.hasApiKey()) {
            System.out.println("[WARN]  AZURE_API_KEY environment variable is not set.");
            System.out.println("        The AI-powered scanner will return an error until you set it.");
        }
        System.out.println();

        // Register observers
        monitor.register(new ConsoleLoggerAuditor());
        monitor.register(new EmailAlertAuditor("security-team@ctes.local"));
        System.out.println();

        Scanner input = new Scanner(System.in);
        while (true) {
            printMenu();
            String choice = input.nextLine().trim();
            switch (choice) {
                case "1": runDemo("vulnerable.sql", demoSqlCode()); break;
                case "2": runDemo("login.jsp",      demoXssCode()); break;
                case "3": runDemo("UserService.java", demoJavaCode()); break;
                case "4": runCustomScan(input); break;
                case "5": printReport(new TextReportCreator()); break;
                case "6": printReport(new JsonReportCreator()); break;
                case "0":
                    System.out.println("Goodbye.");
                    return;
                default:
                    System.out.println("Unknown option.");
            }
        }
    }

    /**
     * One scan pass: Factory picks the Strategy, Context runs it,
     * the Monitor publishes the result to every registered Observer.
     */
    private static void runDemo(String fileName, String code) {
        System.out.println();
        System.out.println(">>> Scanning " + fileName + " ...");
        ThreatScanner scanner = factory.createForFile(fileName);
        System.out.println("    Factory selected: " + scanner.getName());

        ScannerContext context = new ScannerContext(scanner);
        ScanResult result = context.runScan(fileName, code);
        history.add(result);

        // If the AI scanner returned an error, show it loudly instead of
        // pretending no threat was found.
        if (result.getThreatDescription().startsWith("[ERROR]")) {
            System.out.println("    !! " + result.getThreatDescription());
            return;
        }

        if (result.isThreatFound()) {
            monitor.publish(result);
        } else {
            System.out.println("    No threat detected.");
        }
    }

    private static void runCustomScan(Scanner input) {
        System.out.println("Enter file name (e.g. myclass.java): ");
        String fileName = input.nextLine().trim();
        System.out.println("Paste code, then an empty line to finish:");
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = input.nextLine();
            if (line.isEmpty()) break;
            sb.append(line).append('\n');
        }
        runDemo(fileName, sb.toString());
    }

    private static void printReport(ReportCreator creator) {
        System.out.println();
        System.out.println(creator.generate(history));
    }

    private static void printBanner() {
        System.out.println("========================================================");
        System.out.println("  CTES - Cyber Threat Evaluation System");
        System.out.println("  Design Patterns & Modeling | UDST Spring 2026");
        System.out.println("========================================================");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("---- MENU ----");
        System.out.println("1) Scan demo .sql file    (SQL Injection Scanner via Factory)");
        System.out.println("2) Scan demo .jsp file    (XSS Scanner via Factory)");
        System.out.println("3) Scan demo .java file   (AI Remediation Scanner - calls Azure)");
        System.out.println("4) Scan custom code");
        System.out.println("5) Generate text report   (Factory Method)");
        System.out.println("6) Generate JSON report   (Factory Method)");
        System.out.println("0) Exit");
        System.out.print("Choice: ");
    }

    // ---- Demo code samples --------------------------------------------------

    private static String demoSqlCode() {
        return "String q = \"SELECT * FROM users WHERE name = '\" + userName + \"'\";\n"
             + "stmt.executeQuery(q);";
    }

    private static String demoXssCode() {
        return "String name = request.getParameter(\"name\");\n"
             + "response.getWriter().println(\"<h1>Hello \" + name + \"</h1>\");";
    }

    private static String demoJavaCode() {
        return "public class AuthService {\n"
             + "    // Hardcoded database credentials\n"
             + "    private static final String DB_USER = \"admin\";\n"
             + "    private static final String DB_PASS = \"admin123\";\n"
             + "    // Weak cryptography\n"
             + "    public String hash(String pw) throws Exception {\n"
             + "        return java.security.MessageDigest.getInstance(\"MD5\")\n"
             + "            .digest(pw.getBytes()).toString();\n"
             + "    }\n"
             + "}";
    }
}
