package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.erp.model.Employment;

import java.util.List;
import java.util.Optional;

public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    List<Employment> findByEmployeeId(Long employeeId);
    Optional<Employment> findByEmployeeIdAndStatus(Long employeeId, Employment.EmploymentStatus status);
    List<Employment> findByStatus(Employment.EmploymentStatus status);
}