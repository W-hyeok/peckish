package com.peckish.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Error implements ErrorController {
    private String ERROR_PATH = "/error";

//    @RequestMapping(value = "/error")
//    public String handleError(HttpServletRequest request) {
//        Object status = request.getAttribute("javax.servlet.error.status_code");
//        if (status != null) {
//            int statusCode = Integer.parseInt(status.toString());
//            if (statusCode == 404) {
//                request.setAttribute("javax.servlet.error.status_code", 404);
//                request.setAttribute("javax.servlet.error.message", "404 Not Found");
//                return "404 Not Found";
//            }
//            if (statusCode == 405) {
//                request.setAttribute("javax.servlet.error.status_code", 405);
//            }
//            if (statusCode == 500) {
//                request.setAttribute("javax.servlet.error.status_code", 500);
//            }
//
//        }
//    }
}
