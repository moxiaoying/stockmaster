package com.stock.master.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("stock")
public class MyController {
    @RequestMapping("/index")
    public String hello(){
        return "index";
    }

    @RequestMapping("system")
    public String system(){
        return "system/index";
    }
}
