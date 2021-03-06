package com.shop;

import com.shop.models.AdminOrders;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrdersUnitTest {
    AdminOrders order = new AdminOrders("betty_dobre@yahoo.com", "str. Lalelelor", "Ploiesti", "", "Beatrice Dobre", "0725341675", "not shipped", "", "1080","card at delivery");

    @Test
    public void getEmail() {
        assertNotNull(order.getEmail());
        assertTrue(order.getEmail().contains("@"));
        assertEquals(order.getEmail(), "betty_dobre@yahoo.com");
    }

    @Test
    public void getAddress() {
        assertNotNull(order.getAddress());
        assertEquals(order.getAddress(), "str. Lalelelor");
    }

    @Test
    public void getCity() {
        assertNotNull(order.getCity());
        assertEquals(order.getCity(), "Ploiesti");
    }

    @Test
    public void getName() {
        assertNotNull(order.getName());
        assertEquals(order.getName(), "Beatrice Dobre");
    }

    @Test
    public void getPhone() {
        assertNotNull(order.getPhone());
        assertTrue(order.getPhone().length() == 10);
        assertEquals(order.getPhone(), "0725341675");
    }

    @Test
    public void getState() {
        assertNotNull(order.getState());
        assertEquals(order.getState(), "not shipped");
    }

    @Test
    public void getTotalAmount() {
        assertNotNull(order.getTotalAmount());
        assertEquals(order.getTotalAmount(), "1080");
    }

    @Test
    public void getPayment() {
        assertNotNull(order.getPayment());
        assertEquals(order.getPayment(), "card at delivery");
    }
}