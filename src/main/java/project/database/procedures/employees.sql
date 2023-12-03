DELIMITER //


CREATE OR REPLACE PROCEDURE DeleteEmployee(
    IN p_user_id INT
)
BEGIN
    DELETE FROM employees
    WHERE user_id = p_user_id;
END //

CREATE OR REPLACE PROCEDURE GetEmployeesInWarehouse(
    IN p_warehouse_id INT
)
BEGIN
    SELECT *
    FROM employees
    WHERE warehouse_id = p_warehouse_id;
END //

CREATE OR REPLACE PROCEDURE UpdateEmployeePosition(
    IN p_user_id INT,
    IN p_new_position VARCHAR(50)
)
BEGIN
    UPDATE employees
    SET position = p_new_position
    WHERE user_id = p_user_id;
END //

DELIMITER ;