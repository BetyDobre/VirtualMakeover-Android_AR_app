package com.shop;

import com.shop.models.Comments;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommentsUnitTest {
    Comments comment = new Comments("very nice product", "", "Beatrice Dobre", "betty_dobre@yahoo.com", "06Mar2021", "2:00:00PM");

    @Test
    public void getUserEmail() {
        assertNotNull(comment.getUserEmail());
        assertTrue(comment.getUserEmail().contains("@"));
        assertEquals(comment.getUserEmail(), "betty_dobre@yahoo.com");
    }

    @Test
    public void getContent() {
        assertNotNull(comment.getContent());
        assertEquals(comment.getContent(), "very nice product");
    }

    @Test
    public void getUserName() {
        assertNotNull(comment.getUserName());
        assertEquals(comment.getUserName(), "Beatrice Dobre");
    }
}