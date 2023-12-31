DELIMITER //

CREATE OR REPLACE PROCEDURE DeleteProductInStock(
    IN p_product_id INT,
    IN p_warehouse_id INT
)
BEGIN
    DELETE FROM products_in_stock
    WHERE product_id = p_product_id AND warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE DeleteProduct(
    IN p_product_name VARCHAR(255)
)
BEGIN
    DELETE FROM products
    WHERE product_name = p_product_name;
END //

CREATE OR REPLACE PROCEDURE DeleteProductInStockByWarehouse(
    IN p_product_id INT,
    IN p_warehouse_id INT
)
BEGIN
    DELETE FROM products_in_stock
    WHERE product_id = p_product_id AND warehouse_id = p_warehouse_id;
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

CREATE OR REPLACE PROCEDURE GetProductInfo(
    IN p_product_id INT
)
BEGIN
    SELECT *
    FROM products
    WHERE product_id = p_product_id;
END //

CREATE OR REPLACE PROCEDURE GetProductsStockInAllWarehouses()
BEGIN
    SELECT ps.product_id, p.product_name, ps.quantity, ps.warehouse_id, ps.expiration_date
    FROM products_in_stock ps
             JOIN products p ON ps.product_id = p.product_id;
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

CREATE OR REPLACE PROCEDURE GetProductsInStockInWarehouse(
    IN p_warehouse_id INT
)
BEGIN
    SELECT ps.*, p.product_name
    FROM products_in_stock ps
             JOIN products p ON ps.product_id = p.product_id
    WHERE ps.warehouse_id = p_warehouse_id;
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

CREATE OR REPLACE PROCEDURE transferProduct(
    source_warehouse_id INT,
    destination_warehouse_id INT,
    transferred_product_id INT,
    transferred_quantity INT,
    transferred_expiration_date INT
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
    source_warehouse_id INT,
    destination_warehouse_id INT,
    transferred_product_id INT,
    transferred_quantity INT
)
BEGIN
    DECLARE quantity_sum INT;
    DECLARE available_quantity INT;
    DECLARE expired_quantity INT;
    DECLARE expiration_date DATE;
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur CURSOR FOR SELECT * FROM temp_available_products;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    START TRANSACTION;

    -- Create a temporary table to store available products
    CREATE TEMPORARY TABLE IF NOT EXISTS temp_available_products (
                                                                     expiration_date DATE,
                                                                     quantity INT
    );

    -- Calculate the total quantity of the transferred product in the source warehouse
    SELECT SUM(quantity) INTO quantity_sum
    FROM products_in_stock
    WHERE warehouse_id = source_warehouse_id
      AND product_id = transferred_product_id
      AND expiration_date > NOW();

    -- Check if there is enough quantity to transfer
    IF quantity_sum >= transferred_quantity THEN

        -- Insert available products into the temporary table
        INSERT INTO temp_available_products (expiration_date, quantity)
        SELECT expiration_date, quantity
        FROM products_in_stock
        WHERE warehouse_id = source_warehouse_id
          AND product_id = transferred_product_id
          AND expiration_date > NOW()
        ORDER BY expiration_date ASC;

        OPEN cur;

        product_loop: LOOP
            FETCH cur INTO expiration_date, available_quantity;

            -- Check if there is enough quantity to transfer
            IF transferred_quantity <= available_quantity THEN
                -- Display or process the available product data as needed
                SELECT expiration_date, available_quantity;
                LEAVE product_loop;
            ELSE
                SELECT expiration_date, available_quantity;
                SET transferred_quantity = transferred_quantity - available_quantity;
            END IF;

        END LOOP product_loop;

        CLOSE cur;

        DROP TEMPORARY TABLE IF EXISTS temp_available_products;

        COMMIT;

    ELSE
        ROLLBACK;
    END IF;
END //

DELIMITER ;