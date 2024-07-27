package com.cool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class Welcome {

    @RequestMapping("/")
    public String welcome() {
        return "welcome";
    }
}
