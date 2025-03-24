package com.caronte.caronte.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Component
public class MediaHandler {

    @Value("${cloudinary.url}")
    private String cloudinaryImageUrl;

    private static BufferedImage base64ToImage(String base64String) {
        BufferedImage image = null;
        try {
            if (base64String != null && base64String.contains(",")) {
                base64String = base64String.substring(base64String.indexOf(",") + 1);
            }
    
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            image = ImageIO.read(bis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public String uploadImageToCloudinary(String base64String, String folder) {
        Cloudinary cloudinaryImage = new Cloudinary(cloudinaryImageUrl);
        BufferedImage image = base64ToImage(base64String);
        File tempFile = null;
        try {
            tempFile = File.createTempFile("upload_", ".png");
            ImageIO.write(image, "png", tempFile);

            Map options = ObjectUtils.asMap("folder", "images/" + folder + "/");
            Map uploadResult = cloudinaryImage.uploader().upload(tempFile, options);
            
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (tempFile != null && tempFile.exists()) 
                tempFile.delete();
        }
    }
}
