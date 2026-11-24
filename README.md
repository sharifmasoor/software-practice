# Bakery Ordering System

A Java Swing desktop application containing three connected staff screens:
- Manage Orders
- Manage Sales
- Manage Delivery

## Requirements
- Java Development Kit (JDK) 8 or later
- VS Code (optional) or any Java IDE

## Run in VS Code / PowerShell

Open the project root folder (`BakeryOrderingSystem_Final`) in VS Code.

Compile all Java files:

```powershell
javac -d out src\ui\Theme.java src\models\Order.java src\data\OrderStore.java src\screens\ManageOrdersScreen.java src\screens\ManageSalesScreen.java src\screens\ManageDeliveryScreen.java src\Main.java
```

Run:

```powershell
java -cp out Main
```

## Features
- Shared order data across all three screens
- View, update and cancel orders
- Completed orders automatically appear in Manage Sales
- Sales total is calculated from completed orders
- Delivery address, phone and status can be updated
- Form validation and confirmation/error messages
- Tabbed navigation between screens (plain Swing JTabbedPane)

## Sample data
The app starts with five sample customer orders. You can edit these in `src/data/OrderStore.java`.

## Important data note
This version stores data in memory using an `ArrayList`. Changes are shared between screens while the app is running, but they reset when the application closes. MySQL persistence can be added as a separate next step.

## Project structure

```text
BakeryOrderingSystem_Final/
├── src/
│   ├── Main.java
│   ├── data/OrderStore.java
│   ├── models/Order.java
│   ├── screens/
│   │   ├── ManageOrdersScreen.java
│   │   ├── ManageSalesScreen.java
│   │   └── ManageDeliveryScreen.java
│   └── ui/Theme.java
└── README.md
```
