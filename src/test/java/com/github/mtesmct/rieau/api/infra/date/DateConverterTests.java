package com.github.mtesmct.rieau.api.infra.date;

import static org.junit.Assert.assertThat;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"app.datetime.mock=01/01/2019 00:00:00"})
public class DateConverterTests {
    @Autowired
    @Qualifier("dateConverter")
    private DateConverter dateConverter;
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;

    @Test
    public void parseDateConverterTest() {
        Date date = this.dateConverter.parse("01/01/2019");
        assertThat(this.dateConverter.format(date), is("01/01/2019"));
    }

    @Test
    public void parseDateTimeConverterTest(){
        Date date = this.dateTimeConverter.parse("01/01/2019 01:02:03");
        assertThat(this.dateTimeConverter.format(date), is("01/01/2019 01:02:03"));
    }
}