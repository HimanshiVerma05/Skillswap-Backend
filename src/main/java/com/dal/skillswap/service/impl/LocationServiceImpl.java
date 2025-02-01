package com.dal.skillswap.service.impl;

import com.dal.skillswap.models.util.Coordinate;
import com.dal.skillswap.service.LocationService;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.dal.skillswap.constants.PathConstants.OPENSTREETMAP_SEARCH_PATH;

@Service
public class LocationServiceImpl implements LocationService {

    @Value("${openstreetmap.api.url}")
    private String openstreetmapApiUrl;

    RestTemplate restTemplate;


    public LocationServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        this.restTemplate.setMessageConverters(messageConverters);
    }

    /**
     * Convert location to coordinates
     * @param location
     * @return
     */
    @Override
    public Coordinate convertLocationToCoordinates(String location) {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(openstreetmapApiUrl);
        builder.setPath(OPENSTREETMAP_SEARCH_PATH);
        builder.addParameter("format", "json");
        builder.addParameter("q", location);

        try {
            ResponseEntity<List> response = restTemplate.getForEntity(builder.build(), List.class);

            if (response.getBody() == null || response.getBody().isEmpty()) {
                throw new RuntimeException("Invalid address");
            }

            HashMap<String, String> locationMap = (HashMap) response.getBody().get(0);
            Double latitude = Double.parseDouble(locationMap.get("lat"));
            Double longitude = Double.parseDouble(locationMap.get("lon"));
            Coordinate coordinate = new Coordinate(latitude, longitude);

            return coordinate;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
