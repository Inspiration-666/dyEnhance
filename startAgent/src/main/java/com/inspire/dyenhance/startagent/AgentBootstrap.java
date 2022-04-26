package com.inspire.dyenhance.startagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

/**
 * @author ：inspire
 * @date ：Created in 2022/4/18 9:47
 * @description：
 * @modified By：inspire
 * @version:
 */
public class AgentBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(AgentBootstrap.class);
    public static void premain(String agentOps, Instrumentation inst){

        logger.info("--------------------------------agent1!");
        inst.addTransformer(new ClassAdapter());
    }
}
