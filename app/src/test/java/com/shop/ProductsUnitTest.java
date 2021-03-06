package com.shop;

import com.shop.models.Products;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProductsUnitTest {
    Products product = new Products("Glasses", "for all ages","","glasses","", "06MAR2021", "2:47:00PM", 580);

    @Test
    public void getPname() {
        assertNotNull(product.getPname());
        assertEquals(product.getPname(), "Glasses");
    }

    @Test
    public void getDescription() {
        assertNotNull(product.getDescription());
        assertEquals(product.getDescription(), "for all ages");
    }

    @Test
    public void getPrice() {
        assertNotEquals(product.getPrice(), 0);
        assertEquals(product.getPrice(), 580);
    }

    @Test
    public void getImage() {
        assertNotNull(product.getImage());
        assertEquals(product.getImage(), "");
    }

    @Test
    public void getCategory() {
        assertNotNull(product.getCategory());
        assertEquals(product.getCategory(), "glasses");
    }

    @Test
    public void getPid() {
        assertNotNull(product.getPid());
        product.setPid(product.getDate() + product.getTime());
        assertEquals(product.getPid(), "06MAR20212:47:00PM");
    }
}