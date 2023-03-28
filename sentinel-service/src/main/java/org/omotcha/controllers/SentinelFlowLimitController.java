package org.omotcha.controllers;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SentinelFlowLimitController {
    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/testA")
    public String testA(){
        return testAbySphU();
    }

    @GetMapping("/testB")
    public String testB(){
        return testBbySphO();
    }

    @GetMapping("/testC")
    @SentinelResource(value = "testCbyAnnotation")
    public String testC() {
        //business logic starts
        log.info("test C success: "+serverPort);
        return "test C success: "+serverPort;
        //business logic end
    }

    // defining sentinel resource by SphU
    public String testAbySphU() {
        Entry entry = null;
        try {
            entry = SphU.entry("testAbySphU");
            //business logic starts
            log.info("test A success: "+serverPort);
            return "test A success: "+serverPort;
            //business logic ends
        } catch (BlockException e1) {
            //dataflow control logic starts
            log.info("test A been limited: "+serverPort);
            return "test A been limited: "+serverPort;
            //dataflow control logic ends
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    // defining sentinel resource by SphO
    public String testBbySphO() {
        if (SphO.entry("testBbySphO")) {
            // make sure finally must be executed
            try {
                //business logic starts
                log.info("test B success: "+serverPort);
                return "test B success: "+serverPort;
                //business logic ends
            } finally {
                SphO.exit();
            }
        } else {
            // resource access denied, service would be downgraded or restricted
            //dataflow control logic starts
            log.info("test B been limited: "+serverPort);
            return "test B been limited: "+serverPort;
            //dataflow control logic ends
        }
    }
}
