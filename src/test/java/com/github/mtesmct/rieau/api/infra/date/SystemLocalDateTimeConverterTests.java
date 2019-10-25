package com.github.mtesmct.rieau.api.infra.date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SystemLocalDateTimeConverterTests {
    @Autowired
    private DateConverter<LocalDateTime> dateConverter;

    @Test
    public void parseDateConverterTest() {
        Optional<LocalDateTime> date = this.dateConverter.parse("01/01/2019");
        assertTrue(date.isPresent());
        assertEquals("01/01/2019", this.dateConverter.formatDate(date.get()));
    }

    @Test
    public void parseDateTimeConverterTest(){
        Optional<LocalDateTime> date = this.dateConverter.parse("01/01/2019 01:02:03");
        assertTrue(date.isPresent());
        assertEquals("01/01/2019 01:02:03", this.dateConverter.formatDateTime(date.get()));
    }

    @Test
    public void yearConverterTest(){
        Optional<LocalDateTime> date = this.dateConverter.parse("01/01/2019 01:02:03");
        assertTrue(date.isPresent());
        assertEquals("2019", this.dateConverter.formatYear(date.get()));
    }
}