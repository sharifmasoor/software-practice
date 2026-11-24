# Online Bakery Ordering System

A Java Swing desktop application with six connected screens (tabs):
- **View Products** - browse by category, choose a quantity, add to cart
- **Place Order** - review the cart, change quantities, remove items
- **Book Order** - enter customer and delivery details and book the order
- **Make Payment** - pay for a booked order (method + reference number)
- **Process Payment** - approve or reject submitted payments
- **Assign Salesman** - assign a salesman to a paid booking

## Requirements
- Java Development Kit (JDK) 8 or later. No database or extra libraries needed.

## Run in VS Code / PowerShell
Open the project root folder (`BakeryOrderingSystem`) and compile:

```powershell
javac -d out src\ui\Theme.java src\models\Product.java src\models\CartItem.java src\models\Booking.java src\models\Payment.java src\models\Salesman.java src\data\Store.java src\controller\OrderController.java src\screens\ViewProductsScreen.java src\screens\PlaceOrderScreen.java src\screens\BookOrderScreen.java src\screens\MakePaymentScreen.java src\screens\ProcessPaymentScreen.java src\screens\AssignSalesmanScreen.java src\Main.java
```

Run:

```powershell
java -cp out Main
```

## Flow of the new use cases
1. Book an order (Book Order tab) - the booking starts as **Unpaid**.
2. **Make Payment** - select the booking, enter payer name, method and reference no. -> payment is **Pending**.
3. **Process Payment** - approve (booking becomes **Paid**) or reject with remarks (booking can be paid again).
4. **Assign Salesman** - only **Paid** bookings; a salesman can have at most 5 active deliveries.

## Features
- Shared data across all three screens (cart, stock, bookings)
- Stock is checked when adding to the cart and again when booking; booking reduces stock
- Validation: name, 11-digit contact, address, valid future date (DD-MM-YYYY)
- Booking ID is generated (BK1001, BK1002, ...) and shown in the Booked Orders list
- Plain tabbed navigation (JTabbedPane); the "Go to ..." buttons move to the next tab

## Data note
Data is stored in memory (`ArrayList`) and resets when the app closes.
Sample products are in `src/data/Store.java`. The database tables are in `schema.sql` (bookings, booking_items, payments, salesmen, products).
A MySQL version can be added by replacing the lists in `Store.java` with JDBC calls in `OrderController.java`.

## Structure
```text
BakeryOrderingSystem/
├── src/
│   ├── Main.java
│   ├── controller/OrderController.java
│   ├── data/Store.java
│   ├── models/ Product, CartItem, Booking, Payment, Salesman
│   ├── screens/ ViewProducts, PlaceOrder, BookOrder, MakePayment, ProcessPayment, AssignSalesman (…Screen.java)
│   └── ui/Theme.java
├── schema.sql
└── README.md
```
