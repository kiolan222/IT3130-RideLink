package com.ridelink.farepayment.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ridelink.farepayment.config.RabbitConfig;
import com.ridelink.farepayment.service.FarePaymentService;

@Component
public class RideEventListener {

	private final FarePaymentService farePaymentService;

	public RideEventListener(FarePaymentService farePaymentService) {
		this.farePaymentService = farePaymentService;
	}

	@RabbitListener(queues = RabbitConfig.QUEUE)
	public void onEvent(RideLifecycleEvent event) {
		if (event == null || event.type() == null) {
			return;
		}
		if ("COMPLETED".equalsIgnoreCase(event.type()) || "ride.completed".equalsIgnoreCase(event.type())) {
			farePaymentService.onRideCompleted(event);
		}
		else if ("CANCELLED".equalsIgnoreCase(event.type()) || "ride.cancelled".equalsIgnoreCase(event.type())) {
			farePaymentService.onRideCancelled(event);
		}
	}
}
