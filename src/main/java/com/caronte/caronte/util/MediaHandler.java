package com.caronte.caronte.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import io.github.cdimascio.dotenv.Dotenv;

public class MediaHandler {

    public static BufferedImage base64ToImage(String base64String) {
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

    public static String uploadImageToCloudinary(BufferedImage image) {
        Dotenv dotenv = Dotenv.load();
        Cloudinary cloudinary_image = new Cloudinary(System.getenv("CLOUDINARY_IMAGES_URL"));

        try {
            File tempFile = File.createTempFile("upload_", ".png");
            ImageIO.write(image, "png", tempFile);

            Map<String, Object> options = ObjectUtils.asMap(
                "folder", "images/obituaries/"
            );

            Map uploadResult = cloudinary_image.uploader().upload(tempFile, options);
            tempFile.delete();
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
