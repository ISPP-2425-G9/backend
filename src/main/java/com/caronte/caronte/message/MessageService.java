package com.caronte.caronte.message;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.image.Image;
import com.caronte.caronte.image.ImageRepository;
import com.caronte.caronte.receiver.Receiver;
import com.caronte.caronte.receiver.ReceiverRepository;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final ReceiverRepository receiverRepository;
    private final ReceiverService receiverService;
    private final ImageRepository imageRepository;
    private final MediaHandler mediaHandler;

    public MessageService(MessageRepository messageRepository,
                          CustomerRepository customerRepository,
                          ReceiverService receiverService,
                          ImageRepository imageRepository,
                          ReceiverRepository receiverRepository) {
        this.imageRepository = imageRepository;
        this.messageRepository = messageRepository;
        this.customerRepository = customerRepository;
        this.receiverService = receiverService;
        this.receiverRepository = receiverRepository;
    }

    @Transactional
    public Message createMessage(MessageRequestDto request, Long customerId) {
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


    public List<Message> getMessagesByCustomerId(Long customerId) {
        return messageRepository.findAllByCustomerId(customerId);
    }

    public Message getMessageById(Long message_id, Long customerId) {
        return messageRepository.findById(message_id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
    }

    public Message updateMessage(Long message_id, MessageRequestDto request, Long customerId) {
        Message message = messageRepository.findById(message_id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

        if (!message.getCustomer().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to access this resource");
        }

        message.setTitle(request.getTitle());
        message.setBody(request.getBody());

        List<Image> images = this.imageRepository.findAllByMessageId(message_id);
        for (Image image : images) {
            MediaHandler.deleteImageFromCloudinary(image.getImageUrl()); //TODO borrar imagenen en Cloudinary
            this.imageRepository.delete(image);
        }

        List<String> customImages = request.getCustomImages();
        for (String customImage : customImages) {
            String processedImageUrl;
            if (customImage != null && customImage.startsWith("data:image/")) {
                processedImageUrl = MediaHandler.uploadImageToCloudinary(MediaHandler.base64ToImage(customImage),"messages/");
                
                Image image = new Image();
                image.setImageUrl(processedImageUrl);
                image.setMessage(message);
                imageRepository.save(image);
            }
        }

        if (request.getRecipients() != null) {
            for (MessageRequestDto.RecipientDto r : request.getRecipients()) {
            boolean recipientExists = receiverRepository.findByMessageId(message_id).stream()
                .anyMatch(receiver -> receiver.getTelephone().equals(r.getTelephone()) || receiver.getEmail().equals(r.getEmail()));
            if (!recipientExists) {
                receiverService.saveMessageReceiver(
                    r.getName(),
                    r.getTelephone(),
                    r.getEmail(),
                    message
                );
            } else {
                receiverService.updateMessageReceiver(
                    receiverRepository.findByMessageId(message_id).stream()
                        .filter(receiver -> receiver.getTelephone().equals(r.getTelephone()) || receiver.getEmail().equals(r.getEmail()))
                        .findFirst()
                        .get()
                        .getId(),
                    r.getName(),
                    r.getTelephone(),
                    r.getEmail()
                );
            }
            }
        }

        return messageRepository.save(message);
    }

    public void deleteMessage(Long message_id, Long customerId) {
        Message message = messageRepository.findById(message_id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

        if (!message.getCustomer().getId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to access this resource");
        }

        List<Image> images = this.imageRepository.findAllByMessageId(message_id);
        for (Image image : images) {
            MediaHandler.deleteImageFromCloudinary(image.getImageUrl()); //TODO borrar imagenen en Cloudinary
            this.imageRepository.delete(image);
        }

        receiverRepository.findByMessageId(message_id).forEach(receiver -> receiverRepository.delete(receiver));
        messageRepository.delete(message);
    }
}