package com.caronte.caronte.message;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.message.DTOs.MessageRequestDto;
import com.caronte.caronte.util.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message extends BaseEntity {

    private static final int CODE_LENGTH = 5;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String body;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private Boolean isLastWill;

    public Message(MessageRequestDto requestDto, Customer customer) {
        this.title = requestDto.getTitle();
        this.body = requestDto.getBody();
        this.isLastWill = requestDto.getIsLastWill();
        this.code = generateUniqueRandomCode();
        this.customer = customer;
    }

    private static String generateUniqueRandomCode() {
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, CODE_LENGTH)
                        .mapToObj(_ -> String.valueOf(random.nextInt(10)))
                        .collect(Collectors.joining());
    }

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @JsonIgnore
    public boolean hasCustomerWithId(Long id) {
        return this.customer.getId() == id;
    }

}
