package com.weyland.bishop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weyland.bishop.annotation.WeylandWatchingYou;

@RestController
@RequestMapping("/prototype")
public class DemoController {

    @GetMapping("/status")
    @WeylandWatchingYou
    public String checkStatus() {
        return "Operational status: NOMINAL";
    }

    @GetMapping("/introduce")
    @WeylandWatchingYou
    public String introduce() {
        return "I am Bishop, synthetic human Weyland Yutani model";
    }
}