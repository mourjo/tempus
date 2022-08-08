package me.mourjo.tempus.models;

import com.opencsv.exceptions.CsvException;
import me.mourjo.tempus.utils.FileUtils;
import me.mourjo.tempus.utils.StringUtils;

import java.io.IOException;
import java.util.*;

import static me.mourjo.tempus.utils.StringUtils.cleanse;

public class LocationTranslator {
    public static final int MAX_LOCATIONS = 15;
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

    /**
     * Given a country and city glob, return matching locations
     *
     * @param country to search for the city in
     * @param cityGlob to match the city in the country
     * @return Set of locations
     */
    public static Set<Location> getLatLong(String country, String cityGlob) {
        prepareData();
        country = cleanse(country);
        cityGlob = cleanse(cityGlob);
        if (!instance.countryCityLocation.containsKey(country)) {
            return Set.of();
        }

        var candidates = instance.countryCityLocation.get(country);
        var pattern = StringUtils.globToRegex(cityGlob);
        Set<Location> locations = new HashSet<>();

        for (String candidateCity : candidates.keySet()) {
            if (pattern.matcher(candidateCity).matches()) {
                for (var location : instance.countryCityLocation.get(country).get(candidateCity)) {
                    if (locations.size() >= MAX_LOCATIONS) {
                        return locations;
                    }
                    locations.add(location);
                }
            }
        }

        return locations;
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

                locations.get(country).get(city).add(Location.of(city, lat, lng));
                locations.get(country).get(stateOrCapital).add(Location.of(stateOrCapital, lat, lng));
            }
            first = false;
        }

        Collections.sort(countries);
        instance.countryNames = Collections.unmodifiableList(countries);
        instance.countryCityLocation = Collections.unmodifiableMap(locations);
    }
}
