-- Database tables for the Online Bakery Ordering System (MySQL)
DROP DATABASE IF EXISTS bakerydb;
CREATE DATABASE bakerydb;
USE bakerydb;

CREATE TABLE products (
  product_id  INT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL UNIQUE,
  category    VARCHAR(30)  NOT NULL,
  price       DECIMAL(10,2) NOT NULL,
  stock       INT NOT NULL DEFAULT 0,
  description VARCHAR(255)
);

CREATE TABLE salesmen (
  salesman_id INT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL,
  phone       VARCHAR(15)  NOT NULL,
  area        VARCHAR(50)  NOT NULL
);

-- one row per booked order (Place Order + Book Order)
CREATE TABLE bookings (
  booking_id     VARCHAR(10) PRIMARY KEY,            -- BK1001
  customer_name  VARCHAR(100) NOT NULL,
  contact        VARCHAR(11)  NOT NULL,
  address        VARCHAR(255) NOT NULL,
  total          DECIMAL(10,2) NOT NULL,
  delivery_date  DATE NOT NULL,
  delivery_time  VARCHAR(10) NOT NULL,
  instructions   VARCHAR(255),
  status         VARCHAR(20) NOT NULL DEFAULT 'Confirmed',
  payment_status ENUM('Unpaid','Pending','Paid','Rejected') NOT NULL DEFAULT 'Unpaid',
  salesman_id    INT NULL,                           -- Assign Salesman
  FOREIGN KEY (salesman_id) REFERENCES salesmen(salesman_id)
);

CREATE TABLE booking_items (
  booking_id VARCHAR(10) NOT NULL,
  product_id INT NOT NULL,
  quantity   INT NOT NULL CHECK (quantity > 0),
  unit_price DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (booking_id, product_id),
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
  FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Make Payment creates a row (Pending); Process Payment approves or rejects it
CREATE TABLE payments (
  payment_id VARCHAR(10) PRIMARY KEY,                -- PY2001
  booking_id VARCHAR(10) NOT NULL,
  payer_name VARCHAR(100) NOT NULL,
  method     ENUM('Cash on Delivery','Credit/Debit Card','Easypaisa','JazzCash','Bank Transfer') NOT NULL,
  reference  VARCHAR(20) NOT NULL DEFAULT '-',
  amount     DECIMAL(10,2) NOT NULL,
  pay_date   DATE NOT NULL,
  status     ENUM('Pending','Approved','Rejected') NOT NULL DEFAULT 'Pending',
  remarks    VARCHAR(255) DEFAULT '',
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

INSERT INTO salesmen (name, phone, area) VALUES
 ('Ali Raza','03001234567','Gulberg'),
 ('Usman Khan','03111234567','Saddar'),
 ('Bilal Ahmed','03211234567','University Town');
