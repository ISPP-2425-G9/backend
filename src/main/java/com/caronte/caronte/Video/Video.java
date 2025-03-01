package com.caronte.caronte.video;

import com.caronte.caronte.message.Message;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Video {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id", nullable = false, updatable = false)
    private Long videoId;

    @Column(nullable = false, length = 512)
    private String videoUrl;
    
    // Relationships
    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
}
