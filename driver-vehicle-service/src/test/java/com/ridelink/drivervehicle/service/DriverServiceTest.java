package com.ridelink.drivervehicle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ridelink.drivervehicle.domain.Availability;
import com.ridelink.drivervehicle.domain.DriverProfile;
import com.ridelink.drivervehicle.repo.DriverProfileRepository;
import com.ridelink.drivervehicle.web.ApiException;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

	@Mock
	private DriverProfileRepository repository;

	@InjectMocks
	private DriverService driverService;

	@Test
	void eligibleRequiresArea() {
		assertThrows(ApiException.class, () -> driverService.findEligible("  "));
	}

	@Test
	void eligibleReturnsOnlyMatchingProfiles() {
		DriverProfile profile = new DriverProfile();
		profile.setId("p1");
		profile.setAccountId("d1");
		profile.setServiceArea("Colombo");
		profile.setAvailability(Availability.AVAILABLE);
		when(repository.findByAvailabilityAndServiceAreaIgnoreCase(Availability.AVAILABLE, "Colombo"))
				.thenReturn(List.of(profile));

		assertEquals(1, driverService.findEligible("Colombo").size());
		assertEquals("d1", driverService.findEligible("Colombo").get(0).accountId());
	}
}
