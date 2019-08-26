package com.github.mtesmct.rieau.api.depositaire.infra.file.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FileUploadSystemService implements FileUploadService {

    @Value("${app.file.upload.dir}")
    private String fileUploadDir;

    @Override
    public File store(String fileName, InputStream inputStream) throws IOException {
        File dirTarget = new File(this.fileUploadDir);
        if (!dirTarget.exists())
            dirTarget.mkdir();
        String cleanedFileName = StringUtils.cleanPath(fileName);
        Path fileStorageLocation = Paths.get(this.fileUploadDir).toAbsolutePath().normalize();
        Path targetLocation = fileStorageLocation.resolve(cleanedFileName);
        Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return targetLocation.toFile();
    }

    @Override
    public Path read(String fileName) {
        String cleanedFileName = StringUtils.cleanPath(fileName);
        Path fileStorageLocation = Paths.get(this.fileUploadDir).toAbsolutePath().normalize();
        return fileStorageLocation.resolve(cleanedFileName);
    }
}