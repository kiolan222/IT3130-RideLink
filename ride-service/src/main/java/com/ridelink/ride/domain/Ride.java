package com.ridelink.ride.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rides")
public class Ride {

	@Id
	private String id;
	private String passengerId;
	private String driverId;
	private String pickup;
	private String destination;
	private Double pickupLat;
	private Double pickupLng;
	private Double destLat;
	private Double destLng;
	private RideStatus status;
	private Double estimatedFare;
	private Instant createdAt;
	private Instant updatedAt;
	private List<StatusChange> history = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(String passengerId) {
		this.passengerId = passengerId;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getPickup() {
		return pickup;
	}

	public void setPickup(String pickup) {
		this.pickup = pickup;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Double getPickupLat() {
		return pickupLat;
	}

	public void setPickupLat(Double pickupLat) {
		this.pickupLat = pickupLat;
	}

	public Double getPickupLng() {
		return pickupLng;
	}

	public void setPickupLng(Double pickupLng) {
		this.pickupLng = pickupLng;
	}

	public Double getDestLat() {
		return destLat;
	}

	public void setDestLat(Double destLat) {
		this.destLat = destLat;
	}

	public Double getDestLng() {
		return destLng;
	}

	public void setDestLng(Double destLng) {
		this.destLng = destLng;
	}

	public RideStatus getStatus() {
		return status;
	}

	public void setStatus(RideStatus status) {
		this.status = status;
	}

	public Double getEstimatedFare() {
		return estimatedFare;
	}

	public void setEstimatedFare(Double estimatedFare) {
		this.estimatedFare = estimatedFare;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<StatusChange> getHistory() {
		if (history == null) {
			history = new ArrayList<>();
		}
		return history;
	}

	public void setHistory(List<StatusChange> history) {
		this.history = history;
	}
}
