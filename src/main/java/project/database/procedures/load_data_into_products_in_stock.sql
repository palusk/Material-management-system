DELIMITER //

CREATE OR REPLACE PROCEDURE LoadDataIntoProductsInStock(
    IN p_product_id VARCHAR(50),
    IN p_quantity VARCHAR(20),
    IN p_warehouse_id VARCHAR(50),
    IN p_expiration_date VARCHAR(20)
)
BEGIN
    DECLARE v_warehouse_exists INT;
    DECLARE v_product_exists INT;
    DECLARE v_load_status VARCHAR(20);

    -- Rozpocznij transakcję
    START TRANSACTION;

    -- Sprawdź czy magazyn istnieje
    SELECT COUNT(*) INTO v_warehouse_exists
    FROM warehouses
    WHERE warehouse_id = p_warehouse_id;

    -- Sprawdź czy produkt istnieje
    SELECT COUNT(*) INTO v_product_exists
    FROM products
    WHERE product_id = p_product_id;

    -- Jeśli magazyn i produkt istnieją oraz format danych jest poprawny
    IF v_warehouse_exists = 1 AND v_product_exists = 1 AND
       p_quantity REGEXP '^[0-9]+$' AND
       p_expiration_date REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}$' THEN

        -- Wstaw dane do tabeli products_in_stock
        INSERT INTO products_in_stock (product_id, quantity, warehouse_id, expiration_date)
        VALUES (p_product_id, p_quantity, p_warehouse_id, p_expiration_date)
        ON DUPLICATE KEY UPDATE
                             quantity = p_quantity, expiration_date = p_expiration_date;

        -- Aktualizuj tabelę stagingową
        SET v_load_status = 'Processed';

    ELSE
        -- Wstaw dane do tabeli logującej błędy lub obsłuż inaczej
        -- W zależności od potrzeb, możesz zastosować różne strategie obsługi błędów
        -- Możesz na przykład wstawić dane do tabeli logów błędów, zwrócić komunikat, itp.
        -- W poniższym przykładzie używam prostego komunikatu.
        SET v_load_status = 'Error';

    END IF;

    -- Aktualizuj tabelę stagingową
    UPDATE staging_products_in_stock
    SET load_status = v_load_status
    WHERE product_id = p_product_id
      AND warehouse_id = p_warehouse_id
      AND expiration_date = p_expiration_date;

    -- Zakończ transakcję
    COMMIT;
END //

DELIMITER ;