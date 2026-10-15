package com.campusfood;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** A first endpoint to check that the web server works. */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from CampusFood!";
    }
}
