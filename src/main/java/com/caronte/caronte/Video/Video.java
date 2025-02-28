package com.caronte.caronte.Video;

import com.caronte.caronte.Message.Message;

import jakarta.persistence.*;
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
