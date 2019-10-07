package com.github.mtesmct.rieau.api.infra.date;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"app.datetime.mock=01/01/2019 00:00:00","app.year.mock=2019"})
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
        assertEquals(this.dateConverter.format(date), "01/01/2019");
    }

    @Test
    public void parseDateTimeConverterTest(){
        Date date = this.dateTimeConverter.parse("01/01/2019 01:02:03");
        assertEquals(this.dateTimeConverter.format(date), "01/01/2019 01:02:03");
    }
}