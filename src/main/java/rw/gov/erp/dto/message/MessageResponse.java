package rw.gov.erp.dto.message;

import lombok.Data;
import rw.gov.erp.model.Message;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private String id;
    private String employeeCode;
    private String employeeName;
    private String message;
    private Integer month;
    private Integer year;
    private LocalDateTime sentAt;
    private boolean emailSent;

    public static MessageResponse fromEntity(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setEmployeeCode(message.getEmployee().getCode());
        response.setEmployeeName(message.getEmployee().getFirstName() + " " + message.getEmployee().getLastName());
        response.setMessage(message.getMessage());
        response.setMonth(message.getMonth());
        response.setYear(message.getYear());
        response.setSentAt(message.getSentAt());
        response.setEmailSent(message.isEmailSent());
        return response;
    }
}