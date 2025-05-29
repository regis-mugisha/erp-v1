package rw.gov.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.gov.erp.model.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByEmployeeCode(String employeeCode);
    List<Message> findByMonthAndYear(Integer month, Integer year);
    List<Message> findByEmailSentFalse();
}
