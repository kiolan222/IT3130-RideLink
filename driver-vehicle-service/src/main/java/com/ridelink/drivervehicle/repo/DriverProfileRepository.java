package com.ridelink.drivervehicle.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ridelink.drivervehicle.domain.Availability;
import com.ridelink.drivervehicle.domain.DriverProfile;

public interface DriverProfileRepository extends MongoRepository<DriverProfile, String> {

	Optional<DriverProfile> findByAccountId(String accountId);

	List<DriverProfile> findByAvailabilityAndServiceAreaIgnoreCase(Availability availability, String serviceArea);
}
