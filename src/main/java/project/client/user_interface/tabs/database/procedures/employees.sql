DELIMITER //

CREATE OR REPLACE PROCEDURE setReportsTo(
    IN p_employee_id INT,
    IN p_reports_to_id INT
)
BEGIN
    UPDATE employees
    SET reports_to = p_reports_to_id
    WHERE employee_id = p_employee_id;
END //

CREATE OR REPLACE PROCEDURE CalculateEmployeeHierarchy()
BEGIN
    DECLARE maxLevel INT;
    DECLARE done INT DEFAULT 0;
    DECLARE warehouseId INT;

    -- Pobierz unikalne warehouse_id z tabeli employees
    DECLARE warehouseCursor CURSOR FOR SELECT DISTINCT warehouse_id FROM employees;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    -- Usuń wszystkie rekordy z tabeli hierarchii przed ponownym jej utworzeniem
    TRUNCATE TABLE employee_hierarchy;

    OPEN warehouseCursor;

    -- Iteruj przez każde warehouse_id
    warehouseLoop: LOOP
        FETCH warehouseCursor INTO warehouseId;
        IF done THEN
            LEAVE warehouseLoop;
        END IF;

        -- Dodaj root node do tabeli hierarchii dla danego warehouse_id
        INSERT INTO employee_hierarchy (warehouse_id, employee_id, descendant_id, level)
        SELECT warehouseId, employee_id, employee_id, 0
        FROM employees
        WHERE reports_to = -1 AND warehouse_id = warehouseId;

        SET maxLevel = 0;

        -- Dodaj rekordy do tabeli hierarchii na kolejnych poziomach dla danego warehouse_id
        REPEAT
            INSERT IGNORE INTO employee_hierarchy (warehouse_id, employee_id, descendant_id, level)
            SELECT warehouseId,e.employee_id,
                   IF(eh.descendant_id IS NULL, -2, eh.descendant_id),(maxLevel + 1)
            FROM employees e
                     LEFT JOIN employee_hierarchy eh ON e.reports_to = eh.employee_id AND eh.level = maxLevel
            WHERE e.warehouse_id = warehouseId
                AND e.reports_to != -1;

            SET maxLevel = maxLevel + 1;
        UNTIL ROW_COUNT() = 0 END REPEAT;

        -- Usuń zbędne rekordy dla danego warehouse_id
        DELETE FROM employee_hierarchy
        WHERE employee_id NOT IN (SELECT employee_id FROM employees WHERE reports_to = -1 AND warehouse_id = warehouseId)
          AND employee_id = descendant_id
          AND warehouse_id = warehouseId;
    END LOOP;

    CLOSE warehouseCursor;
END;

CREATE OR REPLACE PROCEDURE getHierarchy()
BEGIN
    SELECT *
    FROM employee_hierarchy;
END //

CREATE OR REPLACE PROCEDURE getAllEmployees()
BEGIN
    SELECT email
    FROM employees;
END //

DELIMITER ;