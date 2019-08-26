package com.github.mtesmct.rieau.api.depositaire.infra.file.zip;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileZipServiceTests {
    @Autowired
    private FileZipService fileZipService;
    @Value("${app.file.zip.dest.dir}")
    private String fileZipDestDir;

    @Test
    public void unzipTest() throws IOException {
        this.fileZipService
                .unzip("src/test/fixtures/RitaGS.2019-04-03T16_26_40.674661355.A-9-X3UGO4V7-DAUA-2.ADER.ftp.zip");
        File messageFile = new File(this.fileZipDestDir + "/message.xml");
        assertThat(messageFile.exists(), is(true));
    }
}