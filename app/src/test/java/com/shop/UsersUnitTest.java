package com.shop;

import com.shop.models.Users;

import org.junit.Test;

import static org.junit.Assert.*;

public class UsersUnitTest {
    Users user = new Users("mail@gmail.com", "Ion Popescu", "parola", "", "str. Bobalna");

    @Test
    public void getEmail(){
        assertNotNull(user.getEmail());
        assertTrue(user.getEmail().contains("@"));
        assertEquals(user.getEmail(), "mail@gmail.com");
    }

    @Test
    public void getName(){
        assertNotNull(user.getName());
        assertEquals(user.getName(), "Ion Popescu");
    }

    @Test
    public void getPassword(){
        assertTrue(user.getPassword().length() >= 4);
        assertEquals(user.getPassword(), "parola");
    }

    @Test
    public void getAddress(){
        assertEquals(user.getAddress(), "str. Bobalna");
    }
}