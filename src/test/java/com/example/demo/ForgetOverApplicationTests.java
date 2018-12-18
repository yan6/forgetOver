package com.example.demo;

import com.example.demo.service.message.activemq.ActiveMqMessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ForgetOverApplicationTests {

	@Autowired
	private ActiveMqMessageSender sender;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testSend(){
		for (int i = 1; i < 6; i++) {
			this.sender.sendByQueue("hello activemq queue " + i);
		}
	}

	@Test
	public void testSendByTopic() {
		for (int i = 1; i < 6; i++) {
			this.sender.sendByTopic("hello activemq topic " + i);
		}
	}
}
