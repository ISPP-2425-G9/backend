
package com.caronte.caronte.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class MediaHandler {

    private static final String imageToken = "modify_before_deploy";
    private static final String videoToken = "modify_before_deploy";

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

    public static String uploadImageToCloudinary(BufferedImage image, String folder) {
        Cloudinary cloudinary_image = new Cloudinary(imageToken);
        try {
            File tempFile = File.createTempFile("upload_", ".png");
            ImageIO.write(image, "png", tempFile);

            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", "images/" + folder + "/");

            Map uploadResult = cloudinary_image.uploader().upload(tempFile, options);
            tempFile.delete();
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String deleteImageFromCloudinary(String imageUrl) {
        Cloudinary cloudinary = new Cloudinary(imageToken);
        
        try {
            System.out.println("Original image URL: " + imageUrl);
            
            String[] parts = imageUrl.split("/upload/");
            System.out.println("Parts after split: " + parts[1]);

            String filePath = parts[1].split("/", 2)[1].split("\\.")[0];
            System.out.println("Extracted Public ID: " + filePath);
            
            Map<String, Object> result = cloudinary.uploader().destroy(filePath, ObjectUtils.emptyMap());
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
    
    public static File base64ToVideo(String base64String) {
    File videoFile = null;
    try {
        if (base64String != null && base64String.contains(",")) {
            base64String = base64String.substring(base64String.indexOf(",") + 1);
        }

        byte[] videoBytes = Base64.getDecoder().decode(base64String);

        videoFile = File.createTempFile("temp-video-", ".mp4");
        try (FileOutputStream fos = new FileOutputStream(videoFile)) {
            fos.write(videoBytes);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return videoFile;

    }

    public static String uploadVideoToCloudinary(File videoFile) {
        Cloudinary cloudinary_video = new Cloudinary(videoToken);
    
        try {
            Map<String, Object> options = ObjectUtils.asMap(
                "folder", "videos/messages/",
                "resource_type", "video"
            );
    
            Map uploadResult = cloudinary_video.uploader().upload(videoFile, options);
            videoFile.delete();
            return uploadResult.get("secure_url").toString();
    
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    


}
