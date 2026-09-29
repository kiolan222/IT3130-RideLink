package com.ridelink.farepayment.service;

import java.util.Locale;
import java.util.Map;

/**
 * fare = 150 + 50 * km + 10 * minutes.
 * km from Haversine when both coordinates exist; otherwise a place-name table (default 8 km).
 * minutes = km * 1.5 (assumes ~40 km/h).
 */
public class FareCalculator {

	public static final double BASE_FARE = 150;
	public static final double PER_KM = 50;
	public static final double PER_MINUTE = 10;
	public static final double DEFAULT_KM = 8;
	private static final Map<String, Double> PLACE_KM = Map.of(
			"colombo|kandy", 115.0,
			"colombo|galle", 116.0,
			"colombo|negombo", 37.0,
			"kandy|nuwara eliya", 77.0);

	public FareBreakdown estimate(String pickup, String destination, Double pickupLat, Double pickupLng,
			Double destLat, Double destLng) {
		double km;
		String basis;
		if (pickupLat != null && pickupLng != null && destLat != null && destLng != null) {
			km = haversineKm(pickupLat, pickupLng, destLat, destLng);
			basis = "HAVERSINE";
		}
		else {
			km = lookupPlaceKm(pickup, destination);
			basis = "PLACE_TABLE";
		}
		km = Math.round(km * 10.0) / 10.0;
		double minutes = Math.round(km * 1.5 * 10.0) / 10.0;
		double amount = round2(BASE_FARE + PER_KM * km + PER_MINUTE * minutes);
		return new FareBreakdown(km, minutes, amount, basis);
	}

	private double lookupPlaceKm(String pickup, String destination) {
		if (pickup == null || destination == null) {
			return DEFAULT_KM;
		}
		String key = normalize(pickup) + "|" + normalize(destination);
		String reverse = normalize(destination) + "|" + normalize(pickup);
		if (PLACE_KM.containsKey(key)) {
			return PLACE_KM.get(key);
		}
		if (PLACE_KM.containsKey(reverse)) {
			return PLACE_KM.get(reverse);
		}
		return DEFAULT_KM;
	}

	private String normalize(String place) {
		return place.trim().toLowerCase(Locale.ROOT);
	}

	static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
		double earthRadius = 6371.0;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
						* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}

	private double round2(double value) {
		return Math.round(value * 100.0) / 100.0;
	}

	public record FareBreakdown(double distanceKm, double estimatedMinutes, double amount, String basis) {
	}
}
