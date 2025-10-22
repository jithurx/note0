package com.note0.simple;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {
        // Prefer standard CLOUDINARY_URL env var: cloudinary://<key>:<secret>@<cloud_name>
        String cloudinaryUrl = System.getenv("CLOUDINARY_URL");
        if (cloudinaryUrl == null || cloudinaryUrl.isBlank()) {
            // Replace these with your actual Cloudinary credentials
            // Get these from https://cloudinary.com/console
            this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "your_cloud_name_here",
                "api_key", "your_api_key_here", 
                "api_secret", "your_api_secret_here"
            ));
        } else {
            this.cloudinary = new Cloudinary(cloudinaryUrl);
        }
    }

    public String uploadFile(File file, String folder, String publicIdHint) throws IOException {
        try {
            String safeFolder = (folder == null || folder.isBlank()) ? "note0" : folder;
            String name = file.getName().toLowerCase();
            String resourceType;
            if (name.endsWith(".pdf")) {
                // PDFs should be uploaded as raw and explicitly set to public upload type
                resourceType = "raw";
            } else if (name.endsWith(".doc") || name.endsWith(".docx") || name.endsWith(".ppt") || name.endsWith(".pptx") || name.endsWith(".xls") || name.endsWith(".xlsx")) {
                resourceType = "raw";
            } else {
                resourceType = "auto";
            }
            Map uploadParams = ObjectUtils.asMap(
                    "folder", safeFolder,
                    "resource_type", resourceType,
                    "type", "upload",                // ensure public delivery
                    "access_mode", "public",         // avoid authenticated/private assets
                    "public_id", publicIdHint
            );
            Map result = cloudinary.uploader().upload(file, uploadParams);
            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                throw new IOException("Cloudinary did not return a secure_url");
            }
            return secureUrl.toString();
        } catch (Exception e) {
            if (e.getMessage().contains("Invalid API key") || e.getMessage().contains("demo")) {
                throw new IOException("Cloudinary API credentials are invalid. Please set up your Cloudinary account:\n" +
                    "1. Go to https://cloudinary.com and create a free account\n" +
                    "2. Get your credentials from the dashboard\n" +
                    "3. Set CLOUDINARY_URL environment variable or update CloudinaryService.java");
            }
            throw new IOException("Cloudinary upload failed: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a file from Cloudinary using its URL.
     * @param fileUrl The Cloudinary URL of the file to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isBlank()) {
                System.err.println("CloudinaryService: File URL is null or blank");
                return false;
            }
            
            // Extract public_id from Cloudinary URL
            String publicId = extractPublicIdFromUrl(fileUrl);
            if (publicId == null) {
                System.err.println("CloudinaryService: Could not extract public_id from URL: " + fileUrl);
                return false;
            }
            
            System.out.println("CloudinaryService: Attempting to delete public_id: " + publicId);
            
            // Determine resource type based on file extension
            String resourceType = determineResourceType(fileUrl);
            System.out.println("CloudinaryService: Using resource_type: " + resourceType);
            
            // Try with the determined resource type first
            Map deleteParams = ObjectUtils.asMap(
                "resource_type", resourceType,
                "type", "upload"
            );
            
            Map result = cloudinary.uploader().destroy(publicId, deleteParams);
            System.out.println("CloudinaryService: Delete result with " + resourceType + ": " + result);
            
            String resultValue = (String) result.get("result");
            boolean success = "ok".equals(resultValue);
            
            if (success) {
                System.out.println("CloudinaryService: File deleted successfully");
                return true;
            }
            
            // If failed and it's a PDF/document, try with "raw" resource type
            if (!success && (fileUrl.toLowerCase().contains(".pdf") || fileUrl.toLowerCase().contains(".doc"))) {
                System.out.println("CloudinaryService: Retrying with 'raw' resource type...");
                Map rawDeleteParams = ObjectUtils.asMap(
                    "resource_type", "raw",
                    "type", "upload"
                );
                
                result = cloudinary.uploader().destroy(publicId, rawDeleteParams);
                System.out.println("CloudinaryService: Delete result with raw: " + result);
                
                resultValue = (String) result.get("result");
                success = "ok".equals(resultValue);
                
                if (success) {
                    System.out.println("CloudinaryService: File deleted successfully with raw resource type");
                    return true;
                }
            }
            
            System.err.println("CloudinaryService: Delete failed with result: " + resultValue);
            return false;
            
        } catch (Exception e) {
            System.err.println("CloudinaryService: Error deleting file from Cloudinary: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Extracts the public_id from a Cloudinary URL.
     * @param url The Cloudinary URL
     * @return The public_id or null if extraction fails
     */
    private String extractPublicIdFromUrl(String url) {
        try {
            // Expected formats:
            // https://res.cloudinary.com/{cloud}/{resource_type}/upload/v{version}/{folder(s)}/{public_id}.{ext}
            // We need: {folder(s)}/{public_id} (without extension and without version)

            if (url == null || !url.contains("cloudinary.com")) {
                return null;
            }

            int uploadIdx = url.indexOf("/upload/");
            if (uploadIdx < 0) {
                return null;
            }

            // Part after '/upload/'
            String afterUpload = url.substring(uploadIdx + "/upload/".length());

            // Strip query/hash if any
            int qIdx = afterUpload.indexOf('?');
            if (qIdx >= 0) afterUpload = afterUpload.substring(0, qIdx);
            int hashIdx = afterUpload.indexOf('#');
            if (hashIdx >= 0) afterUpload = afterUpload.substring(0, hashIdx);

            // Remove leading version segment if present (e.g., v1761134242/...)
            if (afterUpload.startsWith("v") ) {
                int slashIdx = afterUpload.indexOf('/');
                if (slashIdx > 0) {
                    // Ensure it's actually a version like v123456...
                    String possibleVersion = afterUpload.substring(1, slashIdx);
                    if (possibleVersion.matches("\\d+")) {
                        afterUpload = afterUpload.substring(slashIdx + 1);
                    }
                }
            }

            // Now afterUpload should be: {folder(s)}/{fileName}.{ext}
            // Remove the extension from the last path segment only
            int lastSlash = afterUpload.lastIndexOf('/');
            if (lastSlash < 0) {
                // No folders, just filename
                int dotIdx = afterUpload.lastIndexOf('.');
                return (dotIdx > 0) ? afterUpload.substring(0, dotIdx) : afterUpload;
            } else {
                String folders = afterUpload.substring(0, lastSlash);
                String fileName = afterUpload.substring(lastSlash + 1);
                int dotIdx = fileName.lastIndexOf('.');
                String fileBase = (dotIdx > 0) ? fileName.substring(0, dotIdx) : fileName;
                return folders + "/" + fileBase;
            }

        } catch (Exception e) {
            System.err.println("Error extracting public_id from URL: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Determines the resource type based on the file URL.
     * @param url The Cloudinary URL
     * @return The resource type (raw, image, video, etc.)
     */
    private String determineResourceType(String url) {
        String lowerUrl = url.toLowerCase();
        
        if (lowerUrl.contains("/raw/")) {
            return "raw";
        } else if (lowerUrl.contains("/video/")) {
            return "video";
        } else if (lowerUrl.contains("/image/")) {
            return "image";
        } else {
            // Default to auto, but Cloudinary will handle it
            return "auto";
        }
    }
}


