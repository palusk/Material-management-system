DELIMITER //

CREATE OR REPLACE PROCEDURE GetProductsOrderQuantities()
BEGIN
    SELECT p.product_id, p.product_name, COALESCE(SUM(od.order_quantity), 0) AS total_ordered
    FROM products p
             LEFT JOIN order_details od ON p.product_id = od.product_id
    GROUP BY p.product_id, p.product_name;
END //

CREATE OR REPLACE PROCEDURE GetAllUsersWithDetails()
BEGIN
    SELECT u.user_id, u.username, e.first_name, e.last_name, e.email, e.position, w.warehouse_name
    FROM users u
             JOIN employees e ON u.employee_id = e.employee_id
             JOIN warehouses w ON e.warehouse_id = w.warehouse_id;
END //

CREATE OR REPLACE PROCEDURE UpdateEmployeePosition(
    IN p_user_id INT,
    IN p_new_position VARCHAR(50)
)
BEGIN
    UPDATE employees
    SET position = p_new_position
    WHERE user_id = p_user_id;
END //

CREATE OR REPLACE PROCEDURE GetAllOrdersWithDetails()
BEGIN
    SELECT po.order_id, po.order_date, os.status_name, e.first_name, e.last_name,
           p.product_name, od.order_quantity
    FROM pending_orders po
             JOIN order_status os ON po.status_id = os.status_id
             JOIN employees e ON po.warehouse_id = e.warehouse_id
             JOIN order_details od ON po.order_id = od.order_id
             JOIN products p ON od.product_id = p.product_id;
END //

CREATE OR REPLACE PROCEDURE GetTotalProductQuantityAcrossWarehouses(
    IN p_product_id INT
)
BEGIN
    SELECT p.product_name, COALESCE(SUM(ps.quantity), 0) AS total_quantity
    FROM products p
             LEFT JOIN products_in_stock ps ON p.product_id = ps.product_id
    WHERE p.product_id = p_product_id
    GROUP BY p.product_name;
END //

CREATE OR REPLACE PROCEDURE GetOrdersInWarehouse(
    IN p_warehouse_id INT
)
BEGIN
    SELECT po.order_id, po.order_date, os.status_name, e.first_name, e.last_name
    FROM pending_orders po
             JOIN order_status os ON po.status_id = os.status_id
             JOIN employees e ON po.warehouse_id = e.warehouse_id
    WHERE po.warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE GetAllWarehouses()
BEGIN
    SELECT *
    FROM warehouses;
END //

CREATE OR REPLACE PROCEDURE GetProductsStockInAllWarehouses()
BEGIN
    SELECT ps.product_id, p.product_name, ps.quantity, ps.warehouse_id, ps.expiration_date
    FROM products_in_stock ps
             JOIN products p ON ps.product_id = p.product_id;
END //

CREATE OR REPLACE PROCEDURE GetWarehouseInfo(
    IN p_warehouse_id INT
)
BEGIN
    SELECT *
    FROM warehouses
    WHERE warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE GetProductsOrderDetails()
BEGIN
    SELECT p.product_id, p.product_name, od.order_id, od.order_quantity
    FROM products p
             LEFT JOIN order_details od ON p.product_id = od.product_id;
END //

CREATE OR REPLACE PROCEDURE GetEmployeesInWarehouse(
    IN p_warehouse_id INT
)
BEGIN
    SELECT *
    FROM employees
    WHERE warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE GetProductInfo(
    IN p_product_id INT
)
BEGIN
    SELECT *
    FROM products
    WHERE product_id = p_product_id;
END //

CREATE OR REPLACE PROCEDURE GetOrderDetailsForOrder(
    IN p_order_id INT
)
BEGIN
    SELECT od.order_detail_id, p.product_name, od.order_quantity
    FROM order_details od
             JOIN products p ON od.product_id = p.product_id
    WHERE od.order_id = p_order_id;
END //

CREATE OR REPLACE PROCEDURE GetUsersInWarehouse(
    IN p_warehouse_id INT
)
BEGIN
    SELECT u.*, e.first_name, e.last_name
    FROM users u
             JOIN employees e ON u.employee_id = e.employee_id
    WHERE e.warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE GetProductsInStockInWarehouse(
    IN p_warehouse_id INT
)
BEGIN
    SELECT ps.*, p.product_name
    FROM products_in_stock ps
             JOIN products p ON ps.product_id = p.product_id
    WHERE ps.warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE InsertStagingProductInStock(
    IN p_product_id INT,
    IN p_quantity INT,
    IN p_warehouse_id INT,
    IN p_expiration_date DATE
)
BEGIN
    INSERT INTO staging_products_in_stock (
        product_id,
        quantity,
        warehouse_id,
        expiration_date,
        load_status
    )
    VALUES (
               p_product_id,
               p_quantity,
               p_warehouse_id,
               p_expiration_date,
               'Pending'
           );
END //

CREATE OR REPLACE PROCEDURE DeleteStagingProductInStock(
    IN p_staging_id INT
)
BEGIN
    DELETE FROM staging_products_in_stock
    WHERE staging_id = p_staging_id;
END //

CREATE OR REPLACE PROCEDURE DeleteProductInStock(
    IN p_product_id INT,
    IN p_warehouse_id INT
)
BEGIN
    DELETE FROM products_in_stock
    WHERE product_id = p_product_id AND warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE GetProductStockInfoInWarehouse(
    IN p_warehouse_id INT
)
BEGIN
    SELECT ps.product_id, p.product_name, ps.quantity, ps.expiration_date
    FROM products_in_stock ps
             JOIN products p ON ps.product_id = p.product_id
    WHERE ps.warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE DeleteEmployee(
    IN p_user_id INT
)
BEGIN
    DELETE FROM employees
    WHERE user_id = p_user_id;
END //

CREATE OR REPLACE PROCEDURE DeleteUser(
    IN p_user_id INT
)
BEGIN
    DELETE FROM users
    WHERE user_id = p_user_id;
END //

CREATE OR REPLACE PROCEDURE DeleteOrderStatus(
    IN p_status_id INT
)
BEGIN
    DELETE FROM order_status
    WHERE status_id = p_status_id;
END //

CREATE OR REPLACE PROCEDURE DeletePendingOrder(
    IN p_order_id INT
)
BEGIN
    DELETE FROM pending_orders
    WHERE order_id = p_order_id;
END //

CREATE OR REPLACE PROCEDURE DeleteProcessedStagingRows()
BEGIN
    DELETE FROM staging_products_in_stock WHERE load_status = 'Processed';
END //

CREATE OR REPLACE PROCEDURE DeleteErrorStagingRows()
BEGIN
    DELETE FROM staging_products_in_stock WHERE load_status = 'Error';
END //

CREATE OR REPLACE PROCEDURE DeletePendingStagingRows()
BEGIN
    DELETE FROM staging_products_in_stock WHERE load_status = 'Pending';
END //

CREATE OR REPLACE PROCEDURE DeleteAllStagingRows()
BEGIN
    DELETE FROM staging_products_in_stock;
END //

CREATE OR REPLACE PROCEDURE DeleteOrderDetail(
    IN p_order_detail_id INT
)
BEGIN
    DELETE FROM order_details
    WHERE order_detail_id = p_order_detail_id;
END //

CREATE OR REPLACE PROCEDURE DeleteProductInStockByWarehouse(
    IN p_product_id INT,
    IN p_warehouse_id INT
)
BEGIN
    DELETE FROM products_in_stock
    WHERE product_id = p_product_id AND warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE DeleteStagingProductInStockByWarehouse(
    IN p_product_id INT,
    IN p_warehouse_id INT
)
BEGIN
    DELETE FROM staging_products_in_stock
    WHERE product_id = p_product_id AND warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE UpdatePendingOrderStatus(
    IN p_order_id INT,
    IN p_new_status_id INT
)
BEGIN
    UPDATE pending_orders
    SET status_id = p_new_status_id
    WHERE order_id = p_order_id;
END //

CREATE OR REPLACE PROCEDURE UpdateProductInStockQuantity(
    IN p_product_id INT,
    IN p_warehouse_id INT,
    IN p_new_quantity INT
)
BEGIN
    UPDATE products_in_stock
    SET quantity = p_new_quantity
    WHERE product_id = p_product_id AND warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE GetTotalProductQuantities()
BEGIN
    SELECT p.product_id, p.product_name, SUM(ps.quantity) AS total_quantity
    FROM products p
             LEFT JOIN products_in_stock ps ON p.product_id = ps.product_id
    GROUP BY p.product_id, p.product_name;
END //

CREATE OR REPLACE PROCEDURE GetPendingOrdersWithDetails()
BEGIN
    SELECT po.order_id, po.order_date, os.status_name, e.first_name, e.last_name
    FROM pending_orders po
             JOIN order_status os ON po.status_id = os.status_id
             JOIN employees e ON po.warehouse_id = e.warehouse_id;
END //

CREATE OR REPLACE PROCEDURE InsertOrUpdateEmployee(
    IN p_user_id INT,
    IN p_first_name VARCHAR(50),
    IN p_last_name VARCHAR(50),
    IN p_email VARCHAR(100),
    IN p_position VARCHAR(50),
    IN p_warehouse_id INT
)
BEGIN
    INSERT INTO employees (user_id, first_name, last_name, email, position, warehouse_id)
    VALUES (p_user_id, p_first_name, p_last_name, p_email, p_position, p_warehouse_id)
    ON DUPLICATE KEY UPDATE
                         first_name = VALUES(first_name),
                         last_name = VALUES(last_name),
                         email = VALUES(email),
                         position = VALUES(position),
                         warehouse_id = VALUES(warehouse_id);
END //

CREATE OR REPLACE PROCEDURE InsertOrUpdateUser(
    IN p_employee_id INT,
    IN p_username VARCHAR(50),
    IN p_email VARCHAR(100),
    IN p_password_hash VARCHAR(255)
)
BEGIN
    INSERT INTO users (employee_id, username, email, password_hash)
    VALUES (p_employee_id, p_username, p_email, p_password_hash)
    ON DUPLICATE KEY UPDATE
                         username = VALUES(username),
                         email = VALUES(email),
                         password_hash = VALUES(password_hash);
END //

CREATE OR REPLACE PROCEDURE InsertOrUpdateOrderStatus(
    IN p_status_name VARCHAR(10)
)
BEGIN
    INSERT INTO order_status (status_name)
    VALUES (p_status_name)
    ON DUPLICATE KEY UPDATE
        status_name = VALUES(status_name);
END //

CREATE OR REPLACE PROCEDURE InsertOrUpdatePendingOrder(
    IN p_order_date DATE,
    IN p_status_id INT,
    IN p_warehouse_id INT
)
BEGIN
    INSERT INTO pending_orders (order_date, status_id, warehouse_id)
    VALUES (p_order_date, p_status_id, p_warehouse_id)
    ON DUPLICATE KEY UPDATE
                         status_id = VALUES(status_id),
                         warehouse_id = VALUES(warehouse_id);
END //

CREATE OR REPLACE PROCEDURE InsertOrUpdateOrderDetail(
    IN p_order_id INT,
    IN p_product_id INT,
    IN p_order_quantity INT
)
BEGIN
    INSERT INTO order_details (order_id, product_id, order_quantity)
    VALUES (p_order_id, p_product_id, p_order_quantity)
    ON DUPLICATE KEY UPDATE
        order_quantity = VALUES(order_quantity);
END //

CREATE OR REPLACE PROCEDURE InsertOrUpdateProduct(
    IN p_product_name VARCHAR(255),
    IN p_unit_of_measurement VARCHAR(50)
)
BEGIN
    INSERT INTO products (product_name, unit_of_measurement)
    VALUES (p_product_name, p_unit_of_measurement)
    ON DUPLICATE KEY UPDATE
        unit_of_measurement = VALUES(unit_of_measurement);
END //

CREATE OR REPLACE PROCEDURE InsertOrUpdateWarehouse(
    IN p_warehouse_name VARCHAR(255)
)
BEGIN
    INSERT INTO warehouses (warehouse_name)
    VALUES (p_warehouse_name)
    ON DUPLICATE KEY UPDATE
        warehouse_name = VALUES(warehouse_name);
END //

CREATE OR REPLACE PROCEDURE InsertOrUpdateProductInStock(
    IN p_product_id INT,
    IN p_quantity INT,
    IN p_warehouse_id INT,
    IN p_expiration_date DATE
)
BEGIN
    INSERT INTO products_in_stock (product_id, quantity, warehouse_id, expiration_date)
    VALUES (p_product_id, p_quantity, p_warehouse_id, p_expiration_date)
    ON DUPLICATE KEY UPDATE
                         quantity = VALUES(quantity) + quantity,
                         expiration_date = VALUES(expiration_date);
END //

CREATE OR REPLACE PROCEDURE DeleteProduct(
    IN p_product_name VARCHAR(255)
)
BEGIN
    DELETE FROM products
    WHERE product_name = p_product_name;
END //

CREATE OR REPLACE PROCEDURE DeleteWarehouse(
    IN p_warehouse_name VARCHAR(255)
)
BEGIN
    DELETE FROM warehouses
    WHERE warehouse_name = p_warehouse_name;
END //

CREATE OR REPLACE PROCEDURE DeleteProductInStock(
    IN p_product_id INT,
    IN p_warehouse_id INT
)
BEGIN
    DELETE FROM products_in_stock
    WHERE product_id = p_product_id AND warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE GetProductInfoInWarehouse(
    IN p_warehouse_id INT
)
BEGIN
    SELECT p.product_name, p.unit_of_measurement, ps.quantity, ps.expiration_date
    FROM products p
             JOIN products_in_stock ps ON p.product_id = ps.product_id
    WHERE ps.warehouse_id = p_warehouse_id;
END //


CREATE OR REPLACE PROCEDURE LoadRowIntoProductsInStock(
    IN p_product_id VARCHAR(50),
    IN p_quantity VARCHAR(20),
    IN p_warehouse_id VARCHAR(50),
    IN p_expiration_date VARCHAR(20)
)
BEGIN
    DECLARE v_warehouse_exists INT;
    DECLARE v_product_exists INT;
    DECLARE v_load_status VARCHAR(20);
    DECLARE v_error_message VARCHAR(255);

    START TRANSACTION;

    SELECT COUNT(*) INTO v_warehouse_exists
    FROM warehouses
    WHERE warehouse_id = p_warehouse_id;

    SELECT COUNT(*) INTO v_product_exists
    FROM products
    WHERE product_id = p_product_id;

    IF v_warehouse_exists = 1 AND v_product_exists = 1 AND
       p_quantity REGEXP '^[0-9]+$' AND
       p_expiration_date REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}$' THEN

        CALL InsertOrUpdateProductInStock(p_product_id, p_quantity, p_warehouse_id, p_expiration_date);

        SET v_load_status = 'Processed';

    ELSE

        SET v_load_status = 'Error';
        SET v_error_message = '';
        IF v_warehouse_exists = 0 THEN SET v_error_message = CONCAT(v_error_message,'Wrong product id;'); END IF;
        IF v_product_exists = 0 THEN SET v_error_message = CONCAT(v_error_message,'Wrong product id;'); END IF;
        IF NOT p_quantity REGEXP '^[0-9]+$' THEN SET v_error_message = CONCAT(v_error_message,'Wrong quantity;'); END IF;
        IF NOT p_expiration_date REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}$' THEN SET v_error_message = CONCAT(v_error_message,'Wrong exp date;'); END IF;

    END IF;

    UPDATE staging_products_in_stock
    SET load_status = v_load_status
      , error_message = v_error_message
    WHERE product_id = p_product_id
      AND warehouse_id = p_warehouse_id
      AND expiration_date = p_expiration_date;

    COMMIT;
END //


CREATE OR REPLACE PROCEDURE ProcessPendingRowsInStaging()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE p_product_id VARCHAR(50);
    DECLARE p_quantity VARCHAR(20);
    DECLARE p_warehouse_id VARCHAR(50);
    DECLARE p_expiration_date VARCHAR(20);

    -- Cursor for pending rows
    DECLARE cursor_pending_rows CURSOR FOR
        SELECT product_id, quantity, warehouse_id, expiration_date
        FROM staging_products_in_stock
        WHERE load_status = 'Pending';

    -- Declare continue handler for the cursor
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- Start a transaction
    START TRANSACTION;

    -- Open the cursor
    OPEN cursor_pending_rows;

    -- Start processing rows
    processing_rows: LOOP
        FETCH cursor_pending_rows INTO p_product_id, p_quantity, p_warehouse_id, p_expiration_date;

        IF done THEN
            LEAVE processing_rows;
        END IF;

        -- Call the procedure for each pending row
        CALL LoadRowIntoProductsInStock(p_product_id, p_quantity, p_warehouse_id, p_expiration_date);
    END LOOP;

    -- Close the cursor
    CLOSE cursor_pending_rows;

    -- Commit the transaction
    COMMIT;
END //

CREATE OR REPLACE PROCEDURE GetStagingErrorsSortedByTimestamp()
BEGIN
    SELECT *
    FROM staging_products_in_stock
    WHERE load_status = 'Error'
    ORDER BY load_timestamp;
END //

CREATE OR REPLACE PROCEDURE GetStagingTable()
BEGIN
    SELECT *
    FROM staging_products_in_stock
    ORDER BY load_timestamp;
END //

DELIMITER ;