DELIMITER //


CREATE OR REPLACE PROCEDURE InsertOrUpdateEmployee(
    IN p_first_name VARCHAR(50),
    IN p_last_name VARCHAR(50),
    IN p_email VARCHAR(100),
    IN p_position VARCHAR(50),
    IN p_warehouse_id INT,
    IN p_reports_to INT,
    IN p_staging_id INT
)
BEGIN
    DECLARE last_employee_id INT;

    IF NOT EXISTS (SELECT * FROM employees WHERE email = p_email)
    THEN

    INSERT INTO employees (first_name, last_name, email, position, warehouse_id, reports_to)
    VALUES (p_first_name, p_last_name, p_email, p_position, p_warehouse_id, p_reports_to)
    ON DUPLICATE KEY UPDATE
                         first_name = VALUES(first_name),
                         last_name = VALUES(last_name),
                         email = VALUES(email),
                         position = VALUES(position),
                         warehouse_id = VALUES(warehouse_id),
                         reports_to = VALUES(reports_to);

    SET last_employee_id = LAST_INSERT_ID();

    INSERT INTO users (employee_id, email)
    VALUES (LAST_INSERT_ID(), p_email)
    ON DUPLICATE KEY UPDATE
                         email = VALUES(email);

    -- Przypisanie user_id do tabeli employees
    UPDATE employees e
    SET user_id = (SELECT u.user_id FROM users u WHERE u.employee_id = last_employee_id)
    WHERE e.employee_id = last_employee_id;

    UPDATE staging_employees SET load_status = 'Processed' WHERE staging_id = p_staging_id;

    ELSE
        UPDATE staging_employees SET load_status = 'Error' WHERE staging_id = p_staging_id;
    END IF;
END //

CREATE OR REPLACE PROCEDURE InsertStagingEmployees(
    IN p_first_name VARCHAR(255),
    IN p_last_name VARCHAR(255),
    IN p_email VARCHAR(255),
    IN p_position VARCHAR(255),
    IN p_warehouse_id INT,
    IN p_reports_to INT
)
BEGIN
    INSERT INTO staging_employees (
        first_name,
        last_name,
        email,
        position,
        warehouse_id,
        load_status,
        reports_to
    )
    VALUES (
               p_first_name,
               p_last_name,
               p_email,
               p_position,
               p_warehouse_id,
               'Pending',
               p_reports_to
           );
END //

CREATE OR REPLACE PROCEDURE LoadRowIntoEmployees(
    IN p_staging_id INT,
    IN p_first_name VARCHAR(50),
    IN p_last_name VARCHAR(50),
    IN p_email VARCHAR(100),
    IN p_position VARCHAR(50),
    IN p_warehouse_id INT,
    IN p_reports_to INT
)
BEGIN
    DECLARE v_warehouse_exists INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
        END;

    START TRANSACTION;

    SELECT COUNT(*) INTO v_warehouse_exists
    FROM warehouses
    WHERE warehouse_id = p_warehouse_id;

    IF v_warehouse_exists = 1 AND p_email REGEXP '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,4}$' THEN

        CALL InsertOrUpdateEmployee(p_first_name, p_last_name, p_email, p_position, p_warehouse_id, p_reports_to, p_staging_id);
        UPDATE staging_employees
        SET load_status = 'Processed'
        WHERE staging_id = p_staging_id;

    ELSEIF v_warehouse_exists != 1
        THEN
            UPDATE staging_employees
            SET load_status = 'Error',
                error_message = 'Wrong warehouse id'
            WHERE staging_id = p_staging_id;
    ELSE

        UPDATE staging_employees
        SET load_status = 'Error',
            error_message = 'Wrong email'
        WHERE staging_id = p_staging_id;

    END IF;

    COMMIT;
END //

CREATE OR REPLACE PROCEDURE ProcessPendingRowsInEmployees()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE p_employee_id INT;
    DECLARE p_staging_id  INT;
    DECLARE p_first_name VARCHAR(50);
    DECLARE p_last_name VARCHAR(50);
    DECLARE p_email VARCHAR(100);
    DECLARE p_position VARCHAR(50);
    DECLARE p_warehouse_id INT;
    DECLARE p_reports_to INT;
    DECLARE p_load_status ENUM('Pending', 'Processed', 'Error') DEFAULT 'Pending';
    DECLARE p_error_message VARCHAR(255);

    -- Cursor for pending rows
    DECLARE cursor_pending_rows CURSOR FOR
        SELECT staging_id, first_name, last_name, email, position, warehouse_id, reports_to
        FROM staging_employees
        WHERE load_status = 'Pending';

    -- Declare continue handler for the cursor
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
        END;

    -- Start a transaction
    START TRANSACTION;

    -- Open the cursor
    OPEN cursor_pending_rows;

    -- Start processing rows
    processing_rows: LOOP
        FETCH cursor_pending_rows INTO p_staging_id, p_first_name, p_last_name, p_email, p_position, p_warehouse_id, p_reports_to;

        IF done THEN
            LEAVE processing_rows;
        END IF;

        -- Call the procedure for each pending row
        IF NOT EXISTS(SELECT * FROM employees e WHERE e.email = p_email)
            THEN
                CALL LoadRowIntoEmployees(p_staging_id, p_first_name, p_last_name, p_email, p_position, p_warehouse_id,p_reports_to);
            ELSE
                UPDATE staging_employees se SET se.error_message = 'duplicate or empty email' , se.load_status = 'error' WHERE se.staging_id = p_staging_id;
        END IF;
        END LOOP;

    -- Close the cursor
    CLOSE cursor_pending_rows;

    -- Commit the transaction
    COMMIT;
END //

CREATE OR REPLACE PROCEDURE GetEmployeesStagingErrors()
BEGIN
    SELECT *
    FROM staging_employees
    WHERE load_status = 'Error'
    ORDER BY load_timestamp;
END //

CREATE OR REPLACE PROCEDURE DeleteAllEmployeesStagingRows()
BEGIN
    DELETE FROM staging_employees;
END //

CREATE OR REPLACE PROCEDURE GetEmployeesStagingTable()
BEGIN
    SELECT *
    FROM staging_employees
    ORDER BY load_timestamp;
END //

DELIMITER ;