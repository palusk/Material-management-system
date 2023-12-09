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

DELIMITER ;