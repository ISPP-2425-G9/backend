package com.caronte.caronte.message;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.image.Image;
import com.caronte.caronte.image.ImageRepository;
import com.caronte.caronte.message.DTOs.MessageRequestDto;
import com.caronte.caronte.message.DTOs.MessageRequestDto.RecipientDto;
import com.caronte.caronte.receiver.Receiver;
import com.caronte.caronte.receiver.ReceiverRepository;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import com.caronte.caronte.util.exceptions.ResponseThrow;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final ReceiverRepository receiverRepository;
    private final ReceiverService receiverService;
    private final ImageRepository imageRepository;
    private final MediaHandler mediaHandler;

    public MessageService(MessageRepository messageRepository, CustomerRepository customerRepository,
            ReceiverService receiverService, ImageRepository imageRepository,
            ReceiverRepository receiverRepository, MediaHandler mediaHandler) {
        this.imageRepository = imageRepository;
        this.messageRepository = messageRepository;
        this.customerRepository = customerRepository;
        this.receiverService = receiverService;
        this.receiverRepository = receiverRepository;
        this.mediaHandler = mediaHandler;
    }

    @Transactional
    public Message createMessage(MessageRequestDto request, Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> ResourceNotFound.of("Customer"));

        Message message = new Message(request, customer);
        Message savedMessage = messageRepository.save(message);

        List<Image> images = request.getCustomImages().stream()
                .filter(customImage -> customImage != null && customImage.startsWith("data:image/"))
                .map(customImage -> {
                    String processedImageUrl = mediaHandler.uploadImageToCloudinary(customImage, "message");
                    return new Image(processedImageUrl, message);
                }).toList();

        imageRepository.saveAll(images);

        List<Receiver> receivers = request.getRecipients().stream().map(r -> Receiver.parse(r, message)).toList();
        receiverRepository.saveAll(receivers);

        return savedMessage;
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByCustomerId(Long customerId) {
        return messageRepository.findAllByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public Message getMessageById(Long message_id, Long customerId) {
        return messageRepository.findById(message_id).orElseThrow(() -> ResourceNotFound.of("Message"));
    }

    @Transactional
    public Message updateMessage(Long messageId, MessageRequestDto request, Long customerId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> ResourceNotFound.of("Message"));

        ResponseThrow.checkOrForbidden(message.hasCustomerWithId(customerId));

        message.setTitle(request.getTitle());
        message.setBody(request.getBody());

        List<Image> oldImages = this.imageRepository.findAllByMessageId(messageId);
        oldImages.forEach(oldImage -> mediaHandler.deleteImageFromCloudinary(oldImage.getImageUrl()));
        this.imageRepository.deleteAll(oldImages);

        List<Image> newImages = request.getCustomImages().stream()
            .filter(customImage -> customImage != null && customImage.startsWith("data:image/"))
            .map(customImage -> {
                String imageUrl = mediaHandler.uploadImageToCloudinary(customImage, "messages/");
                return new Image(imageUrl, message);
            }).toList();
        imageRepository.saveAll(newImages);
        
        
        for (RecipientDto recipient : request.getRecipients()) {
            List<Receiver> receivers = receiverRepository.findByMessageId(messageId);
            Optional<Receiver> existingReceiver = receivers.stream()
                    .filter(receiver -> receiver.hasEqualEmailOrTelephone(recipient))
                    .findFirst();
        
            if (existingReceiver.isPresent()) {
                Receiver receiver = existingReceiver.get();
                receiverService.updateMessageReceiver(receiver.getId(), recipient);
            } else {
                receiverService.saveMessageReceiver(recipient, message);
            }
        }
        

        return messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long customerId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> ResourceNotFound.of("Message"));

        ResponseThrow.checkOrForbidden(message.hasCustomerWithId(customerId));

        List<Image> images = this.imageRepository.findAllByMessageId(messageId);
        images.forEach(image -> mediaHandler.deleteImageFromCloudinary(image.getImageUrl()));
        this.imageRepository.deleteAll(images);

        receiverRepository.findByMessageId(messageId).forEach(receiver -> receiverRepository.delete(receiver));
        messageRepository.delete(message);
    }
}