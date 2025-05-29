package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.dto.employment.CreateEmploymentRequest;
import rw.gov.erp.dto.employment.EmploymentResponse;
import rw.gov.erp.exception.BadRequestException;
import rw.gov.erp.exception.NotFoundException;
import rw.gov.erp.model.Employee;
import rw.gov.erp.model.Employment;
import rw.gov.erp.repository.EmployeeRepository;
import rw.gov.erp.repository.EmploymentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmploymentResponse createEmployment(CreateEmploymentRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        // Check if employee already has an active employment
        if (employmentRepository.findByEmployeeIdAndStatus(employee.getId(), Employment.EmploymentStatus.ACTIVE).isPresent()) {
            log.warn("Employee {} already has an active employment", employee.getId());
            throw new BadRequestException("Employee already has an active employment");
        }

        Employment employment = new Employment();
        employment.setEmployee(employee);
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        employment.setJoiningDate(request.getJoiningDate());
        employment.setStatus(Employment.EmploymentStatus.ACTIVE);

        employment = employmentRepository.save(employment);
        log.info("Created employment for employee {}", employee.getId());

        return EmploymentResponse.fromEntity(employment);
    }

    public List<EmploymentResponse> getEmploymentsByEmployeeId(Long employeeId) {
        return employmentRepository.findByEmployeeId(employeeId).stream()
                .map(EmploymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EmploymentResponse> getActiveEmployments() {
        return employmentRepository.findByStatus(Employment.EmploymentStatus.ACTIVE).stream()
                .map(EmploymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deactivateEmployment(Long employmentId) {
        Employment employment = employmentRepository.findById(employmentId)
                .orElseThrow(() -> new NotFoundException("Employment not found"));

        employment.setStatus(Employment.EmploymentStatus.INACTIVE);
        employmentRepository.save(employment);
        log.info("Deactivated employment {}", employmentId);
    }
}
