package com.github.mtesmct.rieau.api.infra.application.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.mtesmct.rieau.api.application.auth.MockUsers;

import org.junit.jupiter.api.Test;

public class BasicUsersServiceTests {
    
    private BasicUsersService usersService;

    @Test
    public void nomsTest(){
        this.usersService = new BasicUsersService();
        assertEquals(2, this.usersService.noms(MockUsers.JEAN_MARTIN).length);
        assertEquals("jean", this.usersService.noms(MockUsers.JEAN_MARTIN)[0]);
        assertEquals("martin", this.usersService.noms(MockUsers.JEAN_MARTIN)[1]);
    }
    
}