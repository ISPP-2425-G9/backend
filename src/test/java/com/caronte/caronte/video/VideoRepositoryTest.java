package com.caronte.caronte.video;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caronte.caronte.message.Message;

@ExtendWith(MockitoExtension.class)
public class VideoRepositoryTest {

    @Mock
    private VideoRepository videoRepository;

    @Test
    void testAllArgsConstructor_setsFieldsCorrectly() {
        Message message = new Message();
        message.setId(123L);

        Video video = new Video("https://test.com/video.mp4", message);

        assertEquals("https://test.com/video.mp4", video.getVideoUrl());
        assertEquals(123L, video.getMessage().getId());
    }


    @Test
    void testSaveVideo_returnsSavedVideo() {
        Message message = new Message();
        message.setId(1L);

        Video video = new Video();
        video.setVideoUrl("https://video.com/test.mp4");
        video.setMessage(message);

        when(videoRepository.save(video)).thenReturn(video);

        Video saved = videoRepository.save(video);

        assertNotNull(saved);
        assertEquals("https://video.com/test.mp4", saved.getVideoUrl());
        assertEquals(1L, saved.getMessage().getId());
    }

    @Test
    void testFindById_returnsExpectedVideo() {
        Message message = new Message();
        message.setId(2L);

        Video video = new Video();
        video.setVideoUrl("https://video.com/another.mp4");
        video.setMessage(message);
        video.setId(10L);

        when(videoRepository.findById(10L)).thenReturn(Optional.of(video));

        Optional<Video> result = videoRepository.findById(10L);

        assertTrue(result.isPresent());
        assertEquals("https://video.com/another.mp4", result.get().getVideoUrl());
        assertEquals(2L, result.get().getMessage().getId());
    }
}
