DELIMITER //

CREATE OR REPLACE PROCEDURE getTransferHistory(
)
BEGIN
    SELECT *
    FROM transfers_histo;
END //

CREATE OR REPLACE PROCEDURE GetProductInfo(
    IN p_product_id INT
)
BEGIN
    SELECT *
    FROM products
    WHERE product_id = p_product_id;
END //



CREATE OR REPLACE PROCEDURE getAllProductsAdmin()
BEGIN
    SELECT *
    FROM products;
END //

CREATE OR REPLACE PROCEDURE GetAllProducts()
BEGIN
    SELECT CONCAT(product_id,' ',product_name)
    FROM products;
END //

CREATE OR REPLACE PROCEDURE getAllProductsInStock()
BEGIN
    SELECT *
    FROM products_in_stock;
END //

CREATE OR REPLACE PROCEDURE transferProduct(
    source_warehouse_id INT,
    destination_warehouse_id INT,
    transferred_product_id INT,
    transferred_quantity INT,
    transferred_expiration_date DATE
)
BEGIN
    DECLARE available_quantity INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
        END;

    START TRANSACTION;

    -- Get available quantity from the source warehouse
    SELECT quantity INTO available_quantity
    FROM products_in_stock
    WHERE warehouse_id = source_warehouse_id
      AND product_id = transferred_product_id
      AND expiration_date = transferred_expiration_date;

    -- Check if there is enough quantity to transfer
    IF transferred_quantity <= available_quantity THEN
        -- Deduct transferred quantity from the source warehouse
        UPDATE products_in_stock
        SET quantity = quantity - transferred_quantity
        WHERE warehouse_id = source_warehouse_id
          AND product_id = transferred_product_id
          AND expiration_date = transferred_expiration_date;

        -- Check if the product exists in the destination warehouse
        IF EXISTS (
                SELECT product_id
                FROM products_in_stock
                WHERE warehouse_id = destination_warehouse_id
                  AND product_id = transferred_product_id
                  AND expiration_date = transferred_expiration_date
            ) THEN

            -- If the product exists, update its quantity
            UPDATE products_in_stock
            SET quantity = quantity + transferred_quantity
            WHERE warehouse_id = destination_warehouse_id
              AND product_id = transferred_product_id
              AND expiration_date = transferred_expiration_date;
        ELSE
            -- If the product does not exist, insert a new record
            INSERT INTO products_in_stock (warehouse_id, product_id, expiration_date, quantity)
            VALUES (destination_warehouse_id, transferred_product_id, transferred_expiration_date, transferred_quantity);
        END IF;

        COMMIT;
    ELSE
        ROLLBACK;
    END IF;
END //


CREATE OR REPLACE PROCEDURE listProductsToTransfer(
    IN in_order_id INT
)
BEGIN

    DECLARE source_warehouse_id INT;
    DECLARE transferred_product_id INT;
    DECLARE transferred_quantity INT;
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur CURSOR FOR

        SELECT po.warehouse_id, od.product_id, od.order_quantity
        FROM order_details od
                 JOIN pending_orders po ON od.order_id = po.order_id
        WHERE od.order_id = in_order_id;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
        END;

    START TRANSACTION;

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
    DROP TEMPORARY TABLE IF EXISTS products_list;
    CLOSE cur;
    COMMIT;
END //

CREATE OR REPLACE PROCEDURE listProductsToTransferForSingleRow(
    source_warehouse_id INT,
    transferred_product_id INT,
    transferred_quantity INT
)
BEGIN
    DECLARE available_quantity INT;
    DECLARE name VARCHAR(255);
    DECLARE expiration_date DATE;
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur CURSOR FOR SELECT * FROM temp_available_products;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    CREATE TEMPORARY TABLE IF NOT EXISTS temp_available_products (
                                                                     expiration_date DATE,
                                                                     quantity INT
    );

    CREATE TEMPORARY TABLE IF NOT EXISTS products_list (
                                                           expiration_date DATE,
                                                           quantity INT,
                                                           product_name VARCHAR(255)
    );

    SELECT p.product_name INTO name
    FROM products p
    WHERE p.product_id= transferred_product_id;

    INSERT INTO temp_available_products (expiration_date, quantity)
    SELECT pis.expiration_date, pis.quantity
    FROM products_in_stock pis
    WHERE pis.warehouse_id = source_warehouse_id
      AND pis.product_id = transferred_product_id
      AND pis.expiration_date > NOW()
    ORDER BY pis.expiration_date ASC;

    OPEN cur;

    product_loop: LOOP
        FETCH cur INTO expiration_date, available_quantity;


        IF done THEN
            INSERT INTO products_list
            SELECT null , CONCAT('-',transferred_quantity), name;
            LEAVE product_loop;
        ELSE
            IF transferred_quantity <= available_quantity THEN
                INSERT INTO products_list
                SELECT expiration_date, transferred_quantity, name;
                LEAVE product_loop;
            ELSE
                INSERT INTO products_list
                SELECT expiration_date, available_quantity, name;
                SET transferred_quantity = transferred_quantity - available_quantity;
            END IF;
        END IF;

    END LOOP product_loop;

    CLOSE cur;

    DROP TEMPORARY TABLE IF EXISTS temp_available_products;

END //

CREATE OR REPLACE PROCEDURE execDelete(
    IN p_id VARCHAR(255),
    IN p_table VARCHAR(255),
    IN p_firstValue VARCHAR(255),
    IN p_secondValue VARCHAR(255)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            SET foreign_key_checks = 1;
            ROLLBACK;
        END;


    DECLARE sql_statement VARCHAR(500);
    DECLARE id_exists INT;

    START TRANSACTION;

    SET foreign_key_checks = 0;

    IF p_table = 'pending_orders' AND NULLIF(p_id, '') IS NOT NULL  THEN

            SET @sql_statement := CONCAT('DELETE FROM ', p_table, ' WHERE order_id = ?');
            PREPARE stmt FROM @sql_statement;
            EXECUTE stmt USING p_id;
            DEALLOCATE PREPARE stmt;

    ELSEIF p_table = 'products_in_stock' AND NULLIF(p_id, '') IS NOT NULL  AND NULLIF(p_firstValue, '') IS NOT NULL AND NULLIF(p_secondValue, '') IS NOT NULL THEN

        SET @sql_statement := CONCAT('DELETE FROM ', p_table, ' WHERE product_id = ? and expiration_date = ? and warehouse_id = ?');
        PREPARE stmt FROM @sql_statement;
        EXECUTE stmt USING p_id, p_secondValue, p_firstValue;
        DEALLOCATE PREPARE stmt;

    ELSEIF p_table = 'employees' AND NULLIF(p_id, '') IS NOT NULL THEN

        SET @sql_statement := CONCAT('DELETE FROM ', p_table, ' WHERE employee_id = ?');
        PREPARE stmt FROM @sql_statement;
        EXECUTE stmt USING p_id;
        DEALLOCATE PREPARE stmt;

        SET @sql_statement := CONCAT('DELETE FROM users WHERE employee_id = ?');
        PREPARE stmt FROM @sql_statement;
        EXECUTE stmt USING p_id;
        DEALLOCATE PREPARE stmt;

        SET @sql_statement := CONCAT('DELETE FROM users_profiles WHERE employee_id = ?');
        PREPARE stmt FROM @sql_statement;
        EXECUTE stmt USING p_id;
        DEALLOCATE PREPARE stmt;

        END IF;

    SET foreign_key_checks = 1;

    COMMIT;
END //

CREATE OR REPLACE PROCEDURE execAdd(
    IN p_id VARCHAR(255),
    IN p_table VARCHAR(255),
    IN p_firstValue VARCHAR(255),
    IN p_secondValue VARCHAR(255)
)
BEGIN
    DECLARE sql_statement VARCHAR(500);
    DECLARE id_exists INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            SET foreign_key_checks = 1;
            ROLLBACK;
        END;

    START TRANSACTION;

    SET foreign_key_checks = 0;

    IF p_table = 'products' AND NULLIF(p_firstValue, '') IS NOT NULL AND NULLIF(p_secondValue, '') IS NOT NULL THEN

        SET @sql_statement := CONCAT('INSERT INTO products (product_name, unit_of_measurement) VALUES (?,?)');
        PREPARE stmt FROM @sql_statement;
        EXECUTE stmt USING p_firstValue, p_secondValue;
        DEALLOCATE PREPARE stmt;

    ELSEIF p_table = 'warehouses' AND NULLIF(p_firstValue, '') IS NOT NULL THEN

        SET @sql_statement := CONCAT('INSERT INTO warehouses (warehouse_name) VALUES (?)');
        PREPARE stmt FROM @sql_statement;
        EXECUTE stmt USING p_firstValue;
        DEALLOCATE PREPARE stmt;

    END IF;

    SET foreign_key_checks = 1;
    COMMIT;
END //



CREATE OR REPLACE PROCEDURE execEdit(
    IN p_id VARCHAR(255),
    IN p_table VARCHAR(255),
    IN p_firstValue VARCHAR(255),
    IN p_secondValue VARCHAR(255),
    IN p_thirdValue VARCHAR(255)
)
BEGIN
    DECLARE sql_statement VARCHAR(500);
    DECLARE id_exists INT;
    DECLARE p_date DATE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            SET foreign_key_checks = 1;
            ROLLBACK;
        END;

    START TRANSACTION;

    SET foreign_key_checks = 0;

    IF p_table = 'products' AND NULLIF(p_id, '') IS NOT NULL AND NULLIF(p_firstValue, '') IS NOT NULL AND NULLIF(p_secondValue, '') IS NOT NULL THEN

        SET @sql_statement := CONCAT('UPDATE products SET product_name = ?, unit_of_measurement = ? WHERE product_id = ?');
        PREPARE stmt FROM @sql_statement;
        EXECUTE stmt USING p_firstValue, p_secondValue, p_id;
        DEALLOCATE PREPARE stmt;

    ELSEIF p_table = 'warehouses' AND NULLIF(p_id, '') IS NOT NULL AND NULLIF(p_firstValue, '') IS NOT NULL THEN

        SET @sql_statement := CONCAT('UPDATE warehouses SET warehouse_name = ? WHERE warehouse_id = ?');
        PREPARE stmt FROM @sql_statement;
        EXECUTE stmt USING p_firstValue, p_id;
        DEALLOCATE PREPARE stmt;

    ELSEIF p_table = 'products_in_stock' AND NULLIF(p_id, '') IS NOT NULL AND NULLIF(p_secondValue, '') IS NOT NULL
        AND NULLIF(p_firstValue, '') IS NOT NULL AND NULLIF(p_thirdValue, '') IS NOT NULL THEN

        SET p_date = STR_TO_DATE(p_secondValue, '%Y-%m-%d');

        SET @sql_statement := CONCAT('UPDATE products_in_stock SET quantity = ? WHERE warehouse_id = ? and expiration_date = ? and product_id = ?');
        PREPARE stmt FROM @sql_statement;
        EXECUTE stmt USING p_thirdValue, p_firstValue, p_date, p_id;
        DEALLOCATE PREPARE stmt;

    END IF;

    SET foreign_key_checks = 1;

    COMMIT;
END  //

DELIMITER ;