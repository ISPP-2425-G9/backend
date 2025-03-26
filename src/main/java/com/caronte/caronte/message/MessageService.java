package com.caronte.caronte.message;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.image.Image;
import com.caronte.caronte.image.ImageRepository;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.util.MediaHandler;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final ReceiverService receiverService;
    private final ImageRepository imageRepository;

    public MessageService(MessageRepository messageRepository,
                          CustomerRepository customerRepository,
                          ReceiverService receiverService,
                          ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
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

        String uniqueCode = generateUniqueRandomCode();
        message.setCode(uniqueCode);

        message.setIsLastWill(false);
        message.setCustomer(customer);

        Message savedMessage = messageRepository.save(message);

        List<String> customImages = request.getCustomImages();
        for (String customImage : customImages) {
            String processedImageUrl;
            if (customImage != null && customImage.startsWith("data:image/")) {
                processedImageUrl = MediaHandler.uploadImageToCloudinary(MediaHandler.base64ToImage(customImage),"message");
                
                Image image = new Image();
                image.setImageUrl(processedImageUrl);
                image.setMessage(message);
                imageRepository.save(image);
            }
        }
       

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

    private String generateUniqueRandomCode() {
        String code;
        do {
            int randomNumber = (int)(Math.random() * 100_000); // 00000 - 99999
            code = String.format("%05d", randomNumber);
        } while (messageRepository.existsByCode(code));
        return code;
    }

}