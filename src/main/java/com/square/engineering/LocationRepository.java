package com.square.engineering;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class LocationRepository {
	
	private static final Logger LOG = Logger.getLogger(LocationRepository.class);
	
	private static final String FILE = "locations.properties";
	
	Map<String, Location> map = Collections.synchronizedMap(new HashMap<String, Location>());

	private static LocationRepository instance;
	
	private LocationRepository(){
		loadLocations();
	}
	
	public static LocationRepository get(){
		if(instance == null){
			instance = new LocationRepository();
		}
		return instance;
	}
	
	private void loadLocations(){
		try {
			InputStream is = LocationRepository.class.getResourceAsStream(FILE);
			Properties properties = new Properties();
			properties.load(is);
			
			String[] locations = properties.getProperty("locations").split(",");
			for(String location : locations){
				loadLocation(location, properties);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void loadLocation(String location, Properties properties){
		String json = properties.getProperty("location." + location);
		Gson gson = new Gson();
		Location loc = gson.fromJson(json, Location.class);
		map.put(loc.getId(), loc);
		LOG.info("Loaded from properties " + loc.toString());
	}
	
	public Location get(String locationId){
		Location location = map.get(locationId);
		if(location == null){
			// throw 
		}
		return location;
	}
	
	public void update(Location location){
		map.put(location.getId(), location);
	}
	
	public void delete(String locationId){
		map.remove(locationId);
	}
	
}
