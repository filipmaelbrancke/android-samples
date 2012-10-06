package com.example.insuranceclaim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for the user interface.
 * 
 * @author filip
 */
public class VehicleContent {
	
	/**
	 * An array of sample vehicles.
	 */
	public static List<Vehicle> VEHICLES = new ArrayList<Vehicle>();
	
	/**
	 * A map of sample vehicles, by ID.
	 */
	public static Map<String, Vehicle> VEHICLE_MAP = new HashMap<String, VehicleContent.Vehicle>();

	static {
		addVehicle(new Vehicle("NORMAL_CAR", "Car", R.drawable.car_inverted));
		addVehicle(new Vehicle("SUV", "Jeep", R.drawable.suv_inverted));
		addVehicle(new Vehicle("TRUCK", "Pickup", R.drawable.truck_inverted));
		addVehicle(new Vehicle("BICYCLE", "Bicycle", R.drawable.bicycle_inverted));
		addVehicle(new Vehicle("SPORTS_CAR", "Sports car", R.drawable.sportscar_inverted));
		addVehicle(new Vehicle("DIRT_BIKE", "Dirt bike", R.drawable.dirtbike_inverted));
		addVehicle(new Vehicle("SPORTS_BIKE", "Sports bike", R.drawable.sportsbike_inverted));
	}
	
	private static void addVehicle(Vehicle vehicle) {
		VEHICLES.add(vehicle);
		VEHICLE_MAP.put(vehicle.id, vehicle);
	}
	
	public static class Vehicle {
		public String id;
		public String name;
		public int image;
		
		public Vehicle(String id, String name, int imageResource) {
			this.id = id;
			this.name = name;
			this.image = imageResource;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
