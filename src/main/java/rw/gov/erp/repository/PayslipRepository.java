package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.erp.model.Payslip;

import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends JpaRepository<Payslip, String> {
    List<Payslip> findByEmployeeCode(String employeeCode);
    List<Payslip> findByMonthAndYear(Integer month, Integer year);
    Optional<Payslip> findByEmployeeCodeAndMonthAndYear(String employeeCode, Integer month, Integer year);
    List<Payslip> findByStatus(Payslip.PayslipStatus status);
}
