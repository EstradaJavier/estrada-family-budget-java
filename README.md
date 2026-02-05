# Estrada Family Budget App

A personal desktop budgeting tool built for the Estrada family. Tracks income (military retirement, disability, salaries), fixed/recurring expenses (DFAS deductions, former spouse support, credit cards, loans, subscriptions, pet care), and helps prevent overdrafts with upcoming payments visibility, projected balance simulator, and paid checkboxes.

## Features
- **Monthly Summary** — Income, Expenses, Balance (vertically aligned with color coding)
- **Current Balances** — Pop-up view (Sam’s Club, Citi, mortgage, Regions, Buckle, Cosco, AFEES, etc.)
- **Upcoming Payments** — Pop-up with checkboxes to mark bills as paid (Regions $500, Citi min, Sam's Club min, iCloud, Tractive, SBP, TriWest, former spouse, etc.)
- **Add Transactions** — Form for income/expense with recurring options (weekly/monthly/yearly)
- **Projected Balance Simulator** — Test spend amounts and see impact after upcoming bills/income
- **UI** — Centered elegant title, date + temperature header, horizontal separator, scrollable content, dark labels, modern cards with shadows
- **Data Persistence** — JSON save/load in `~/EstradaFamilyBudget/budget.json`
- **Reference Docs** — Git commands in `/docs/git-commands-reference.md`

## Tech Stack
- Java 21/25
- JavaFX (desktop UI)
- Maven (build & dependencies)
- Jackson (JSON persistence)

## Setup & Run (IntelliJ on Mac)
1. Open the project in IntelliJ IDEA
2. Right-click `pom.xml` → **Maven** → **Reload Project** (or **Sync Project**)
3. Build → **Rebuild Project** (Cmd + Shift + F9)
4. In Maven tool window (right sidebar) → Plugins → javafx → double-click **run**  
   - Or in Terminal: `mvn javafx:run` (if Maven installed globally via Homebrew)

## Branches
- `master` — Stable working version
- `feature/ui-enhancements` — Active improvements (current branch: UI polish, pop-ups, alerts, styling)

## Screenshots
(Add screenshots here later — e.g., main window, pop-ups, summary grid)

## Development Notes
- Data stored in `~/EstradaFamilyBudget/budget.json`
- Balances and upcoming payments are hardcoded defaults — update in `App.java` as needed
- Future ideas:
  - Dynamic real-time temperature (OpenWeather API for Salado, TX)
  - Persistent "paid" checkbox status for bills
  - Functional "Add New Bill" / "Add New Income" dialogs
  - CSV/PDF export
  - Charts (pie for category spending, bar for trends)
  - Dark mode toggle

Built with help from Grok (xAI) 🚀
