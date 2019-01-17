package com.example.demo.service.message.rabbitmq;

import com.example.demo.tool.queue.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitConfig.QUEUE_A)
public class RabbitMqMessageReceiver {
    //rabbit监听器，监听A队列消息

    @RabbitHandler
    public void process(String content) {
        System.out.println("接收处理队列A当中的消息： " + content);
    }

}
