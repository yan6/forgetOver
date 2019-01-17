package com.example.demo.tool.queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * activeMQ 队列相关
 */
@Configuration
public class ActiveMqConfiguration {
//
//    public static final String QUEUE_NAME = "activemq_queue";
//
//    public static final String TOPIC_NAME = "activemq_topic";
//
//    @Bean
//    public Queue queue() {
//        return new ActiveMQQueue(QUEUE_NAME);
//    }
//
//    @Bean
//    public Topic topic() {
//        return new ActiveMQTopic(TOPIC_NAME);
//    }
}
