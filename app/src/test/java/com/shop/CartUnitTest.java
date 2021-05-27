package com.shop;

import com.shop.models.Cart;

import org.junit.Test;

import static org.junit.Assert.*;

public class CartUnitTest {
    Cart cart = new Cart("1234", "Glasses", 580, "2", "0", "");

    @Test
    public void getImage() {
        assertNotNull(cart.getImage());
        assertEquals(cart.getImage(), "");
    }

    @Test
    public void getPid() {
        assertNotNull(cart.getPid());
        assertEquals(cart.getPid(), "1234");
    }

    @Test
    public void getPname() {
        assertNotNull(cart.getPname());
        assertEquals(cart.getPname(), "Glasses");
    }

    @Test
    public void getPrice() {
        assertNotEquals(cart.getPrice(), 0);
        assertEquals(cart.getPrice(), 580, 0);
    }

    @Test
    public void getQuantity() {
        assertNotNull(cart.getQuantity());
        assertEquals(cart.getQuantity(), "2");
    }
}