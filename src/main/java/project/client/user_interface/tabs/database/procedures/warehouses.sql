DELIMITER //

CREATE OR REPLACE PROCEDURE GetAllWarehouses()
BEGIN
    SELECT CONCAT(warehouse_id,' ',warehouse_name)
    FROM warehouses;
END //

CREATE OR REPLACE PROCEDURE GetAllWarehousesAdmin()
BEGIN
    SELECT *
    FROM warehouses;
END //

CREATE OR REPLACE PROCEDURE GetAllWarehousesWithIgnore(
    IN p_login_user Varchar(50)
)
BEGIN
    SELECT CONCAT(warehouse_id,' ',warehouse_name)
    FROM warehouses
    WHERE warehouse_id NOT IN(
        SELECT warehouse_id FROM employees e WHERE email = p_login_user);
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

DELIMITER ;