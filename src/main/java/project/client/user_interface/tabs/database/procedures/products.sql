DELIMITER //

CREATE OR REPLACE PROCEDURE GetProductInfo(
    IN p_product_id INT
)
BEGIN
    SELECT *
    FROM products
    WHERE product_id = p_product_id;
END //

CREATE OR REPLACE PROCEDURE GetAllProducts()
BEGIN
    SELECT CONCAT(product_id,' ',product_name)
    FROM products;
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

    START TRANSACTION;

    -- Get available quantity from the source warehouse
    SELECT quantity INTO available_quantity
    FROM products_in_stock
    WHERE warehouse_id = source_warehouse_id
      AND product_id = transferred_product_id
      AND expiration_date = transferred_expiration_date;

    SELECT available_quantity;

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

            SELECT 1;
            -- If the product exists, update its quantity
            UPDATE products_in_stock
            SET quantity = quantity + transferred_quantity
            WHERE warehouse_id = destination_warehouse_id
              AND product_id = transferred_product_id
              AND expiration_date = transferred_expiration_date;
        ELSE
            SELECT (destination_warehouse_id, transferred_product_id, transferred_expiration_date, transferred_quantity);
            SELECT 2;
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

DELIMITER ;