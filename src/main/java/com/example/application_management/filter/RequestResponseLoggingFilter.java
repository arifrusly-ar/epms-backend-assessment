package com.example.application_management.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    // Logger untuk menyimpan request dan response
    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    // Had maksimum body yang akan direkodkan
    private static final int MAX_LOG_BYTES = 4096;

    // Endpoint sensitif yang tidak boleh log body
    private static final Set<String> SENSITIVE_PATHS = Set.of("/api/auth", "/login", "/oauth2");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Wrap request supaya body boleh dibaca selepas diproses
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(
                        request, MAX_LOG_BYTES);

        // Wrap response supaya response body boleh direkodkan
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Rekod masa mula request
        long startTime = System.currentTimeMillis();

        try {
            // Teruskan request ke Spring Security dan Controller
            filterChain.doFilter(wrappedRequest, wrappedResponse);

        } finally {

            // Kira masa execution API
            long duration = System.currentTimeMillis() - startTime;

            // Dapatkan endpoint dan HTTP method
            String uri = request.getRequestURI();
            String method = request.getMethod();

            // Log body hanya untuk kandungan JSON yang tidak sensitif
            boolean safeRequest = isSafeToLog(uri, request.getContentType());

            boolean safeResponse = isSafeToLog(uri, response.getContentType());

            String requestBody = safeRequest
                    ? readBody(wrappedRequest.getContentAsByteArray())
                    : "[REDACTED OR NON-JSON]";

            String responseBody = safeResponse
                    ? readBody(wrappedResponse.getContentAsByteArray())
                    : "[REDACTED OR NON-JSON]";

            // Log request information
            log.info(
                    "REQUEST | Method={} | URI={} | Body={}",
                    method, uri, requestBody
            );

            // Log response information
            log.info(
                    "RESPONSE | Method={} | URI={} | Status={} | Duration={}ms | Body={}",
                    method,
                    uri,
                    wrappedResponse.getStatus(),
                    duration,
                    responseBody
            );

            // Penting: salin semula response kepada client
            wrappedResponse.copyBodyToResponse();
        }
    }

    // Semak sama ada body selamat untuk direkodkan
    private boolean isSafeToLog(String uri, String contentType) {

        String path = uri.toLowerCase(Locale.ROOT);

        // Jangan log body authentication endpoints
        if (SENSITIVE_PATHS.stream().anyMatch(path::startsWith)) {
            return false;
        }

        // Hanya log JSON, bukan binary atau multipart files
        return contentType != null
                && contentType.toLowerCase(Locale.ROOT)
                .contains("application/json");
    }

    // Convert cached bytes kepada String
    private String readBody(byte[] bytes) {

        if (bytes.length == 0) {
            return "[EMPTY]";
        }

        // Hadkan saiz log supaya fail tidak terlalu besar
        int length = Math.min(bytes.length, MAX_LOG_BYTES);

        return new String(
                bytes, 0, length, StandardCharsets.UTF_8
        );
    }
}
