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
            throw new IllegalStateException("CLOUDINARY_URL environment variable is not set.");
        }
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }

    public String uploadFile(File file, String folder, String publicIdHint) throws IOException {
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
    }
}


