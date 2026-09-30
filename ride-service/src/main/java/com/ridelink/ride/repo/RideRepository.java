package com.ridelink.ride.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ridelink.ride.domain.Ride;

public interface RideRepository extends MongoRepository<Ride, String> {

	List<Ride> findByPassengerIdOrderByCreatedAtDesc(String passengerId);

	List<Ride> findByDriverIdOrderByCreatedAtDesc(String driverId);
}
