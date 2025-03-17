package com.caronte.caronte.message;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.receiver.ReceiverService;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final ReceiverService receiverService;

    public MessageService(MessageRepository messageRepository, CustomerRepository customerRepository,ReceiverService receiverService) {
        this.messageRepository = messageRepository;
        this.customerRepository = customerRepository;
        this.receiverService = receiverService;
    }

    @Transactional
    public Message createMessage(CreateMessageRequestDto request, Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Message message = new Message();
        message.setTitle(request.getTitle());
        message.setBody(request.getBody());
        message.setCode(request.getCode());
        message.setIsLastWill(request.getIsLastWill());
        message.setVideoUrl(request.getVideoUrl());
        message.setImage(request.getImage());
        message.setCustomer(customer);

        Message savedMessage = messageRepository.save(message);

        if (request.getRecipients() != null) {
            for (CreateMessageRequestDto.RecipientDto r : request.getRecipients()) {
                receiverService.saveMessageReceiver(
                        r.getName(),
                        r.getTelephone(),
                        r.getEmail(),
                        savedMessage
                );
            }
        }

        return savedMessage;
    }
}
