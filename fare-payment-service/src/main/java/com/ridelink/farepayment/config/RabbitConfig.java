package com.ridelink.farepayment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;

@Configuration
public class RabbitConfig {

	public static final String EXCHANGE = "ridelink.rides";
	public static final String QUEUE = "fare.ride.lifecycle";
	public static final String RK_COMPLETED = "ride.completed";
	public static final String RK_CANCELLED = "ride.cancelled";

	@Bean
	TopicExchange rideExchange() {
		return new TopicExchange(EXCHANGE, true, false);
	}

	@Bean
	Queue fareQueue() {
		return new Queue(QUEUE, true);
	}

	@Bean
	Binding completedBinding(Queue fareQueue, TopicExchange rideExchange) {
		return BindingBuilder.bind(fareQueue).to(rideExchange).with(RK_COMPLETED);
	}

	@Bean
	Binding cancelledBinding(Queue fareQueue, TopicExchange rideExchange) {
		return BindingBuilder.bind(fareQueue).to(rideExchange).with(RK_CANCELLED);
	}

	@Bean
	Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
		DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
		typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
		converter.setJavaTypeMapper(typeMapper);
		return converter;
	}

	@Bean
	SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
			Jackson2JsonMessageConverter converter) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(converter);
		return factory;
	}
}
