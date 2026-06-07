package com.loantrack.service;

import com.loantrack.enums.DocumentType;
import com.loantrack.model.Document;
import com.loantrack.model.LoanApplication;
import com.loantrack.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    // Base directory for uploads based on your application.properties
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void saveDocument(LoanApplication application, MultipartFile file, DocumentType type) throws IOException {
        if (file.isEmpty()) {
            return;
        }

        // 1. Create a dedicated folder for this application ID
        Path uploadPath = Paths.get(UPLOAD_DIR + application.getApplicationId());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. Resolve the file name and save it to the physical disk
        String filename = type.name() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 3. Save the metadata to the database
        Document document = new Document();
        document.setApplication(application);
        document.setDocType(type);
        document.setOriginalFilename(file.getOriginalFilename());
        // Calculate size in KB
        document.setFileSizeKb((int) (file.getSize() / 1024)); 
        // Relative path to be served by the web server
        document.setFilePath("/uploads/" + application.getApplicationId() + "/" + filename); 

        documentRepository.save(document);
    }
}