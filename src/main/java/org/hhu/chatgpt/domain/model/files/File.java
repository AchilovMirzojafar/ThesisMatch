package org.hhu.chatgpt.domain.model.files;

import java.time.LocalDateTime;

public class File {

    private String title;
    private String description;

    // Systeminformationen
    private String uploadedBy;
    private LocalDateTime uploadedAt;

    // Dateiinformationen
    private String storedFilename;
    private String originalFilename;
    private FileType fileType;

    public File(String title,
                String description,
                String uploadedBy,
                LocalDateTime uploadedAt,
                String storedFilename,
                String originalFilename,
                FileType fileType) {

        this.title = title;
        this.description = description;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
        this.storedFilename = storedFilename;
        this.originalFilename = originalFilename;
        this.fileType = fileType;
    }

    // Getter
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public FileType getFileType() {
        return fileType;
    }
}

