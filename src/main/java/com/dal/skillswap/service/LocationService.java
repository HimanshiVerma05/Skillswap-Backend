package com.dal.skillswap.service;

import com.dal.skillswap.models.util.Coordinate;

public interface LocationService {
    Coordinate convertLocationToCoordinates(String location);
}
