package me.mourjo.tempus.utils;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.*;

import static me.mourjo.tempus.utils.StringUtils.cleanse;

public class LocationTranslator {
    private static final String WORLD_CITIES_CSV = "simplemaps_worldcities_basicv1.75/worldcities.csv";
    private static LocationTranslator instance;
    private Map<String, Map<String, Location>> countryCityLocation;
    private List<String> countryNames;

    LocationTranslator() throws IOException, CsvException {
        countryCityLocation = new HashMap<>();
        countryNames = new ArrayList<>();
    }

    private static void prepareData() {
        try {
            if (instance == null) {
                instance = new LocationTranslator();
                instance.loadFile();
            }

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

    public static List<String> getAllCountries() {
        prepareData();
        return instance.countryNames;
    }

    private void loadFile() throws IOException, CsvException {
        boolean first = true;
        for (String[] line : FileUtils.readCSVFile(WORLD_CITIES_CSV)) {
            if (!first && line.length >= 4) {
                String city = cleanse(line[1]);
                String stateOrCapital = cleanse(line[7]); // some rows like New York or New Jersey are not stored as cities
                double lat = Double.parseDouble(line[2]);
                double lng = Double.parseDouble(line[3]);
                String country = cleanse(line[4]);

                if (null == instance.countryCityLocation.putIfAbsent(country, new HashMap<>())) {
                    instance.countryNames.add(line[4]);
                }

                instance.countryCityLocation.get(country).put(city, Location.of(lat, lng));
                instance.countryCityLocation.get(country).put(stateOrCapital, Location.of(lat, lng));
            }
            first = false;
        }
        Collections.sort(instance.countryNames);
        instance.countryNames = Collections.unmodifiableList(instance.countryNames);
    }
}
