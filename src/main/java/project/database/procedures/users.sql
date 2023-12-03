DELIMITER //

CREATE OR REPLACE PROCEDURE GetAllUsersWithDetails()
BEGIN
    SELECT u.user_id, u.username, e.first_name, e.last_name, e.email, e.position, w.warehouse_name
    FROM users u
             JOIN employees e ON u.employee_id = e.employee_id
             JOIN warehouses w ON e.warehouse_id = w.warehouse_id;
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


CREATE OR REPLACE PROCEDURE DeleteUser(
    IN p_user_id INT
)
BEGIN
    DELETE FROM users
    WHERE user_id = p_user_id;
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

DELIMITER ;