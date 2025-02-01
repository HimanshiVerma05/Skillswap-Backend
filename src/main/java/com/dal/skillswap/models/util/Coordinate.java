package com.dal.skillswap.models.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Coordinate {
    private Double latitude;
    private Double longitude;
    // Radius of the Earth in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double KILOMETERS_TO_METERS = 1000.0;

    /**
     * Calculates distance between two coordinates
     *
     * @param destinationCoordinate Other coordinate to compare with
     * @return distance between two coordinates
     * <p>
     * This code is taken from below source and changed according to the requirement
     * - Author(s) name: David George
     * - Date: May 28, 2013
     * - Title of source code: Calculating distance between two points, using latitude longitude
     * - Type: Source code
     * - Web address: https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
     */
    public double calculateDistance(Coordinate destinationCoordinate) {

        double deltaLatitude = Math.toRadians(this.getLatitude() - destinationCoordinate.getLatitude());
        double deltaLongitude = Math.toRadians(this.getLongitude() - destinationCoordinate.getLongitude());

        double sinSquaredHalfDeltaLat = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2);
        double sourceLatitudeCos = Math.cos(Math.toRadians(this.getLatitude()));
        double destinationLatitudeCos = Math.cos(Math.toRadians(destinationCoordinate.getLatitude()));
        double cosProductLatitudes = sourceLatitudeCos * destinationLatitudeCos;
        double sinSquaredHalfDeltaLon = Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2);

        double a = sinSquaredHalfDeltaLat
                + cosProductLatitudes
                * sinSquaredHalfDeltaLon;

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c * KILOMETERS_TO_METERS;
    }
}
