package com.dal.skillswap.service.impl;

import com.dal.skillswap.models.util.Coordinate;
import com.dal.skillswap.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LocationServiceImplTests {

    @Autowired
    private LocationService locationService;

    @Test
    void testGetUserDetails() {
        Coordinate coordinate = new Coordinate(44.629021, -63.5714195);
        assertEquals(locationService.convertLocationToCoordinates("B3H1B9"), coordinate);
    }

    @Test
    void testCalculateDistance() {
        Coordinate coordinate = locationService.convertLocationToCoordinates("B3H1B9");
        Coordinate otherCoordinate = locationService.convertLocationToCoordinates("B3J2K9");
        assertEquals(coordinate.calculateDistance(otherCoordinate), 1395.9179316049747);
    }
}
