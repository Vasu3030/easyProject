package vasu.easyproject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/auth")
    public String hello() {
        return "Hello World";
    }
}
