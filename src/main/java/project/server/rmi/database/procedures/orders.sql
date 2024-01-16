DELIMITER //

CREATE OR REPLACE PROCEDURE GetProductsOrderQuantities()
BEGIN
    SELECT p.product_id, p.product_name, COALESCE(SUM(od.order_quantity), 0) AS total_ordered
    FROM products p
             LEFT JOIN order_details od ON p.product_id = od.product_id
    GROUP BY p.product_id, p.product_name;
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

CREATE OR REPLACE PROCEDURE GetOrdersInWarehouse(
    IN p_warehouse_id INT
)
BEGIN
    SELECT CONCAT(po.order_id,' - ', po.order_date,' - ', os.status_name,';')
    FROM pending_orders po
             JOIN order_status os ON po.status_id = os.status_id
    WHERE po.warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE GetProductsOrderDetails()
BEGIN
    SELECT p.product_id, p.product_name, od.order_id, od.order_quantity
    FROM products p
             LEFT JOIN order_details od ON p.product_id = od.product_id;
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

CREATE OR REPLACE PROCEDURE DeleteOrderDetail(
    IN p_order_detail_id INT
)
BEGIN
    DELETE FROM order_details
    WHERE order_detail_id = p_order_detail_id;
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

CREATE OR REPLACE PROCEDURE GetPendingOrdersWithDetails()
BEGIN
    SELECT po.order_id, po.order_date, os.status_name, e.first_name, e.last_name
    FROM pending_orders po
             JOIN order_status os ON po.status_id = os.status_id
             JOIN employees e ON po.warehouse_id = e.warehouse_id;
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

CREATE OR REPLACE PROCEDURE createOrder(
    IN p_order_id INT
)
BEGIN

    INSERT INTO pending_orders (order_date, status_id, warehouse_id, ordering_warehouse_id)
    SELECT DISTINCT CURRENT_DATE, 1, warehouse_id, ordering_warehouse_id
    FROM staging_orders
    WHERE order_id = p_order_id;

    SET @last_order_id = LAST_INSERT_ID();

    INSERT INTO order_details (order_id, product_id, order_quantity)
    SELECT
        @last_order_id,
        product_id,
        order_quantity
    FROM
        staging_orders
    WHERE
            order_id = p_order_id;
END//

CREATE OR REPLACE PROCEDURE insertStagingOrder(inputString TEXT)
BEGIN
    DECLARE value1 INT;
    DECLARE value2 INT;
    DECLARE value3 INT;
    DECLARE value4 INT;
    DECLARE orderid INT;
    DECLARE startIndex INT DEFAULT 1;
    DECLARE nextIndex INT;

    -- Rozpoczęcie transakcji
    START TRANSACTION;

    SELECT COALESCE(MAX(order_id) + 1, 0) INTO orderid FROM staging_orders;


    -- Tworzenie tymczasowej tabeli
    CREATE TEMPORARY TABLE IF NOT EXISTS temp_table (
                                                        column1 INT,
                                                        column2 INT,
                                                        column3 INT,
                                                        column4 INT
    );

    WHILE startIndex <= LENGTH(inputString) DO
            SET nextIndex = LOCATE(';', inputString, startIndex);

            IF nextIndex = 0 THEN
                SET nextIndex = LENGTH(inputString) + 1;
            END IF;

            SET value1 = SUBSTRING(inputString, startIndex, nextIndex - startIndex);
            SET startIndex = nextIndex + 1;

            SET nextIndex = LOCATE(';', inputString, startIndex);

            IF nextIndex = 0 THEN
                SET nextIndex = LENGTH(inputString) + 1;
            END IF;

            SET value2 = SUBSTRING(inputString, startIndex, nextIndex - startIndex);
            SET startIndex = nextIndex + 1;

            SET nextIndex = LOCATE(';', inputString, startIndex);

            IF nextIndex = 0 THEN
                SET nextIndex = LENGTH(inputString) + 1;
            END IF;

            SET value3 = SUBSTRING(inputString, startIndex, nextIndex - startIndex);
            SET startIndex = nextIndex + 1;

            SET nextIndex = LOCATE(';', inputString, startIndex);

            IF nextIndex = 0 THEN
                SET nextIndex = LENGTH(inputString) + 1;
            END IF;

            SET value4 = SUBSTRING(inputString, startIndex, nextIndex - startIndex);
            SET startIndex = nextIndex + 1;


            INSERT INTO staging_orders (order_id, product_id, warehouse_id, order_quantity, ordering_warehouse_id) VALUES (orderid, value1, value2, value3, value4);
        END WHILE;

    CALL createOrder(orderid);

    COMMIT;

    DROP TEMPORARY TABLE IF EXISTS temp_table;

END //

CREATE OR REPLACE PROCEDURE getOrderDetails(
    IN p_order_id INT
)
BEGIN
    SELECT pr.product_name as 'Product name', os.order_quantity as 'Ordered quantity', pr.unit_of_measurement as 'Unit of measurement'
    FROM pending_orders po
             JOIN order_details os ON po.order_id = os.order_id
             JOIN products pr ON pr.product_id = os.product_id
    WHERE po.order_id = p_order_id;
END //

CREATE OR REPLACE PROCEDURE cancelOrder(
    IN p_order_id INT
)
BEGIN
    UPDATE pending_orders po
    SET po.status_id = 3
    WHERE po.order_id = p_order_id;
END //

CREATE OR REPLACE PROCEDURE completeOrder(
    IN p_order_id INT
)
BEGIN
    DECLARE source_warehouse_id INT;
    DECLARE transferred_product_id INT;
    DECLARE transferred_quantity INT;
    DECLARE v_warehouse_id INT;
    DECLARE v_ordering_warehouse_id INT;
    DECLARE v_product_id INT;
    DECLARE v_order_quantity INT;
    DECLARE v_expiration_date DATE;
    DECLARE done INT DEFAULT FALSE;

    DECLARE cur CURSOR FOR
        SELECT po.warehouse_id, od.product_id, od.order_quantity
        FROM order_details od
                 JOIN pending_orders po ON od.order_id = po.order_id
        WHERE od.order_id = p_order_id;

    DECLARE cur_orders CURSOR FOR
        SELECT po.warehouse_id, po.ordering_warehouse_id, od.product_id, pl.quantity, pl.expiration_date
        FROM order_details od
                 JOIN pending_orders po ON od.order_id = po.order_id
                 JOIN products p ON od.product_id = p.product_id
                 JOIN products_list pl ON p.product_name = pl.product_name
        WHERE od.order_id = p_order_id;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    CREATE TEMPORARY TABLE IF NOT EXISTS products_list_transfer (
                                                                    expiration_date DATE,
                                                                    quantity INT,
                                                                    product_name VARCHAR(255)
    );

    START TRANSACTION;
    UPDATE pending_orders po
    SET po.status_id = 2
    WHERE po.order_id = p_order_id;


    CREATE TEMPORARY TABLE IF NOT EXISTS products_list (
                                                           expiration_date DATE,
                                                           quantity INT,
                                                           product_name VARCHAR(255)
    );
    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO source_warehouse_id, transferred_product_id, transferred_quantity;

        IF done THEN
            LEAVE read_loop;
        END IF;

        CALL listProductsToTransferForSingleRow(source_warehouse_id, transferred_product_id, transferred_quantity);
    END LOOP read_loop;
    SELECT * FROM products_list;
    CLOSE cur;

    SET done = FALSE;

    OPEN cur_orders;

    read_loop: LOOP
        FETCH cur_orders INTO v_warehouse_id, v_ordering_warehouse_id, v_product_id, v_order_quantity, v_expiration_date;

        IF done THEN
            LEAVE read_loop;
        END IF;

        IF v_expiration_date is not null THEN
            CALL transferProduct(v_warehouse_id, v_ordering_warehouse_id, v_product_id, v_order_quantity, v_expiration_date);
        END IF;
    END LOOP;

    CLOSE cur_orders;

    DROP TEMPORARY TABLE IF EXISTS products_list;

    COMMIT;

END//

DELIMITER ;