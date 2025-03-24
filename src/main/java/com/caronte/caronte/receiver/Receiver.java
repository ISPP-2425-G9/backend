package com.caronte.caronte.receiver;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.caronte.caronte.message.CreateMessageRequestDto.RecipientDto;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituraryRequestDto.ContactDto;
import com.caronte.caronte.util.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
     name = "receiver",
     uniqueConstraints = {
         @UniqueConstraint(columnNames = {"obituary_id", "telephone"}),
         @UniqueConstraint(columnNames = {"obituary_id", "email"}),
         @UniqueConstraint(columnNames = {"message_id", "telephone"}),
         @UniqueConstraint(columnNames = {"message_id", "email"})
     }
 )
public class Receiver extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 15)
    private String telephone;

    @Column(nullable = false , length = 255)
    private String email;

    // Relationships
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Obituary obituary;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Message message;

    public static Receiver parse(RecipientDto recipientDto, Message message){
        Receiver receiver = new Receiver();
        receiver.setName(recipientDto.getName());
        receiver.setTelephone(recipientDto.getTelephone());
        receiver.setEmail(recipientDto.getEmail());
        receiver.setMessage(message);
        return receiver;
    }

    public static Receiver parse(ContactDto contactDto, Obituary obituary){
        Receiver receiver = new Receiver();
        receiver.setName(contactDto.getName());
        receiver.setTelephone(contactDto.getPhone());
        receiver.setEmail(contactDto.getEmail());
        receiver.setObituary(obituary);
        return receiver;
    }
}
