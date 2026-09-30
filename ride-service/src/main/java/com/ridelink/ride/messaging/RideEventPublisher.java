package com.ridelink.ride.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.ridelink.ride.config.RabbitConfig;
import com.ridelink.ride.domain.Ride;

@Component
public class RideEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(RideEventPublisher.class);

	private final RabbitTemplate rabbitTemplate;

	public RideEventPublisher(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void publishCompleted(Ride ride) {
		publish(RabbitConfig.RK_COMPLETED, ride, "COMPLETED");
	}

	public void publishCancelled(Ride ride) {
		publish(RabbitConfig.RK_CANCELLED, ride, "CANCELLED");
	}

	private void publish(String routingKey, Ride ride, String type) {
		RideLifecycleEvent event = new RideLifecycleEvent(ride.getId(), ride.getPassengerId(), ride.getDriverId(),
				ride.getPickup(), ride.getDestination(), ride.getPickupLat(), ride.getPickupLng(), ride.getDestLat(),
				ride.getDestLng(), type);
		try {
			rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, event);
		}
		catch (Exception ex) {
			log.warn("Failed to publish {} for ride {}: {}", type, ride.getId(), ex.getMessage());
		}
	}
}
