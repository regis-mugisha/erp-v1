package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.dto.employee.EmployeeRequest;
import rw.gov.erp.dto.employee.EmployeeResponse;
import rw.gov.erp.exception.BadRequestException;
import rw.gov.erp.exception.NotFoundException;
import rw.gov.erp.model.Employee;
import rw.gov.erp.repository.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing employee-related operations.
 * Handles CRUD operations for employees and implements business logic for employee management.
 *
 * Key Responsibilities:
 * - Create new employee records with proper validation
 * - Retrieve employee information
 * - Update employee details
 * - Delete employee records
 * - Handle employee authentication data
 *
 * Security Features:
 * - Password encryption using Spring Security's PasswordEncoder
 * - Role-based access control
 * - Input validation and sanitization
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new employee record.
     * Validates email uniqueness and encrypts password before saving.
     *
     * @param request Employee creation request containing employee details
     * @return EmployeeResponse containing the created employee's information
     * @throws BadRequestException if email already exists
     */
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            log.warn("Attempt to create employee with existing email: {}", request.getEmail());
            throw new BadRequestException("Email already exists");
        }

        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setRoles(request.getRoles());
        employee.setMobile(request.getMobile());
        employee.setDateOfBirth(request.getDateOfBirth());

        Employee saved = employeeRepository.save(employee);
        log.info("Created new employee: {} {} ({})", saved.getFirstName(), saved.getLastName(), saved.getEmail());
        return EmployeeResponse.fromEntity(saved);
    }

    /**
     * Retrieves all employees in the system.
     *
     * @return List of EmployeeResponse containing all employee information
     */
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific employee by their unique code.
     *
     * @param id Employee's unique identifier
     * @return EmployeeResponse containing the employee's information
     * @throws NotFoundException if employee is not found
     */
    public EmployeeResponse getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeResponse::fromEntity)
                .orElseThrow(() -> {
                    log.warn("Employee not found: {}", id);
                    return new NotFoundException("Employee not found");
                });
    }

    /**
     * Updates an existing employee's information.
     * Only updates password if a new one is provided.
     *
     * @param id Employee's unique identifier
     * @param request Updated employee information
     * @return EmployeeResponse containing the updated employee information
     * @throws NotFoundException if employee is not found
     */
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found for update: {}", id);
                    return new NotFoundException("Employee not found");
                });

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setMobile(request.getMobile());
        employee.setDateOfBirth(request.getDateOfBirth());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Employee updated = employeeRepository.save(employee);
        log.info("Updated employee: {}", id);
        return EmployeeResponse.fromEntity(updated);
    }

    /**
     * Deletes an employee record from the system.
     *
     * @param id Employee's unique identifier
     * @throws NotFoundException if employee is not found
     */
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            log.warn("Attempt to delete non-existent employee: {}", id);
            throw new NotFoundException("Employee not found");
        }
        employeeRepository.deleteById(id);
        log.info("Deleted employee: {}", id);
    }
}
