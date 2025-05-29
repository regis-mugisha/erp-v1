package rw.gov.erp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rw.gov.erp.model.Employee;
import rw.gov.erp.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            // Check if admin already exists
            if (employeeRepository.findByEmail("mugisharegis72@gmail.com").isEmpty()) {
                // Create admin user
                Employee admin = new Employee();
                admin.setFirstName("Mugisha");
                admin.setLastName("Regis");
                admin.setEmail("mugisharegis72@gmail.com");
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setMobile("+250788888888");
                admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
                admin.setStatus(Employee.EmployeeStatus.ACTIVE);
                admin.setRoles(List.of("ROLE_ADMIN"));
                employeeRepository.save(admin);
                log.info("Admin user created successfully");
            }

            // Check if manager already exists
            if (employeeRepository.findByEmail("manager@erp.gov.rw").isEmpty()) {
                // Create manager user
                Employee manager = new Employee();
                manager.setFirstName("Manager");
                manager.setLastName("User");
                manager.setEmail("manager@erp.gov.rw");
                manager.setPassword(passwordEncoder.encode("manager123"));
                manager.setMobile("+250788888889");
                manager.setDateOfBirth(LocalDate.of(1990, 1, 1));
                manager.setStatus(Employee.EmployeeStatus.ACTIVE);
                manager.setRoles(List.of("ROLE_MANAGER"));
                employeeRepository.save(manager);
                log.info("Manager user created successfully");
            }
        } catch (Exception e) {
            log.error("Error seeding database: {}", e.getMessage());
        }
    }
} 