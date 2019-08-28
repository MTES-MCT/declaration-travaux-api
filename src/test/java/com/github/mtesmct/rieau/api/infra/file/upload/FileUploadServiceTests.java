package com.github.mtesmct.rieau.api.infra.file.upload;

import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.github.mtesmct.rieau.api.infra.file.upload.FileUploadService;

import static org.hamcrest.core.Is.is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileUploadServiceTests {
    @Autowired
    private FileUploadService fileUploadService;

    @Test
    public void readTest() throws IOException {
        File storedFile = this.fileUploadService
                .store("test.txt", new ByteArrayInputStream("test file".getBytes()));
        Path readPath = this.fileUploadService
                .read(storedFile.getName());
        assertThat(readPath.toFile().exists(), is(true));
    }
    @Test
    public void storeTest() throws IOException {
        File storedFile = this.fileUploadService
                .store("test.txt", new ByteArrayInputStream("test file".getBytes()));
        assertThat(storedFile.exists(), is(true));
    }
}