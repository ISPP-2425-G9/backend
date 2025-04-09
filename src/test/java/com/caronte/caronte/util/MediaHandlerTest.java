package com.caronte.caronte.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MediaHandlerTest {

    private MediaHandler mediaHandler;
    private Cloudinary mockCloudinaryImage;
    private Cloudinary mockCloudinaryVideo;
    private Uploader mockUploaderImage;
    private Uploader mockUploaderVideo;

    @BeforeEach
    void setUp() throws Exception {
        mediaHandler = new MediaHandler("cloudinary://dummy:dummy@dummy", "cloudinary://dummy:dummy@dummy");
        mockCloudinaryImage = mock(Cloudinary.class);
        mockCloudinaryVideo = mock(Cloudinary.class);
        mockUploaderImage = mock(Uploader.class);
        mockUploaderVideo = mock(Uploader.class);
        when(mockCloudinaryImage.uploader()).thenReturn(mockUploaderImage);
        when(mockCloudinaryVideo.uploader()).thenReturn(mockUploaderVideo);
        java.lang.reflect.Field imageField = MediaHandler.class.getDeclaredField("cloudinaryImage");
        imageField.setAccessible(true);
        imageField.set(mediaHandler, mockCloudinaryImage);

        java.lang.reflect.Field videoField = MediaHandler.class.getDeclaredField("cloudinaryVideo");
        videoField.setAccessible(true);
        videoField.set(mediaHandler, mockCloudinaryVideo);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testUploadImageToCloudinary() throws IOException {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        String base64String = "data:image/png;base64," + base64;
        String folder = "testFolder";
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://dummy.secure.url/image.png");
        when(mockUploaderImage.upload(any(File.class), any(Map.class))).thenReturn(uploadResult);
        String resultUrl = mediaHandler.uploadImageToCloudinary(base64String, folder);
        assertNotNull(resultUrl);
        assertEquals("http://dummy.secure.url/image.png", resultUrl);
    }

    @Test
    void testUploadVideoToCloudinary() throws IOException {
        File tempVideo = File.createTempFile("test_video", ".mp4");
        try (FileOutputStream fos = new FileOutputStream(tempVideo)) {
            fos.write("dummy video content".getBytes());
        }
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://dummy.secure.url/video.mp4");
        when(mockUploaderVideo.upload(eq(tempVideo), any(Map.class))).thenReturn(uploadResult);

        String resultUrl = mediaHandler.uploadVideoToCloudinary(tempVideo);
        assertNotNull(resultUrl);
        assertEquals("http://dummy.secure.url/video.mp4", resultUrl);
        assertFalse(tempVideo.exists());
    }

    @Test
    void testDeleteImageFromCloudinary() throws Exception {
        String imageUrl = "http://res.cloudinary.com/demo/upload/images/testFolder/somePublicId.png";
        Map<String, Object> destroyResult = new HashMap<>();
        destroyResult.put("result", "ok");
        when(mockUploaderImage.destroy(any(String.class), any(Map.class))).thenReturn(destroyResult);

        String result = mediaHandler.deleteImageFromCloudinary(imageUrl);
        assertNotNull(result);
        assertEquals("ok", result);
    }

    @Test
    void testBase64ToImage_valid() {
        String base64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAObU8NEAAAAASUVORK5CYII=";
        BufferedImage image = MediaHandler.base64ToImage(base64);
        assertNotNull(image);
        assertEquals(1, image.getWidth());
        assertEquals(1, image.getHeight());
    }

    @Test
    void testBase64ToImage_oversize() {
        byte[] largeData = new byte[5 * 1024 * 1024 + 1];
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = 1;
        }
        String base64 = Base64.getEncoder().encodeToString(largeData);
        BufferedImage image = MediaHandler.base64ToImage(base64);
        assertNull(image, "Para datos oversize se espera que base64ToImage retorne null");
    }

    @Test
    void testBase64ToVideo() throws IOException {
        byte[] videoData = "dummy video bytes".getBytes();
        String base64 = Base64.getEncoder().encodeToString(videoData);
        File videoFile = mediaHandler.base64ToVideo(base64);
        assertNotNull(videoFile);
        assertTrue(videoFile.exists());
        assertEquals(videoData.length, videoFile.length());
        videoFile.delete();
    }
}
