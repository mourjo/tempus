package me.mourjo.tempus.utils;

import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.*;

import static me.mourjo.tempus.utils.StringUtils.cleanse;

public class LocationTranslator {
    private static LocationTranslator instance;
    private static final String WORLD_CITIES_CSV = "simplemaps_worldcities_basicv1.75/worldcities.csv";
    Map<String, Map<String, Location>> countryCityLocation;
     LocationTranslator() throws IOException, CsvException {
        countryCityLocation = new HashMap<>();
    }

    private void loadFile() throws IOException, CsvException {
        boolean first = true;
        for (String[] line : FileUtils.readCSVFile(WORLD_CITIES_CSV)) {
            if (!first && line.length >= 4) {
                String city = cleanse(line[1]);
                double lat = Double.parseDouble(line[2]);
                double lng = Double.parseDouble(line[3]);
                String country = cleanse(line[4]);
                instance.countryCityLocation.putIfAbsent(country, new HashMap<>());
                instance.countryCityLocation.get(country).put(city, Location.of(lat, lng));
            }
            first = false;
        }
    }

    private static void prepareData() {
        try {
            if (instance == null) {
                instance = new LocationTranslator();
            }
            instance.loadFile();

        } catch (CsvException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Optional<Location> getLatLong(String country, String city) {
        prepareData();
        country = cleanse(country);
        city = cleanse(city);
        if (!instance.countryCityLocation.containsKey(country)
                || !instance.countryCityLocation.get(country).containsKey(city)) {
            return Optional.empty();
        }

        return Optional.of(instance.countryCityLocation.get(country).get(city));
    }
}


class Location {
    final double latitude;
    final double longitude;

    private Location(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }

    public static Location of(double lat, double lng) {
        return new Location(lat, lng);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (Double.compare(location.latitude, latitude) != 0) return false;
        return Double.compare(location.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
                latitude +
                "," + longitude +
                '}';
    }
}