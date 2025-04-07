package com.caronte.caronte.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Component
public class MediaHandler {

    private final Cloudinary cloudinaryImage;
    private final Cloudinary cloudinaryVideo;

    public MediaHandler(@Value("${cloudinary.image.url}") String cloudinaryImageUrl,
            @Value("${cloudinary.video.url}") String cloudinaryVideoUrl) {
        this.cloudinaryImage = new Cloudinary(cloudinaryImageUrl);
        this.cloudinaryVideo = new Cloudinary(cloudinaryVideoUrl);
    }

    @SuppressWarnings("rawtypes")
    public String uploadImageToCloudinary(String base64String, String folder) {
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

    @SuppressWarnings("rawtypes")
    public String uploadVideoToCloudinary(File videoFile) {
        try {
            Map options = ObjectUtils.asMap("folder", "videos/messages/", "resource_type", "video");
            Map uploadResult = cloudinaryVideo.uploader().upload(videoFile, options);
            videoFile.delete();
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
         
    // TODO: complete functionality to perform deletion
    @SuppressWarnings("rawtypes")
    public String deleteImageFromCloudinary(String imageUrl) {        
        try {
            System.out.println("Original image URL: " + imageUrl);
            
            String[] parts = imageUrl.split("/upload/");
            System.out.println("Parts after split: " + parts[1]);

            String filePath = parts[1].split("/", 2)[1].split("\\.")[0];
            System.out.println("Extracted Public ID: " + filePath);
            
            Map result = cloudinaryImage.uploader().destroy(filePath, ObjectUtils.emptyMap());
            System.out.println("Cloudinary result: " + result);
            
            if(result.containsKey("result") && result.get("result").equals("ok")) {
                System.out.println("Image deleted successfully.");
            } else {
                System.out.println("Image deletion failed: " + result);
            }
    
            return result.get("result").toString();
            
        } catch (Exception e) {
            System.err.println("Error while deleting image from Cloudinary: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage base64ToImage(String base64String) {
        BufferedImage image = null;
        try {
            byte[] imageBytes = decoder(base64String);
            if (imageBytes.length > 5 * 1024 * 1024) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image size exceeds 5 MB");
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            return ImageIO.read(bis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    @SuppressWarnings("unused")
    public File base64ToVideo(String base64String) {
        File videoFile = null;
        try {
            byte[] videoBytes = decoder(base64String);
            videoFile = File.createTempFile("temp-video-", ".mp4");
            try (FileOutputStream fos = new FileOutputStream(videoFile)) {
                fos.write(videoBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoFile;

    }

    private static byte[] decoder(String base64String) {
        if (base64String != null && base64String.contains(","))
            base64String = base64String.substring(base64String.indexOf(",") + 1);

        return Base64.getDecoder().decode(base64String);
    }

}