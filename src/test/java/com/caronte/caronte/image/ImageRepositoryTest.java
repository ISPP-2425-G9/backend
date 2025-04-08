package com.caronte.caronte.image;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caronte.caronte.message.Message;

@ExtendWith(MockitoExtension.class)
public class ImageRepositoryTest {

    @Mock
    private ImageRepository imageRepository;

    @Test
    public void findAllByMessageId_returnsCorrectImages() {
        Message message = new Message();
        message.setId(1L);

        Image img1 = new Image("https://img1.com", message);
        Image img2 = new Image("https://img2.com", message);

        when(imageRepository.findAllByMessageId(1L)).thenReturn(List.of(img1, img2));

        List<Image> results = imageRepository.findAllByMessageId(1L);

        assertEquals(2, results.size());
        assertEquals("https://img1.com", results.get(0).getImageUrl());
        assertEquals("https://img2.com", results.get(1).getImageUrl());
    }
}
