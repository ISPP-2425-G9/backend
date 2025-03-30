package com.caronte.caronte.receiver;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.message.Message;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituraryRequestDto.ContactDto;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Paragraph;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;

@Service
public class ReceiverService {


    ReceiverRepository receiverRepository;

    JavaMailSender mailSender;

    public ReceiverService(ReceiverRepository receiverRepository, JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.receiverRepository = receiverRepository;
    }
    @Transactional
    public Receiver saveObituaryReceiver(String name, String telephone, String email, Obituary obituary) {
        Receiver receiver = new Receiver();
        receiver.setName(name);
        receiver.setTelephone(telephone);
        receiver.setEmail(email);
        receiver.setObituary(obituary);
        receiver = receiverRepository.saveAndFlush(receiver);
        return receiver;
    }
    @Transactional
    public void deleteReceiversByObituaryId(Obituary obituary) {
        receiverRepository.deleteByObituary(obituary);
        receiverRepository.flush();
    }
    @Transactional(readOnly = true)
    public List<ReceiverResponseDTO> getReceiversByObituaryId(Obituary obituary) {
        List<ReceiverResponseDTO> receivers = new ArrayList<>();
        List<Receiver> receiversList = receiverRepository.findByObituary(obituary);
        for (Receiver receiver : receiversList) {
            ReceiverResponseDTO receiverResponseDTO = new ReceiverResponseDTO();
            receiverResponseDTO.setId(receiver.getId());
            receiverResponseDTO.setName(receiver.getName());
            receiverResponseDTO.setTelephone(receiver.getTelephone());
            receiverResponseDTO.setEmail(receiver.getEmail());
            receivers.add(receiverResponseDTO);
        }

        return receivers;
    }

    @Transactional
    public Receiver saveMessageReceiver(String name, String telephone, String email, Message message) {
        Receiver receiver = new Receiver();
        receiver.setName(name);
        receiver.setTelephone(telephone);
        receiver.setEmail(email);
        receiver.setMessage(message);
        return receiverRepository.save(receiver);
    }

    @Transactional
    public Receiver updateMessageReceiver(Long id, String name, String telephone, String email) {
        Receiver receiver = receiverRepository.findById(id).orElseThrow(() -> new RuntimeException("Receiver not found"));
        if (!receiver.getName().equals(name)) {
            receiver.setName(name);
        }
        if (!receiver.getTelephone().equals(telephone)) {
            receiver.setTelephone(telephone);
        }
        if (!receiver.getEmail().equals(email)) {
            receiver.setEmail(email);
        }
        return receiverRepository.save(receiver);
    }

    public void notifyReceivers(List<ContactDto> contacts, Obituary obituary) {
        for (ContactDto contact : contacts) {
            try {
                byte[] pdfBytes = generateObituaryPdf(obituary);
                
                sendEmailWithAttachment(contact.getEmail(), "Esquela de " + obituary.getName(), 
                    "Adjunto encontrarás la esquela de " + obituary.getName(), 
                    pdfBytes, "esquela_" + obituary.getName() + ".pdf");

                System.out.println("Email enviado a: " + contact.getEmail());
            } catch (Exception e) {
                System.out.println("Error al notificar por email a: " + contact.getEmail() + " - " + e.getMessage());
            }
        }
    }

    public byte[] generateObituaryPdf(Obituary obituary) throws Exception {
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

    if (obituary.getCustomImageUrl() != null && !obituary.getCustomImageUrl().isEmpty()) {
        try {
            ImageData imageData = ImageDataFactory.create(obituary.getCustomImageUrl());
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

    // 4. Frase de despedida
    if (obituary.getFarewellPhrase() != null && !obituary.getFarewellPhrase().isBlank()) {
        Paragraph phrase = new Paragraph("\"" + obituary.getFarewellPhrase() + "\"")
                .setFontSize(16)
                .setItalic()
                .setFontColor(textColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(phrase);
    }

    // 5. Mensaje
    if (obituary.getFarewellMessage() != null && !obituary.getFarewellMessage().isBlank()) {
        Paragraph message = new Paragraph(obituary.getFarewellMessage())
                .setFontSize(12)
                .setFontColor(textColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(message);
    }

    document.close();
    return baos.toByteArray();
}

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] pdfBytes, String filename) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true = multipart

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        helper.addAttachment(filename, new ByteArrayDataSource(pdfBytes, "application/pdf"));

        mailSender.send(mimeMessage);
    } 

    public Color getColorFromString(String colorString) {
        try {
            if (colorString.contains(",")) {
                String[] rgb = colorString.split(",");
                int r = Integer.parseInt(rgb[0].trim());
                int g = Integer.parseInt(rgb[1].trim());
                int b = Integer.parseInt(rgb[2].trim());
                return new DeviceRgb(r, g, b);
            } else {
                if (colorString.startsWith("#")) colorString = colorString.substring(1);
                int r = Integer.parseInt(colorString.substring(0, 2), 16);
                int g = Integer.parseInt(colorString.substring(2, 4), 16);
                int b = Integer.parseInt(colorString.substring(4, 6), 16);
                return new DeviceRgb(r, g, b);
            }
        } catch (Exception e) {
            return new DeviceRgb(0, 0, 0); // negro por defecto
        }
    }
}
