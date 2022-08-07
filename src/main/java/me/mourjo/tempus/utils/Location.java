package me.mourjo.tempus.utils;

public class Location implements Comparable<Location> {
    public final double latitude;
    public final double longitude;

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

    @Override
    public int compareTo(Location o) {
        if (o == this){
            return 0;
        }

        if (o.latitude == this.latitude) {
            return Double.compare(this.longitude, o.longitude);
        }

        return Double.compare(this.latitude, o.latitude);
    }
}
