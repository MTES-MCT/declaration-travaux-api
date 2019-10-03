package com.github.mtesmct.rieau.api.infra.http;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
public class JsonErrorController implements ErrorController {

    public static final String PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    private Map<String, Object> getErrorAttributes(WebRequest webRequest) {
        return errorAttributes.getErrorAttributes(webRequest, false);
    }

    @RequestMapping(value = PATH, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    ResponseEntity<ErrorJson> error(WebRequest webRequest, HttpServletResponse response) {
        return ResponseEntity.status(response.getStatus())
                .body(new ErrorJson(response.getStatus(), getErrorAttributes(webRequest)));
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}