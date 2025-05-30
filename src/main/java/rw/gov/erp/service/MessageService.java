package rw.gov.erp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.dto.message.CreateMessageRequest;
import rw.gov.erp.dto.message.MessageResponse;
import rw.gov.erp.exception.BadRequestException;
import rw.gov.erp.exception.NotFoundException;
import rw.gov.erp.model.Message;
import rw.gov.erp.model.Payslip;
import rw.gov.erp.repository.MessageRepository;
import rw.gov.erp.repository.PayslipRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing message-related operations.
 * Handles the generation and management of system messages, particularly salary payment notifications.
 *
 * Key Responsibilities:
 * - Generate messages for approved payslips
 * - Track message delivery status
 * - Retrieve messages by various criteria
 * - Mark messages as sent
 *
 * Message Types:
 * - Salary Payment Notifications
 * - System Notifications
 * - Status Updates
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final PayslipRepository payslipRepository;
    private final EmailService emailService;

    /**
     * Creates messages for all approved payslips in a given month and year.
     * Generates personalized salary payment notifications for each employee.
     *
     * @param month Month for message generation
     * @param year Year for message generation
     */
    @Transactional
    public void createMessagesForApprovedPayslips(Integer month, Integer year) {
        List<Payslip> paidPayslips = payslipRepository.findByMonthAndYear(month, year).stream()
                .filter(payslip -> payslip.getStatus() == Payslip.PayslipStatus.PAID)
                .toList();

        paidPayslips.forEach(payslip -> {
            String message = String.format(
                    "Dear %s, your salary for %d/%d from Rwanda Government amounting to %s has been credited to your account %s successfully.",
                    payslip.getEmployee().getFirstName(),
                    payslip.getMonth(),
                    payslip.getYear(),
                    payslip.getNetSalary(),
                    payslip.getEmployee().getId()
            );

            Message messageEntity = new Message();
            messageEntity.setEmployee(payslip.getEmployee());
            messageEntity.setMessage(message);
            messageEntity.setMonth(month);
            messageEntity.setYear(year);
            messageEntity.setEmailSent(false);

            messageRepository.save(messageEntity);
            log.info("Created message for employee {} for {}/{}", payslip.getEmployee().getId(), month, year);
        });
    }

    /**
     * Retrieves all messages for a specific employee.
     *
     * @param employeeId Employee's unique identifier
     * @return List of MessageResponse objects
     */
    public List<MessageResponse> getMessagesByEmployeeId(Long employeeId) {
        return messageRepository.findByEmployeeId(employeeId).stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all messages for a specific month and year.
     *
     * @param month Month to filter by
     * @param year Year to filter by
     * @return List of MessageResponse objects
     */
    public List<MessageResponse> getMessagesByMonthAndYear(Integer month, Integer year) {
        return messageRepository.findByMonthAndYear(month, year).stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all messages that haven't been sent yet.
     * Used for batch processing of pending notifications.
     *
     * @return List of unsent MessageResponse objects
     */
    public List<MessageResponse> getUnsentMessages() {
        return messageRepository.findByEmailSentFalse().stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Marks a specific message as sent.
     * Used after successful delivery of the message.
     *
     * @param messageId Message's unique identifier
     * @throws NotFoundException if message is not found
     */
    @Transactional
    public void markMessageAsSent(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.warn("Message not found for marking as sent: {}", messageId);
                    return new NotFoundException("Message not found");
                });

        message.setEmailSent(true);
        messageRepository.save(message);
        log.info("Marked message as sent: {}", messageId);
    }

    @Transactional
    public void sendPendingEmails() {
        List<Message> unsentMessages = messageRepository.findByEmailSentFalse();
        
        for (Message message : unsentMessages) {
            try {
                emailService.sendEmail(
                    "mugisharegis72@gmail.com", // Admin's Mailtrap account
                    "Salary Payment Notification",
                    message.getMessage()
                );
                markMessageAsSent(message.getId());
                log.info("Sent email for message: {}", message.getId());
            } catch (Exception e) {
                log.error("Failed to send email for message: {}", message.getId(), e);
            }
        }
    }

    public MessageResponse createMessage(CreateMessageRequest request) {
        Payslip payslip = payslipRepository.findById(request.getPayslipId())
                .orElseThrow(() -> new NotFoundException("Payslip not found"));

        int month = payslip.getMonth();
        int year = payslip.getYear();

        // Check if message already exists
        if (messageRepository.findByEmployeeIdAndMonthAndYear(payslip.getEmployee().getId(), month, year).isPresent()) {
            log.warn("Message already exists for employee {} for {}/{}", payslip.getEmployee().getId(), month, year);
            throw new BadRequestException("Message already exists for employee " + payslip.getEmployee().getId() + " for " + month + "/" + year);
        }

        // Create message
        Message message = new Message();
        message.setEmployee(payslip.getEmployee());
        message.setMessage(request.getMessage());
        message.setMonth(month);
        message.setYear(year);

        message = messageRepository.save(message);
        log.info("Created message for employee {} for {}/{}", payslip.getEmployee().getId(), month, year);

        return MessageResponse.fromEntity(message);
    }
}
