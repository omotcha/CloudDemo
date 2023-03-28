package org.omotcha.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeptController {
    @Value("${server.port}")
    private String serverPort;

    @GetMapping(value = "/dept/nacos/{id}")
    public String getPayment(@PathVariable("id") Integer id) {
        return "<h2>Success</h2>service name：spring-cloud-alibaba-provider<br /> service port： " +
                serverPort + "<br /> param：" + id;
    }

}
