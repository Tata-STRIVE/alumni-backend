package com.striveconnect.service;

import com.striveconnect.entity.User;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new UsernameNotFoundException("Tenant context not set for user: " + mobileNumber);
        }

        User user = userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for tenant " + tenantId + " with mobile number: " + mobileNumber));

        // *** THE FIX IS HERE ***
        // Create the authority with the standard "ROLE_" prefix.
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // The password field is empty as we use OTP, not password-based auth.
        return new org.springframework.security.core.userdetails.User(user.getMobileNumber(), "", authorities);
    }
}

