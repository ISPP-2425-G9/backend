package com.caronte.caronte.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.image.Image;
import com.caronte.caronte.image.ImageRepository;
import com.caronte.caronte.message.DTOs.MessageRequestDto;
import com.caronte.caronte.receiver.Receiver;
import com.caronte.caronte.receiver.ReceiverRepository;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.util.AESCipher;
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
    MediaHandler mediaHandler;
    private final AESCipher aesCipher;

    public MessageService(MessageRepository messageRepository,
                          CustomerRepository customerRepository,
                          ReceiverService receiverService,
                          ImageRepository imageRepository,
                          ReceiverRepository receiverRepository,
                          MediaHandler mediaHandler,
                          AESCipher aesCipher) {
        this.imageRepository = imageRepository;
        this.messageRepository = messageRepository;
        this.customerRepository = customerRepository;
        this.receiverService = receiverService;
        this.receiverRepository = receiverRepository;
        this.mediaHandler = mediaHandler;
        this.aesCipher = aesCipher;
    }

    public Message getMessageById(Long messageId) {
            return messageRepository.findById(messageId)
                .orElseThrow(() -> ResourceNotFound.of("Message", "id", messageId));
    }

    public MessageRequestDto getMessageRequestDtoByMessageId(Long customerId, Long messageId) {
            Message message = getMessageById(messageId);
            ResponseThrow.checkOrForbidden(message.hasCustomerWithId(customerId));
            return convertToDto(message);
    }

    public MessageRequestDto getMessageRequestDtoByMessageId(Long messageId) {
        Message message = getMessageById(messageId);
        return convertToDto(message);
    }
    
    public List<MessageRequestDto> getMessagesRequestDtoByCustomerId(Long customerId) {
        List<Message> messages = messageRepository.findAllByCustomerId(customerId);
        return messages.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public Message createMessage(MessageRequestDto request, Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ResourceNotFound.of("Customer", "id", customerId));

        Message message = new Message();
        message.setTitle(request.getTitle());
        message.setBody(request.getBody());

        String uniqueCode = generateUniqueRandomCode(); // AQUIIII
        message.setCode(uniqueCode);

        message.setIsLastWill(false);
        message.setCustomer(customer);

        Message savedMessage = messageRepository.save(message);

        uploadNewImages(request.getCustomImages(), new ArrayList<>(), savedMessage);

        //Esto es necesario crearlo desde 0 por la relación entre receiver y message NO SE PUEDE ACTUALIZAR
        //Con la funcion anterior si creas dos mensajes y se lo quieres enviar a la misma persona, uno de los dos no le llega 
        if (request.getRecipients() != null) {
            for (MessageRequestDto.RecipientDto r : request.getRecipients()){
                receiverService.saveReceiverByRecipientDto(r, savedMessage);
            }
        }

        return savedMessage;
    }

    @Transactional
    public Message updateMessage(Long message_id, MessageRequestDto request, Long customerId) {
        Message message = this.getMessageById(message_id);

        ResponseThrow.checkOrForbidden(message.hasCustomerWithId(customerId), "User not authorized to access this resource");
    
        message.setTitle(request.getTitle());
        message.setBody(request.getBody());
    
        List<Image> existingImages = this.imageRepository.findAllByMessageId(message_id);
        List<String> requestImageUrls = request.getCustomImages();
    
        existingImages = removeObsoleteImages(existingImages, requestImageUrls);
    
        uploadNewImages(requestImageUrls, existingImages, message);

        if (request.getRecipients() != null) {
            updateRecipients(request, message);
        }
    
        return messageRepository.save(message);
    }

    public void deleteMessage(Long message_id, Long customerId) {
        Message message = this.getMessageById(message_id);

        ResponseThrow.checkOrForbidden(message.hasCustomerWithId(customerId), "User not authorized to access this resource");

        List<Image> images = this.imageRepository.findAllByMessageId(message_id);
        for (Image image : images) {
            mediaHandler.deleteImageFromCloudinary(image.getImageUrl());
            this.imageRepository.delete(image);
        }
        receiverRepository.findByMessageId(message_id).forEach(receiver -> receiverRepository.delete(receiver));
        messageRepository.delete(message);
    }
    
    private MessageRequestDto convertToDto(Message message) {
        MessageRequestDto dto = new MessageRequestDto();
        dto.setMessageId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setBody(message.getBody());
        dto.setIsLastWill(message.getIsLastWill());
    
        List<String> imageUrls = imageRepository.findAllByMessageId(message.getId()).stream()
            .map(Image::getImageUrl)
            .toList();
        dto.setCustomImages(imageUrls);
    
        List<MessageRequestDto.RecipientDto> recipients = receiverRepository.findByMessageId(message.getId()).stream()
            .map(this::convertToRecipientDto)
            .toList();
        dto.setRecipients(recipients);
    
        return dto;
    }
    
    private MessageRequestDto.RecipientDto convertToRecipientDto(Receiver receiver) {
        MessageRequestDto.RecipientDto recipientDto = new MessageRequestDto.RecipientDto();
        recipientDto.setName(receiver.getName());
        recipientDto.setTelephone(receiver.getTelephone());
        recipientDto.setEmail(receiver.getEmail());
        return recipientDto;
    }

    private void uploadNewImages(List<String> requestImageUrls, List<Image> existingImages, Message message) {
        for (String customImage : requestImageUrls) {
            if (existingImages.stream().anyMatch(i -> i.getImageUrl().equals(customImage))) {
                continue;
            }
    
            if (customImage != null && customImage.startsWith("data:image/")) {
                String processedImageUrl = mediaHandler.uploadImageToCloudinary(
                    customImage,
                    message.getCustomer().getDni() + "/messages/" + message.getId() + "/"
                );
    
                Image image = new Image();
                image.setImageUrl(processedImageUrl);
                image.setMessage(message);
                imageRepository.save(image);
            }
        }
    }

    private void updateRecipients(MessageRequestDto request, Message message) {

        for (Receiver receiver : receiverRepository.findByMessageId(message.getId())) {
            //Si no existe en la request pero si en la base de datos se elimina
            if (request.getRecipients().stream().noneMatch(r -> r.getTelephone().equals(receiver.getTelephone()) && r.getEmail().equals(receiver.getEmail()))) {
                receiverRepository.delete(receiver);
            }
        }
        for (MessageRequestDto.RecipientDto r : request.getRecipients()) { 
            //Un receptor existe si tiene el mismo telefono y email
            //Si existe se actualiza 
            //Si no existe se crea uno nuevo
            Receiver receiverExistent = receiverRepository.findByMessageIdAndTelephoneOrEmail(message.getId(), r.getTelephone(), r.getEmail()).orElse(null);
            if (receiverExistent == null) receiverService.saveReceiverByRecipientDto(r, message);
            else receiverService.updateMessageReceiver(receiverExistent.getId(),r);
        }
    }

    private List<Image> removeObsoleteImages(List<Image> existingImages, List<String> requestImageUrls) {
        Iterator<Image> iterator = existingImages.iterator();
        while (iterator.hasNext()) {
            Image image = iterator.next();
            if (!requestImageUrls.contains(image.getImageUrl())) {
                mediaHandler.deleteImageFromCloudinary(image.getImageUrl());
                imageRepository.delete(image);
                iterator.remove();
            }
        }
        return existingImages;
    }

    private String generateUniqueRandomCode() {
        String code;
        int randomNumber = (int)(Math.random() * 100_000); // 00000 - 99999
        code = String.format("%05d", randomNumber);
        code = aesCipher.encrypt(code);
        return code;
    }

    public boolean validateMessageCode(Long messageId, String inputCode) {
    Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> ResourceNotFound.of("Message not found"));
    String decryptedCode = aesCipher.decrypt(message.getCode());
    return inputCode.equals(decryptedCode);
}


    public boolean isOwner(Long messageId, Long customerId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> ResourceNotFound.of("Message not found"));
        return message.getCustomer().getId().equals(customerId);
    }
}