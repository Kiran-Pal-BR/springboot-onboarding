package com.example.onboarding.securityTesting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Bypasses the need for a live Keycloak server by mocking the decoder
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void publicEndpoint_ShouldBeAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/public/welcome"))
                .andExpect(status().isOk());
    }

    @Test
    void secureEndpoint_ShouldBeUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/tasks/secure"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_ShouldBeAccessibleWithAdminRoleJwt() throws Exception {
        // Build a mock JWT that matches the structure expected by your JwtAuthenticationConverter
        Jwt mockJwt = Jwt.withTokenValue("mock-jwt-token")
                .header("alg", "none")
                .claim("realm_access", Map.of("roles", List.of("ADMIN")))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();


        when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer mock-jwt-token"))
                .andExpect(status().isOk());
    }
}