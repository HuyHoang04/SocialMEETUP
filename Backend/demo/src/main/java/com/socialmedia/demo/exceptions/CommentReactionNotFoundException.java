package com.socialmedia.demo.exceptions;

public class CommentReactionNotFoundException extends RuntimeException {
    public CommentReactionNotFoundException(String message) {
        super(message);
    }
}