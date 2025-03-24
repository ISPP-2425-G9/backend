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
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import com.caronte.caronte.video.Video;
import com.caronte.caronte.video.VideoRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final ReceiverRepository receiverRepository;
    private final VideoRepository videoRepository;
    private final ImageRepository imageRepository;

    public MessageService(MessageRepository messageRepository, CustomerRepository customerRepository,
            ReceiverRepository receiverRepository, VideoRepository videoRepository, ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
        this.messageRepository = messageRepository;
        this.customerRepository = customerRepository;
        this.receiverRepository = receiverRepository;
        this.videoRepository = videoRepository;
    }

    @Transactional
    public Message createMessage(CreateMessageRequestDto request, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ResourceNotFound.of("Customer"));

        Message message = new Message(request, customer);
        Message savedMessage = messageRepository.save(message);
        
        Video video = new Video(request.getVideoUrl(), savedMessage);
        Image image = new Image(request.getImageUrl(), savedMessage);
        videoRepository.save(video);
        imageRepository.save(image);

        if (request.getRecipients() != null) {
            List<Receiver> receivers = request.getRecipients().stream().map(r -> Receiver.parse(r, message)).toList();
            receiverRepository.saveAll(receivers);
        }

        return savedMessage;
    }


}
