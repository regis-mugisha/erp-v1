-- Create the function that will be triggered
CREATE OR REPLACE FUNCTION create_salary_message()
RETURNS TRIGGER AS $$
DECLARE
    employee_name VARCHAR;
BEGIN
    -- Only trigger when status changes to PAID
    IF NEW.status = 'PAID' AND (OLD.status IS NULL OR OLD.status != 'PAID') THEN
        -- Get employee's full name
        SELECT CONCAT(first_name, ' ', last_name) INTO employee_name
        FROM employees
        WHERE id = NEW.employee_id;

        -- Insert the message
        INSERT INTO messages (
            employee_id,
            message,
            month,
            year,
            created_at,
            email_sent
        ) VALUES (
            NEW.employee_id,
            format(
                'Dear %s, your salary for %s/%s has been processed successfully. Base Salary: %s, Gross Salary: %s, Net Salary: %s. Deductions: Tax (%s), Pension (%s), Medical Insurance (%s), Others (%s). Allowances: Housing (%s), Transport (%s).',
                employee_name,
                NEW.month,
                NEW.year,
                NEW.base_salary,
                NEW.gross_salary,
                NEW.net_salary,
                NEW.employee_taxed_amount,
                NEW.pension_amount,
                NEW.medical_insurance_amount,
                NEW.other_taxed_amount,
                NEW.house_amount,
                NEW.transport_amount
            ),
            NEW.month,
            NEW.year,
            CURRENT_TIMESTAMP,
            false
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger
DROP TRIGGER IF EXISTS payslip_status_change_trigger ON payslips;
CREATE TRIGGER payslip_status_change_trigger
    AFTER UPDATE ON payslips
    FOR EACH ROW
    EXECUTE FUNCTION create_salary_message(); 