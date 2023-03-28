package org.omotcha.controllers;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/testD")
    @SentinelResource(value = "testD-resource", blockHandler = "blockHandlerTestD")
    public String testD() {
        initFlowRules();
        log.info("test D success: "+serverPort);
        return "test D success: "+serverPort;
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

    public String blockHandlerTestD(BlockException exception) {
        log.info(Thread.currentThread().getName() + "TestD access denied! Please try it later.");
        return "TestD access denied! Please try it later.";
    }

    private static void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        //定义一个限流规则对象
        FlowRule rule = new FlowRule();
        //资源名称
        rule.setResource("testD-resource");
        //限流阈值的类型
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置 QPS 的阈值为 2
        rule.setCount(2);
        rules.add(rule);
        //定义限流规则
        FlowRuleManager.loadRules(rules);
    }
}
