package com.bfxy.springboot.conusmer;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

@Component
public class RabbitReceiver {


	/**
	 * 这个地方进行申明的队列，交换机会自动创建。
	 * 但是队列，交换机名称已经写死
	 * @param message
	 * @param channel
	 * @throws Exception
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "queue-1", durable="true"),
			exchange = @Exchange(value = "exchange-1", durable="true", type= "topic", ignoreDeclarationExceptions = "true"),
			key = "springboot.*"
			)
	)
	@RabbitHandler
	public void onMessage(Message message, Channel channel) throws Exception {
		System.err.println("--------------------------------------");
		System.err.println("消费端Payload: " + message.getPayload());
		Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
		//手工ACK
		channel.basicAck(deliveryTag, false);
	}
	
	
	/**
	 * 从配置文件中获取所需要的配置
	 * @param order
	 * @param channel
	 * @param headers
	 * @throws Exception
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}", 
			durable="${spring.rabbitmq.listener.order.queue.durable}"),

			exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}", 
			durable="${spring.rabbitmq.listener.order.exchange.durable}", 
			type= "${spring.rabbitmq.listener.order.exchange.type}", 
			ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),

			key = "${spring.rabbitmq.listener.order.key}"
			)
	)
	@RabbitHandler
	public void onOrderMessage(@Payload com.bfxy.springboot.entity.Order order, 
			Channel channel, 
			@Headers Map<String, Object> headers) throws Exception {
		System.err.println("--------------------------------------");
		System.err.println("消费端order: " + order.getId());
		Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
		//手工ACK
		channel.basicAck(deliveryTag, false);
	}
	
	
}
