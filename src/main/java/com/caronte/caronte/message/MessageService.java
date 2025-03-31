package com.caronte.caronte.message;

import java.util.ArrayList;
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

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final ReceiverRepository receiverRepository;
    private final ReceiverService receiverService;
    private final ImageRepository imageRepository;

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
                processedImageUrl = MediaHandler.uploadImageToCloudinary(MediaHandler.base64ToImage(customImage),"messages/");
                
                Image image = new Image();
                image.setImageUrl(processedImageUrl);
                image.setMessage(message);
                imageRepository.save(image);
            }
        }
       

        if (request.getRecipients() != null) {
            for (MessageRequestDto.RecipientDto r : request.getRecipients()) {
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

    public List<MessageRequestDto> getMessagesRequestDtoByCustomerId(Long customerId) {
        List<Message> messages = messageRepository.findAllByCustomerId(customerId);
        List<MessageRequestDto> messageDtos = new ArrayList<>();
        for (Message message : messages) {
            MessageRequestDto messageDto = new MessageRequestDto();
            List<Image> images = imageRepository.findAllByMessageId(message.getId());
            List<String> imageUrls = new ArrayList<>();
            List<Receiver> receivers = receiverRepository.findByMessageId(message.getId());
            messageDto.setTitle(message.getTitle());
            messageDto.setBody(message.getBody());
            messageDto.setCustomImages(new ArrayList<>());
            messageDto.setIsLastWill(message.getIsLastWill());
            messageDto.setCustomImages(imageUrls);
            messageDto.setRecipients(new ArrayList<>());
            for (Image image : images) {
                imageUrls.add(image.getImageUrl());
            }
            for(Receiver receiver : receivers) {
            MessageRequestDto.RecipientDto recipientDto = new MessageRequestDto.RecipientDto();
            recipientDto.setName(receiver.getName());
            recipientDto.setTelephone(receiver.getTelephone());
            recipientDto.setEmail(receiver.getEmail());
            messageDto.getRecipients().add(recipientDto);
            }
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
    }

    public MessageRequestDto getMessageRequestDtoByMessageId(Long message_id, Long customerId) {
        Message message = messageRepository.findById(message_id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        MessageRequestDto messageRequestDto = new MessageRequestDto();
        messageRequestDto.setTitle(message.getTitle());
        messageRequestDto.setBody(message.getBody());
        messageRequestDto.setCustomImages(this.imageRepository.findAllByMessageId(message_id).stream()
            .map(Image::getImageUrl)
            .toList());
        messageRequestDto.setIsLastWill(message.getIsLastWill());
        List<Receiver> receivers = receiverRepository.findByMessageId(message_id);
        List<MessageRequestDto.RecipientDto> recipientDtos = new ArrayList<>();
        for (Receiver receiver : receivers) {
            MessageRequestDto.RecipientDto recipientDto = new MessageRequestDto.RecipientDto();
            recipientDto.setName(receiver.getName());
            recipientDto.setTelephone(receiver.getTelephone());
            recipientDto.setEmail(receiver.getEmail());
            recipientDtos.add(recipientDto);
        }
        messageRequestDto.setRecipients(recipientDtos);

        return messageRequestDto;
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