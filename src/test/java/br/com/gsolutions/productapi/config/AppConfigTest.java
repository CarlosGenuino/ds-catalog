package br.com.gsolutions.productapi.config;

import br.com.gsolutions.productapi.entities.User;
import br.com.gsolutions.productapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppConfigTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppConfig appConfig;

    @Test
    public void whenPasswordEncoderBean_thenReturnBCryptPasswordEncoder() {
        // When
        BCryptPasswordEncoder passwordEncoder = appConfig.passwordEncoder();

        // Then
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder.matches("password", passwordEncoder.encode("password")));
    }

    @Test
    public void whenUserDetailsServiceBean_thenReturnUserDetailsService() {
        // Given
        String email = "user@example.com";
        User userDetails = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userDetails));

        // When
        UserDetailsService userDetailsService = appConfig.userDetailsService();
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetailsService);
        assertEquals(userDetails, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    public void whenUserDetailsServiceBeanWithInvalidUser_thenThrowUsernameNotFoundException() {
        // Given
        String email = "invalid@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When / Then
        UserDetailsService userDetailsService = appConfig.userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });
        verify(userRepository).findByEmail(email);
    }

    @Test
    public void whenAuthenticationProviderBean_thenReturnDaoAuthenticationProvider() {
        // When
        AuthenticationProvider authenticationProvider = appConfig.authenticationProvider();

        // Then
        assertNotNull(authenticationProvider);
        assertTrue(authenticationProvider instanceof DaoAuthenticationProvider);
    }
}