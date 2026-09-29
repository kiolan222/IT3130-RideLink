package com.ridelink.ride.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.web.ApiException;

@Component
public class PeerClients {

	private final RestClient driverClient;
	private final RestClient fareClient;

	public PeerClients(@Value("${ridelink.driver-service-url}") String driverUrl,
			@Value("${ridelink.fare-service-url}") String fareUrl) {
		this.driverClient = RestClient.builder().baseUrl(driverUrl).build();
		this.fareClient = RestClient.builder().baseUrl(fareUrl).build();
	}

	public List<EligibleDriver> findEligible(String area, String bearerToken) {
		try {
			List<EligibleDriver> drivers = driverClient.get()
					.uri("/api/drivers/eligible?area={area}", area)
					.header(HttpHeaders.AUTHORIZATION, bearerToken)
					.retrieve()
					.body(new ParameterizedTypeReference<>() {
					});
			return drivers == null ? List.of() : drivers;
		}
		catch (RestClientException ex) {
			throw new ApiException(HttpStatus.BAD_GATEWAY, "Driver service unavailable: " + ex.getMessage());
		}
	}

	public void updateAvailability(String accountId, String availability, String bearerToken) {
		try {
			driverClient.patch()
					.uri("/api/drivers/{accountId}/availability", accountId)
					.header(HttpHeaders.AUTHORIZATION, bearerToken)
					.contentType(MediaType.APPLICATION_JSON)
					.body(Map.of("availability", availability))
					.retrieve()
					.toBodilessEntity();
		}
		catch (RestClientException ex) {
			throw new ApiException(HttpStatus.BAD_GATEWAY, "Could not update driver availability: " + ex.getMessage());
		}
	}

	public FareEstimate estimateFare(CreateRideRequest request, String bearerToken) {
		try {
			return fareClient.post()
					.uri("/api/fares/estimate")
					.header(HttpHeaders.AUTHORIZATION, bearerToken)
					.contentType(MediaType.APPLICATION_JSON)
					.body(new EstimatePayload(request.pickup(), request.destination(), request.pickupLat(),
							request.pickupLng(), request.destLat(), request.destLng()))
					.retrieve()
					.body(FareEstimate.class);
		}
		catch (RestClientException ex) {
			throw new ApiException(HttpStatus.BAD_GATEWAY, "Fare service unavailable: " + ex.getMessage());
		}
	}

	public record EstimatePayload(String pickup, String destination, Double pickupLat, Double pickupLng, Double destLat,
			Double destLng) {
	}
}
