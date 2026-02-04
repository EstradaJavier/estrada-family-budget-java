# Estrada Family Budget App (Java Edition)

Desktop application for tracking family income, expenses, budgets, recurring transactions, charts, and exports.

## Current Status
- Maven + Java + JavaFX skeleton
- Directory structure set up

## Project Structure
estrada-family-budget-java/
├── pom.xml
├── README.md
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/estrada/budget/
│   │   │       ├── app/          → App.java (JavaFX launch)
│   │   │       ├── model/        → Transaction, Category, Budget
│   │   │       └── util/         → JSON save/load, CSV/PDF export
│   │   └── resources/
│   │       ├── data/             → budget.json
│   │       ├── css/              → style.css
│   │       ├── images/
│   │       └── fxml/             → future UI layouts
│   └── test/
└── docs/                         → notes, diagrams

## Next Steps
1. Add JavaFX dependencies to pom.xml
2. Implement basic model classes
3. Create simple JavaFX window
4. Port recurring & summary logic from Python version

Built with Grok help 🚀
