package com.github.mtesmct.rieau.api.depositaire.infra.date;

import static org.junit.Assert.assertThat;

import java.util.Date;

import com.github.mtesmct.rieau.api.depositaire.infra.adau.xml.XmlUnmarshallException;

import static org.hamcrest.core.Is.is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DateConverterTests {
    @Autowired
    @Qualifier("dateConverter")
    private DateConverter dateConverter;
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;
    @Autowired
    @Qualifier("adauDateTimeConverter")
    private DateConverter adauDateTimeConverter;

    @Test
    public void parseDateConverterTest() throws XmlUnmarshallException {
        Date date = this.dateConverter.parse("01/01/2019");
        assertThat(this.dateConverter.format(date), is("01/01/2019"));
    }

    @Test
    public void parseDateTimeConverterTest() throws XmlUnmarshallException {
        Date date = this.dateTimeConverter.parse("01/01/2019 01:02:03");
        assertThat(this.dateTimeConverter.format(date), is("01/01/2019 01:02:03"));
    }

    @Test
    public void parseADAUDateTimeConverterTest() throws XmlUnmarshallException {
        Date date = this.adauDateTimeConverter.parse("2019-04-03T16:26:20.790+02:00");
        assertThat(this.adauDateTimeConverter.format(date), is("2019-04-03T16:26:20.790+02:00"));
    }
}