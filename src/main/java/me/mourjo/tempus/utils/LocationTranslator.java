package me.mourjo.tempus.utils;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static me.mourjo.tempus.utils.StringUtils.cleanse;

public class LocationTranslator {
    private static final String WORLD_CITIES_CSV = "simplemaps_worldcities_basicv1.75/worldcities.csv.gz";
    private static LocationTranslator instance;
    private Map<String, Map<String, List<Location>>> countryCityLocation;
    private List<String> countryNames;

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

    public static List<Location> getLatLong(String country, String city) {
        prepareData();
        country = cleanse(country);
        city = cleanse(city);
        if (!instance.countryCityLocation.containsKey(country)
                || !instance.countryCityLocation.get(country).containsKey(city)) {
            return List.of();
        }

        var candidates = instance.countryCityLocation.get(country);
        var pattern = StringUtils.globToRegex(city);
        List<Location> locations = new ArrayList<>();

        for (String cityInCountry : candidates.keySet()) {
            if(pattern.matcher(cityInCountry).matches()){
                locations.addAll(instance.countryCityLocation.get(country).get(cityInCountry));
            }
        }

        return locations.stream().distinct().collect(Collectors.toList());
    }

    public static List<String> getAllCountries() {
        prepareData();
        return instance.countryNames;
    }

    private void loadFile() throws IOException, CsvException {
        if (countryNames != null) {
            return;
        }

        List<String> countries = new ArrayList<>();
        Map<String, Map<String, List<Location>>> locations = new HashMap<>();

        boolean first = true;
        for (String[] line : FileUtils.readCSVFile(WORLD_CITIES_CSV)) {
            if (!first && line.length >= 4) {
                String city = cleanse(line[1]);
                String stateOrCapital = cleanse(line[7]); // some rows like New York or New Jersey are not stored as cities
                double lat = Double.parseDouble(line[2]);
                double lng = Double.parseDouble(line[3]);
                String country = cleanse(line[4]);

                if (null == locations.putIfAbsent(country, new HashMap<>())) {
                    countries.add(line[4]);
                }

                locations.get(country).putIfAbsent(city, new ArrayList<>());
                locations.get(country).putIfAbsent(stateOrCapital, new ArrayList<>());

                locations.get(country).get(city).add(Location.of(lat, lng));
                locations.get(country).get(stateOrCapital).add(Location.of(lat, lng));
            }
            first = false;
        }

        Collections.sort(countries);
        instance.countryNames = Collections.unmodifiableList(countries);
        instance.countryCityLocation = Collections.unmodifiableMap(locations);
    }
}
