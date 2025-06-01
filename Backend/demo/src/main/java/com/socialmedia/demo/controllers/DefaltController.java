package com.socialmedia.demo.controllers;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DefaltController {

    @GetMapping
    public String hello() {
        return "Hello World";
    }
}
