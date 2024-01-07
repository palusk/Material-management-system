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
    SELECT po.order_id, po.order_date, os.status_name, e.first_name, e.last_name
    FROM pending_orders po
             JOIN order_status os ON po.status_id = os.status_id
             JOIN employees e ON po.warehouse_id = e.warehouse_id
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

DELIMITER ;