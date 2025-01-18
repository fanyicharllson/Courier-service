# Courier-service

A GUI courier-service project built in Java (JavaFX).

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)
- [NB](#NB)

## Introduction
Courier-service is a graphical user interface (GUI) application designed to manage courier services. It is developed using Java with JavaFX for the frontend and sqlite3 for the backend. The database was design using dbbrowser.

## Features
- User-friendly GUI
- Manage courier orders
- Track delivery status
- Generate reports

## Installation
To install and run the project locally, follow these steps:

1. Clone the repository:
    ```sh
    git clone https://github.com/fanyicharllson/Courier-service.git
    ```

2. Navigate to the project directory:
    ```sh
    cd Courier-service
    ```
3. Create the following tables in db browser
   ```sh
       CREATE TABLE Company_Details (
    company_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    contact TEXT NOT NULL
    )
   ```    
   ```sh
        CREATE TABLE Delivery_Details (
    order_id INTEGER NOT NULL,
    delivery_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_status TEXT NOT NULL,
    hub_id INTEGER NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (hub_id) REFERENCES Hub_Rates(hub_id)
    )  
   ```    
   ```sh
        CREATE TABLE Non_Delivery_Details (
    order_id INTEGER NOT NULL,
    reason TEXT NOT NULL,
    attempt_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
    )
       
   ```    
   ```sh
    CREATE TABLE Reviews (
    review_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_email TEXT NOT NULL,
    order_id INTEGER NOT NULL,
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,
    FOREIGN KEY (user_email) REFERENCES users(email),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
    )
       
   ```    
   ```sh
        CREATE TABLE User_Addresses (
    address_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    full_name TEXT NOT NULL,
    street_address TEXT NOT NULL,
    city TEXT NOT NULL,
    state TEXT NOT NULL,
    zip_code TEXT NOT NULL,
    address_type TEXT CHECK (address_type IN ('Home', 'Work', 'Other')),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    )
       
   ```    
   ```sh
        CREATE TABLE cart (
    cart_id INTEGER PRIMARY KEY AUTOINCREMENT, -- Unique identifier for each cart entry
    product_id INTEGER NOT NULL,              -- Foreign key referencing the product
    quantity INTEGER NOT NULL,                -- Quantity of the product in the cart
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, user_email TEXT NOT NULL, -- Timestamp for when the product was added
    FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE
    )
       
   ```    
   ```sh
   CREATE TABLE orders (
    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, user_email TEXT NOT NULL, status TEXT DEFAULT 'Pending',
    FOREIGN KEY (product_id) REFERENCES products(product_id)
    )
       
   ```
```sh
CREATE TABLE products (
    product_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price REAL NOT NULL,
    stock INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
, imagePath TEXT, likes INTEGER DEFAULT 0, quantity INTEGER DEFAULT 1)
)

```  
```sh
CREATE TABLE users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
, image_path VARCHAR(1000))
)
```  
```sh
CREATE TABLE Hub_Rates (
    hub_id INTEGER PRIMARY KEY AUTOINCREMENT,
    location TEXT NOT NULL,
    rate_per_km REAL NOT NULL
)

```
 

3. Open the project in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).

4. Build and run the project.
   
5. ## NB
   Make sure you update the database locations in the services folder(containing two files) containing the db locations

## Usage
Once the project is set up, you can use the application to:
- Create and manage courier orders.
- Track the status of deliveries.
- Generate and view reports.

## Contributing
Contributions are welcome! If you would like to contribute, please fork the repository and submit a pull request. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository.
2. Create your feature branch:
    ```sh
    git checkout -b feature/YourFeature
    ```
3. Commit your changes:
    ```sh
    git commit -m 'Add some feature'
    ```
4. Push to the branch:
    ```sh
    git push origin feature/YourFeature
    ```
5. Open a pull request.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
