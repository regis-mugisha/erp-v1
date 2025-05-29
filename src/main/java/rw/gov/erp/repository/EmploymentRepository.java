package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.erp.model.Employment;

import java.util.List;
import java.util.Optional;

public interface EmploymentRepository extends JpaRepository<Employment, String> {
    List<Employment> findByEmployeeCode(String employeeCode);
    Optional<Employment> findByEmployeeCodeAndStatus(String employeeCode, Employment.EmploymentStatus status);
}