DELIMITER //

CREATE OR REPLACE PROCEDURE InitDatabase()
BEGIN

    CREATE OR REPLACE TABLE warehouses (
                                           warehouse_id INT AUTO_INCREMENT PRIMARY KEY,
                                           warehouse_name VARCHAR(255) NOT NULL
    );

    CREATE OR REPLACE TABLE employees (
                                          employee_id INT AUTO_INCREMENT PRIMARY KEY,
                                          user_id INT UNIQUE,
                                          first_name VARCHAR(50) NOT NULL,
                                          last_name VARCHAR(50) NOT NULL,
                                          email VARCHAR(100) NOT NULL,
                                          position VARCHAR(50),
                                          warehouse_id INT,
                                          reports_to INT,
                                          FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id)
    );

    CREATE OR REPLACE TABLE users (
                                      user_id INT AUTO_INCREMENT PRIMARY KEY,
                                      employee_id INT,
                                      username VARCHAR(50) NOT NULL,
                                      email VARCHAR(100) NOT NULL,
                                      password_hash VARCHAR(255) NOT NULL,
                                      registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
    );

    CREATE OR REPLACE TABLE order_status (
                                             status_id INT AUTO_INCREMENT PRIMARY KEY,
                                             status_name VARCHAR(10) NOT NULL
    );

    CREATE OR REPLACE TABLE products (
                                         product_id INT AUTO_INCREMENT PRIMARY KEY,
                                         product_name VARCHAR(255) NOT NULL,
                                         unit_of_measurement VARCHAR(50) NOT NULL
    );

    CREATE OR REPLACE TABLE pending_orders (
                                               order_id INT AUTO_INCREMENT PRIMARY KEY,
                                               order_date DATE,
                                               status_id INT,
                                               warehouse_id INT,
                                               FOREIGN KEY (status_id) REFERENCES order_status(status_id),
                                               FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id)
    );

    CREATE OR REPLACE TABLE order_details (
                                              order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
                                              order_id INT,
                                              product_id INT,
                                              order_quantity INT,
                                              FOREIGN KEY (order_id) REFERENCES pending_orders(order_id),
                                              FOREIGN KEY (product_id) REFERENCES products(product_id)
    );

    CREATE OR REPLACE TABLE products_in_stock (
                                                  product_id INT,
                                                  quantity INT,
                                                  warehouse_id INT,
                                                  expiration_date DATE,
                                                  PRIMARY KEY (product_id, warehouse_id, expiration_date),
                                                  FOREIGN KEY (product_id) REFERENCES products(product_id),
                                                  FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id)
    );

    CREATE OR REPLACE TABLE staging_products_in_stock (
                                                          staging_id INT AUTO_INCREMENT PRIMARY KEY,
                                                          product_id INT,
                                                          quantity INT,
                                                          warehouse_id INT,
                                                          expiration_date DATE,
                                                          load_status ENUM('Pending', 'Processed', 'Error') DEFAULT 'Pending',
                                                          load_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                          error_message VARCHAR(255)
    );

    CREATE OR REPLACE TABLE staging_employees (
                                                  staging_id INT AUTO_INCREMENT PRIMARY KEY,
                                                  first_name VARCHAR(50) NOT NULL,
                                                  last_name VARCHAR(50) NOT NULL,
                                                  email VARCHAR(100) NOT NULL,
                                                  position VARCHAR(50),
                                                  warehouse_id INT,
                                                  load_status ENUM('Pending', 'Processed', 'Error') DEFAULT 'Pending',
                                                  load_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                  error_message VARCHAR(255)
    );

    CREATE OR REPLACE TABLE employee_hierarchy (
                                                   employee_id INT,
                                                   descendant_id INT,
                                                   level INT,
                                                   warehouse_id INT,
                                                   PRIMARY KEY (employee_id, descendant_id),
                                                   FOREIGN KEY (descendant_id) REFERENCES employees(employee_id)
    );

    CREATE OR REPLACE TABLE profiles (
                                         profile_id INT PRIMARY KEY,
                                         profile_name VARCHAR(50) NOT NULL
    );

    INSERT INTO profiles (profile_id, profile_name)
    VALUES
        (0, 'Manager'),
        (1, 'Supervisor'),
        (2, 'Senior Worker'),
        (3, 'Worker');

    CREATE OR REPLACE TABLE users_profiles (
                                               profile_id INT,
                                               employee_id INT,
                                               PRIMARY KEY (profile_id, employee_id),
                                               FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
                                               FOREIGN KEY (profile_id) REFERENCES profiles(profile_id)
    );

END //

DELIMITER ;