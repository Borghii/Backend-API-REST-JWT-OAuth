package project.api.rest.service;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @Captor
    private ArgumentCaptor<JwtEncoderParameters> parametersCaptor;

    @InjectMocks
    private TokenService tokenService;


    @Test
    public void testGenerateToken() {
        // Given
        String username = "testUser";
        List<SimpleGrantedAuthority> mockAuthorities = new ArrayList<>();
        mockAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        mockAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenReturn((Collection) mockAuthorities);

        when(jwt.getTokenValue()).thenReturn("test-token-value");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // When
        String token = tokenService.generateToken(authentication);

        // Then
        assertNotNull(token);
        assertEquals("test-token-value", token);

        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }


    @Test
    public void testGenerateTokenWithCorrectClaims() {
        // Given
        String username = "testUser";

        List<SimpleGrantedAuthority> mockAuthorities = new ArrayList<>();
        mockAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenReturn((Collection) mockAuthorities);

        when(jwt.getTokenValue()).thenReturn("test-token-value");
        when(jwtEncoder.encode(parametersCaptor.capture())).thenReturn(jwt);

        // when
        String token = tokenService.generateToken(authentication);

        // then
        assertThat(token).isNotNull();

        JwtClaimsSet claims = parametersCaptor.getValue().getClaims();

        assertThat((String) claims.getClaim("iss")).isEqualTo("self");
        assertThat((String) claims.getClaim("scope")).isEqualTo("ROLE_USER");
        assertThat(claims.getSubject()).isEqualTo(username);


        Instant issuedAt = claims.getIssuedAt();
        Instant expiresAt = claims.getExpiresAt();

        assertThat(issuedAt).isNotNull();
        assertThat(expiresAt).isNotNull();

        long diffSeconds = expiresAt.getEpochSecond() - issuedAt.getEpochSecond();
        assertThat(diffSeconds).isCloseTo(3600, within(10L));

    }

    @Test
    public void testGenerateTokenWithMultipleAuthorities() {
        // given
        String username = "multiRoleUser";

        List<SimpleGrantedAuthority> mockAuthorities = new ArrayList<>();
        mockAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        mockAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        mockAuthorities.add(new SimpleGrantedAuthority("SCOPE_READ"));

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenReturn((Collection) mockAuthorities);

        when(jwt.getTokenValue()).thenReturn("multi-role-token");
        when(jwtEncoder.encode(parametersCaptor.capture())).thenReturn(jwt);

        // when
        String token = tokenService.generateToken(authentication);

        JwtClaimsSet jwtClaimsSet = parametersCaptor.getValue().getClaims();

        // then

        assertThat((String) jwtClaimsSet.getClaim("scope"))
                .contains("ROLE_USER","ROLE_ADMIN","SCOPE_READ");

    }
}