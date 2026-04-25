# CTES — Cyber Threat Evaluation System

Reference implementation for the SOFT 3202 GenAI design patterns project.
A small Java console application that scans code snippets for security
vulnerabilities and uses Azure OpenAI to suggest fixes.

## Design patterns implemented

| # | Pattern        | Category    | Where to find it |
|---|----------------|-------------|------------------|
| 1 | Strategy       | Behavioural | `scanner/ThreatScanner.java`, `ScannerContext.java`, and the 3 concrete scanners |
| 2 | Factory Method | Creational  | `factory/ScannerFactory.java` and `report/ReportCreator.java` subclasses |
| 3 | Observer       | Behavioural | `observer/SecurityMonitor.java` + `SecurityAuditor` implementations |
| 4 | Adapter        | Structural  | `adapter/GenAIClient.java` + `AzureOpenAIAdapter.java` |
| 5 | Singleton      | Creational  | `config/Settings.java` |

GoF coverage: **Creational ✓  Structural ✓  Behavioural ✓**

The AI-powered feature (`AiRemediationScanner`) is where Strategy, Adapter,
and Singleton meet: the scanner is a Strategy, it depends on the `GenAIClient`
Adapter which wraps the Azure REST API, and the Adapter reads its endpoint
and API key from the `Settings` Singleton.

## Prerequisites

- Java 17 or newer
- Maven 3.6+

## 1. Install dependencies

From the project root:

```bash
mvn clean package
```

Maven will download OkHttp and org.json on first run.

## 2. Set the API key as an environment variable

**Never commit the key. Never paste it into source files.**

Linux / macOS:
```bash
export AZURE_API_KEY="your-key-here"
```

Windows (PowerShell):
```powershell
$env:AZURE_API_KEY = "your-key-here"
```

Windows (cmd):
```cmd
set AZURE_API_KEY=your-key-here
```

## 3. Run the application

```bash
mvn exec:java -Dexec.mainClass=com.ctes.Main
```

Or run the packaged jar:
```bash
java -cp "target/classes:$(find ~/.m2/repository -name 'okhttp-4.12.0.jar'):$(find ~/.m2/repository -name 'okio-jvm-3.6.0.jar'):$(find ~/.m2/repository -name 'kotlin-stdlib-*.jar' | head -1):$(find ~/.m2/repository -name 'json-20240303.jar')" com.ctes.Main
```

## Menu options

```
1) Scan demo .sql file    -> Factory picks SqlInjectionScanner (Strategy)
2) Scan demo .jsp file    -> Factory picks XssScanner          (Strategy)
3) Scan demo .java file   -> Factory picks AiRemediationScanner -> Azure OpenAI
4) Scan custom code       -> paste your own snippet
5) Generate text report   -> TextReportCreator (Factory Method)
6) Generate JSON report   -> JsonReportCreator (Factory Method)
0) Exit
```

When a scan detects a threat, `SecurityMonitor` publishes the result to
every registered `SecurityAuditor` (Observer pattern), which in the demo
includes a console logger and a simulated email alert.

## Project layout

```
src/main/java/com/ctes/
├── Main.java                         entry point, wires everything together
├── config/
│   └── Settings.java                 Singleton
├── adapter/
│   ├── GenAIClient.java              Adapter target interface
│   └── AzureOpenAIAdapter.java       Adapter concrete implementation
├── core/
│   └── ScanResult.java               value object
├── scanner/
│   ├── ThreatScanner.java            Strategy interface
│   ├── ScannerContext.java           Strategy context
│   ├── SqlInjectionScanner.java      concrete Strategy
│   ├── XssScanner.java               concrete Strategy
│   └── AiRemediationScanner.java     concrete Strategy (AI-powered)
├── factory/
│   └── ScannerFactory.java           Factory Method
├── observer/
│   ├── SecurityAuditor.java          Observer interface
│   ├── SecurityMonitor.java          Subject
│   ├── ConsoleLoggerAuditor.java     concrete Observer
│   └── EmailAlertAuditor.java        concrete Observer
└── report/
    ├── Report.java                   product base
    ├── TextReport.java               concrete product
    ├── JsonReport.java               concrete product
    ├── ReportCreator.java            Factory Method creator
    ├── TextReportCreator.java        concrete creator
    └── JsonReportCreator.java        concrete creator
```

## Notes

- All code targets Java 17.
- No secrets in source files. `Settings.getInstance()` reads `AZURE_API_KEY`
  from the environment at runtime.
- Network calls go through OkHttp with a 60-second call timeout.
