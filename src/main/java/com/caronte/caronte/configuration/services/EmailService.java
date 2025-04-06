package com.caronte.caronte.configuration.services;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.caronte.caronte.obituary.Obituary;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.transaction.Transactional;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;    
    private final String defaultFrom;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String defaultFrom) {
        this.mailSender = mailSender;
        this.defaultFrom = defaultFrom;
    }

    @Transactional
    public byte[] generateObituaryPdf(Obituary obituary)  {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        Color textColor = getColorFromString(obituary.getWordColor());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            String bgUrl = obituary.getImageTemplate().getImageUrl();
            ImageData bgImage = ImageDataFactory.create(bgUrl);
            PdfCanvas canvas = new PdfCanvas(pdf.addNewPage());
            Rectangle pageSize = pdf.getDefaultPageSize();
            canvas.addImageFittedIntoRectangle(bgImage, pageSize, false);
        } catch (Exception e) {
            System.out.println("Error cargando fondo: " + e.getMessage());
            pdf.addNewPage();
        }

        String customImageUrl = obituary.getCustomImageUrl();
        if (customImageUrl != null && !customImageUrl.isEmpty()) {
            try {
                ImageData imageData = ImageDataFactory.create(customImageUrl);
                Image image = new Image(imageData);
                image.setWidth(100);
                image.setHeight(100);
                image.setFixedPosition(pdf.getNumberOfPages(), 250, 650);
                PdfCanvas clippingCanvas = new PdfCanvas(pdf.getPage(pdf.getNumberOfPages()));
                clippingCanvas.saveState();
                clippingCanvas.circle(300, 700, 50);
                clippingCanvas.clip();
                clippingCanvas.endPath();
                document.add(image);
                clippingCanvas.restoreState();
            } catch (Exception e) {
                System.out.println("Error cargando imagen circular: " + e.getMessage());
            }
        }

        Paragraph name = new Paragraph(obituary.getName())
                .setFontSize(24)
                .setBold()
                .setFontColor(textColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(160);
        document.add(name);
        
        String birth = obituary.getBirthDate() != null ? obituary.getBirthDate().format(formatter) : "¿?";
        String death = obituary.getDeathDate() != null ? obituary.getDeathDate().format(formatter) : "¿?";
        Paragraph dates = new Paragraph(birth + " - " + death)
                .setFontSize(14)
                .setFontColor(textColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(dates);
        String farewellPhrase = obituary.getFarewellPhrase();
        if (farewellPhrase != null && !farewellPhrase.isBlank()) {
            Paragraph phrase = new Paragraph("\"" + farewellPhrase + "\"")
                    .setFontSize(16)
                    .setItalic()
                    .setFontColor(textColor)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(phrase);
        }
        String farewellMessage = obituary.getFarewellMessage();
        if (farewellMessage != null && !farewellMessage.isBlank()) {
            Paragraph message = new Paragraph(farewellMessage)
                    .setFontSize(12)
                    .setFontColor(textColor)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(message);
        }

        document.close();
        return baos.toByteArray();
    }

    @Transactional
    public void sendEmailWithAttachment(String to, String subject, String body, byte[] pdfBytes, String filename) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true = multipart
        helper.setFrom(defaultFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        helper.addAttachment(filename, new ByteArrayDataSource(pdfBytes, "application/pdf"));

        mailSender.send(mimeMessage);
    }

    @Transactional
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
    
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
    
        mailSender.send(mimeMessage);
    }

    public Color getColorFromString(String colorString) {
        try {
            String[] rgb = colorString.split(",");
            int r = Integer.parseInt(rgb[0].trim());
            int g = Integer.parseInt(rgb[1].trim());
            int b = Integer.parseInt(rgb[2].trim());
            return new DeviceRgb(r, g, b);
        } catch (Exception e) {
            return new DeviceRgb(0, 0, 0);
        }
    }
}
