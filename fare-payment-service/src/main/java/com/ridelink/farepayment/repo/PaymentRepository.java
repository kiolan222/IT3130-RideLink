package com.ridelink.farepayment.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ridelink.farepayment.domain.Payment;

public interface PaymentRepository extends MongoRepository<Payment, String> {

	Optional<Payment> findByRideId(String rideId);
}
