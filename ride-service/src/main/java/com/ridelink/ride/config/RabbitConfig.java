package com.ridelink.ride.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	public static final String EXCHANGE = "ridelink.rides";
	public static final String RK_COMPLETED = "ride.completed";
	public static final String RK_CANCELLED = "ride.cancelled";

	@Bean
	TopicExchange rideExchange() {
		return new TopicExchange(EXCHANGE, true, false);
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
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(converter);
		return template;
	}
}
