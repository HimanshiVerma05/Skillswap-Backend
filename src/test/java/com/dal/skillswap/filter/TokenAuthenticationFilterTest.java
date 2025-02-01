package com.dal.skillswap.filter;

import com.dal.skillswap.utils.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class TokenAuthenticationFilterTest {

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDoFilterInternal_WithValidToken() throws IOException, ServletException {
        Claims claims = mock(Claims.class);
        when(jwtUtils.getToken(request)).thenReturn("validToken");
        when(jwtUtils.getClaims(request)).thenReturn(claims);
        when(jwtUtils.validateClaims(claims)).thenReturn(true);
        when(claims.getSubject()).thenReturn("test@example.com");

        tokenAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(0)).setAttribute(eq("email"), eq("test@example.com"));
        assertEquals(1, mockingDetails(filterChain).getInvocations().size());

    }

    @Test
    public void testDoFilterInternal_WithInvalidToken() throws IOException, ServletException {
        when(jwtUtils.getToken(request)).thenReturn(null);

        tokenAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(1, mockingDetails(filterChain).getInvocations().size());
    }

    @Test
    public void testDoFilterInternal_WithException() throws IOException, ServletException {
        when(jwtUtils.getToken(request)).thenThrow(new RuntimeException("Token error"));

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        tokenAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
        verify(response, times(1)).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(mapper, times(1)).writeValue(eq(writer), anyMap());

        assertNull(response.getContentType());
    }
}
