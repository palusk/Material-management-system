DELIMITER //




CREATE OR REPLACE PROCEDURE updateIncorrectFile(
    IN p_product_id INT,
    IN p_quantity INT,
    IN p_warehouse_id INT,
    IN p_expiration_date VARCHAR(50)
)
BEGIN
    -- Wstawienie danych do tabeli staging_products_in_stock

    UPDATE staging_products_in_stock
    SET load_status = 'incorrect file'
    WHERE load_status = 'Pending';

END //

CREATE OR REPLACE PROCEDURE InsertStagingProductInStock(
    IN p_product_id INT,
    IN p_quantity INT,
    IN p_warehouse_id INT,
    IN p_expiration_date VARCHAR(50)
)
BEGIN
    DECLARE exit handler FOR SQLEXCEPTION
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
                       0,  -- default to 0 quantity
                       p_warehouse_id,
                       DATE_FORMAT(STR_TO_DATE(p_expiration_date, '%d.%m.%Y'), '%Y-%m-%d'),
                       'row error'
                   );
        END;


    IF p_quantity IS NULL OR p_quantity < 0 THEN
        INSERT INTO staging_products_in_stock (
            product_id,
            quantity,
            warehouse_id,
            expiration_date,
            load_status
        )
        VALUES (
                   p_product_id,
                   0,
                   p_warehouse_id,
                   DATE_FORMAT(STR_TO_DATE(p_expiration_date, '%d.%m.%Y'), '%Y-%m-%d'),
                   'InvalidQuantity'
               );
    ELSE
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
                   DATE_FORMAT(STR_TO_DATE(p_expiration_date, '%d.%m.%Y'), '%Y-%m-%d'),
                   'Pending'
               );
    END IF;
END //

CREATE OR REPLACE PROCEDURE DeleteAllProductStagingRows()
BEGIN
    DELETE FROM staging_products_in_stock;
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
                         quantity = VALUES(quantity) + quantity;
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
        IF v_warehouse_exists = 0 THEN SET v_error_message = CONCAT(v_error_message,'Wrong warehouse id;'); END IF;
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

CREATE OR REPLACE PROCEDURE ProcessPendingRowsInProductStaging()
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

CREATE OR REPLACE PROCEDURE GetProductStagingErrors()
BEGIN
    SELECT *
    FROM staging_products_in_stock
    WHERE load_status = 'Error'
    ORDER BY load_timestamp;
END //

CREATE OR REPLACE PROCEDURE GetProductStagingTable()
BEGIN
    SELECT *
    FROM staging_products_in_stock
    ORDER BY load_timestamp;
END //

DELIMITER ;