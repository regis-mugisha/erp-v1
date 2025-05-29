package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.erp.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByEmployeeId(Long employeeId);
    Optional<Message> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
    List<Message> findByMonthAndYear(Integer month, Integer year);
    List<Message> findByEmailSentFalse();
}
