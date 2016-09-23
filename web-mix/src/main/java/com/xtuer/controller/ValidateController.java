package com.xtuer.controller;

import com.xtuer.bean.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

@Controller
public class ValidateController {
    @GetMapping("/validate-user")
    @ResponseBody
    public String validateUser(@ModelAttribute @Valid User user, BindingResult result) {
        System.out.println(result.hasErrors());

        if (result.hasErrors()) {
            // return result.getFieldErrors().toString();
            List<String> messages = new LinkedList<String>();

            for (FieldError error : result.getFieldErrors()) {
                messages.add(error.getDefaultMessage());
            }
            return messages.toString();
        }

        return "名字: " + user.getUsername() + ", Password: " + user.getPassword();
    }
}
