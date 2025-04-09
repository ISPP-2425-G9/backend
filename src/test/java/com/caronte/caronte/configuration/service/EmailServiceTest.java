package com.caronte.caronte.configuration.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import com.caronte.caronte.obituary.Obituary;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.caronte.caronte.configuration.services.EmailService;
import com.caronte.caronte.imageTemplate.ImageTemplate;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private EmailService emailService;

    private final String defaultFrom = "test@example.com";

    

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender, defaultFrom);
    }

    @Test
void testGetColorFromString_valid() {
    Color color = emailService.getColorFromString("255, 255, 255");
    DeviceRgb deviceColor = (DeviceRgb) color;
    float[] rgb = deviceColor.getColorValue();
    assertEquals(1.0f, rgb[0], 0.001);
    assertEquals(1.0f, rgb[1], 0.001);
    assertEquals(1.0f, rgb[2], 0.001);
}

@Test
void testGetColorFromString_invalid() {
    Color color = emailService.getColorFromString("invalid");
    DeviceRgb deviceColor = (DeviceRgb) color;
    float[] rgb = deviceColor.getColorValue();
    assertEquals(0.0f, rgb[0], 0.001);
    assertEquals(0.0f, rgb[1], 0.001);
    assertEquals(0.0f, rgb[2], 0.001);
}

    @Test
    void testGenerateObituaryPdf_withInvalidImages() {
        Obituary obituary = new Obituary();
        obituary.setName("John Doe");
        obituary.setBirthDate(LocalDate.of(1950, 1, 1));
        obituary.setDeathDate(LocalDate.of(2020, 12, 31));
        obituary.setWordColor("0,0,0");
        obituary.setFarewellPhrase("Farewell");
        obituary.setFarewellMessage("Goodbye");
        obituary.setCustomImageUrl("invalid-url");

        ImageTemplate imageTemplate = new ImageTemplate();
        imageTemplate.setImageUrl("invalid-url");
        obituary.setImageTemplate(imageTemplate);

        byte[] pdfBytes = emailService.generateObituaryPdf(obituary);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void testSendEmailWithAttachment() throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        byte[] dummyPdf = "dummy pdf content".getBytes();
        String filename = "obituary.pdf";
        
        emailService.sendEmailWithAttachment("recipient@example.com", "Subject", "Email body", dummyPdf, filename);
    
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendEmail() throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        emailService.sendEmail("recipient@example.com", "Subject", "Email body");
        
        verify(mailSender).send(mimeMessage);
    }
}

