package com.socialmedia.demo.exceptions;

public class PostReactionNotFoundException extends RuntimeException {
    public PostReactionNotFoundException(String message) {
        super(message);
    }
}