package com.example.demo.service.message.activemq;

import com.example.demo.tool.queue.ActiveMqConfiguration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqMessageReceiver {
//    @JmsListener(destination = ActiveMqConfiguration.QUEUE_NAME)
//    public void receiveByQueue(String message) {
//        System.out.println("接收队列消息:" + message);
//    }
//
//    @JmsListener(destination = ActiveMqConfiguration.TOPIC_NAME)
//    public void receiveByTopic(String message) {
//        System.out.println("接收主题消息:" + message);
//    }
}
