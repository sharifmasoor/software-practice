# Online Bakery Ordering System

A Java Swing desktop application with three connected screens (tabs):
- **View Products** - browse by category, choose a quantity, add to cart
- **Place Order** - review the cart, change quantities, remove items
- **Book Order** - enter customer and delivery details and book the order

## Requirements
- Java Development Kit (JDK) 8 or later. No database or extra libraries needed.

## Run in VS Code / PowerShell
Open the project root folder (`BakeryOrderingSystem`) and compile:

```powershell
javac -d out src\ui\Theme.java src\models\Product.java src\models\CartItem.java src\models\Booking.java src\data\Store.java src\controller\OrderController.java src\screens\ViewProductsScreen.java src\screens\PlaceOrderScreen.java src\screens\BookOrderScreen.java src\Main.java
```

Run:

```powershell
java -cp out Main
```

## Features
- Shared data across all three screens (cart, stock, bookings)
- Stock is checked when adding to the cart and again when booking; booking reduces stock
- Validation: name, 11-digit contact, address, valid future date (DD-MM-YYYY)
- Booking ID is generated (BK1001, BK1002, ...) and shown in the Booked Orders list
- Plain tabbed navigation (JTabbedPane); the "Go to ..." buttons move to the next tab

## Data note
Data is stored in memory (`ArrayList`) and resets when the app closes.
Sample products are in `src/data/Store.java`. A MySQL version can be added later by
replacing the lists in `Store.java` with database calls in `OrderController.java`.

## Structure
```text
BakeryOrderingSystem/
├── src/
│   ├── Main.java
│   ├── controller/OrderController.java
│   ├── data/Store.java
│   ├── models/ Product.java, CartItem.java, Booking.java
│   ├── screens/ ViewProductsScreen.java, PlaceOrderScreen.java, BookOrderScreen.java
│   └── ui/Theme.java
└── README.md
```
