package com.striveconnect.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    // Switched to a standard, non-loading Cache for simplicity and robustness.
    private final Cache<String, String> otpCache;

    public OtpService() {
        otpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Generates a 6-digit OTP and stores it in the cache.
     */
    public String generateOtp(String key) {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(999999));
        logger.debug("Storing OTP '{}' for key '{}' in cache.", otp, key);
        otpCache.put(key, otp);
        return otp;
    }

    /**
     * Validates the provided OTP against the one in the cache.
     */
    public boolean validateOtp(String key, String otp) {
        logger.debug("Attempting to validate OTP '{}' for key '{}'.", otp, key);

        // Use getIfPresent which returns null if the key is not found or has expired.
        // This is a simpler and more direct way to check the cache.
        String cachedOtp = otpCache.getIfPresent(key);

        if (cachedOtp != null && cachedOtp.equals(otp)) {
            logger.debug("OTP validation successful for key '{}'. Invalidating cache entry.", key);
            otpCache.invalidate(key); // OTP is single-use
            return true;
        }
        
        logger.warn("OTP validation failed for key '{}'. Provided OTP: '{}', Cached OTP: '{}'. The cached OTP might have been null if it expired.", key, otp, cachedOtp);
        return false;
    }
}

