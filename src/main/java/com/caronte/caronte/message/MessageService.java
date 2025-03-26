package com.caronte.caronte.message;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.image.Image;
import com.caronte.caronte.image.ImageRepository;
import com.caronte.caronte.receiver.Receiver;
import com.caronte.caronte.receiver.ReceiverRepository;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final ReceiverRepository receiverRepository;
    private final ImageRepository imageRepository;
    private final MediaHandler mediaHandler;

    public MessageService(MessageRepository messageRepository, CustomerRepository customerRepository,
            ReceiverRepository receiverRepository, ImageRepository imageRepository, MediaHandler mediaHandler) {
        this.imageRepository = imageRepository;
        this.messageRepository = messageRepository;
        this.customerRepository = customerRepository;
        this.receiverRepository = receiverRepository;
        this.mediaHandler = mediaHandler;
    }

    @Transactional
    public Message createMessage(CreateMessageRequestDto request, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ResourceNotFound.of("Customer"));

        Message message = new Message(request, customer);
        Message savedMessage = messageRepository.save(message);

        List<String> customImages = request.getCustomImages();
        List<Image> images = customImages.stream().filter(customImage -> customImage != null && customImage.startsWith("data:image/")).map(customImage -> {
            String processedImageUrl = mediaHandler.uploadImageToCloudinary(customImage, "message");
            return new Image(processedImageUrl, message);
        }).toList();
        imageRepository.saveAll(images);

        if (request.getRecipients() != null) {
            List<Receiver> receivers = request.getRecipients().stream().map(r -> Receiver.parse(r, message)).toList();
            receiverRepository.saveAll(receivers);
        }

        return savedMessage;
    }


}