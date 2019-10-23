package com.github.mtesmct.rieau.api.infra.date;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.mtesmct.rieau.api.domain.services.DateService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"app.datetime.mock=01/01/2019 00:00:00","app.year.mock=2019"})
public class DateServiceTests {
    @Autowired
    private DateService dateService;

    @Test
    public void year(){
        assertEquals("2019", this.dateService.year());
    }
    
}