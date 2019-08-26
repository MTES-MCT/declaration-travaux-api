package com.github.mtesmct.rieau.api.depositaire.infra.file.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface FileUploadService {
    public File store(String fileName, InputStream inputStream) throws IOException;
    public Path read(String fileName);
}