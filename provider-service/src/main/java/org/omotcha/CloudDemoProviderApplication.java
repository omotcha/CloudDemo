package org.omotcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CloudDemoProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudDemoProviderApplication.class, args);
    }

}
