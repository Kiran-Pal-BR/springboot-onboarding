# Day 24: Enterprise Spring Security - OAuth2 Resource Server & JWT Architecture

## 1. Overview

As part of the enterprise onboarding roadmap, Day 24 transitions the application from stateful, database-backed HTTP Basic Authentication (Day 23) to a stateless, token-based security architecture using OAuth2 Resource Servers and JSON Web Tokens (JWT).

In this architecture, Spring Boot acts purely as a **Resource Server** that validates cryptographically signed tokens issued by an external Identity Provider (IdP) such as Keycloak, Auth0, or Okta, eliminating the need to query a database for authentication on every request.

---

## 2. End-to-End Authentication & Validation Flow

```
+--------+       1. Login (Credentials)      +--------------------+
| Client | --------------------------------> | Identity Provider  |
| (App)  |                                   | (e.g., Keycloak)   |
|        | <-------------------------------- |                    |
+--------+       2. Returns Signed JWT       +--------------------+
    |                                        ^
    | 3. HTTP Request with Bearer JWT        | 4. Fetch JWKS
    |                                        |    (Public Keys)
    v                                        |
+------------------------------------------+ |
| Spring Boot API (OAuth2 Resource Server) |----+
|  - Validates JWT Signature (via JWKS)    |
|  - Extracts Roles from realm_access      |
|  - Authorizes Request                    |
+------------------------------------------+
```

### Lifecycle Breakdown

**1. Authentication Request**
The client authenticates against the external Identity Provider by providing valid user credentials (username/password).

**2. Token Issuance**
Upon successful verification, the IdP generates a **signed JWT** (JSON Web Token) containing user claims, scopes, and roles, and returns it to the client. The JWT is signed with the IdP's private key.

**3. API Access**
The client makes HTTP requests to the Spring Boot REST API, attaching the token in the request header:

```
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

**4. JWKS Fetch (Signature Validation Setup)**
On startup (or when the signing key rotates), Spring Boot fetches the **JSON Web Key Set (JWKS)** from the IdP's public endpoint (derived from the `issuer-uri`). The JWKS contains the public keys needed to verify the JWT signature cryptographically.

**5. Stateless Validation**
The Spring Boot Resource Server intercepts every incoming request, validates the cryptographic signature of the token against the IdP's public keys, extracts authority claims (roles), and authorizes execution — all without maintaining server-side sessions or querying a database.

---

## 3. Implementation Details

### A. Maven Dependencies

To enable resource server capabilities, the following starter is included in `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

This dependency brings in:
- `spring-security-oauth2-resource-server` — OAuth2 Resource Server support
- `spring-security-oauth2-jose` — JWT parsing and signature validation
- `nimbus-jose-jwt` — The underlying JWT library for decoding and validation

---

### B. Security Configuration (`SecurityConfig.java`)

The security filter chain disables CSRF (since the API is stateless) and configures Spring Security to process incoming bearer tokens with a **custom JWT authentication converter**.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || !realmAccess.containsKey("roles")) {
                return List.of();
            }
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        });
        return converter;
    }
}
```

#### Key Configuration Points

| Configuration | Purpose |
|---|---|
| `csrf().disable()` | Disables CSRF protection — stateless JWT APIs don't use cookies/sessions |
| `/api/public/**` → `permitAll()` | Public endpoints accessible without authentication |
| `/api/admin/**` → `hasRole("ADMIN")` | Admin endpoints require the `ADMIN` role |
| `anyRequest()` → `authenticated()` | All other endpoints require a valid JWT |
| `jwtAuthenticationConverter()` | Custom converter that maps Keycloak roles to Spring Security authorities |

---

### C. Custom JWT Authentication Converter

The **most critical part** of this implementation is the custom `jwtAuthenticationConverter()` bean. It bridges the gap between Keycloak's JWT claim structure and Spring Security's authority model.

#### How It Works

1. **Extract `realm_access` claim** — Keycloak places user roles inside the `realm_access.roles` claim of the JWT payload
2. **Map to `SimpleGrantedAuthority`** — Each role string is prefixed with `ROLE_` and converted to uppercase, matching Spring Security's `hasRole()` convention
3. **Return empty list** — If the claim is missing, the user gets no authorities (authenticated but with no roles)

#### Example JWT Payload (Keycloak)

```json
{
  "exp": 1700000000,
  "sub": "admin-user",
  "realm_access": {
    "roles": ["ADMIN"]
  },
  "preferred_username": "admin-user"
}
```

After processing by the converter, Spring Security sees:
- `SimpleGrantedAuthority("ROLE_ADMIN")`

This allows `.hasRole("ADMIN")` to match, because Spring Security automatically prepends `ROLE_` when evaluating `hasRole()`.

---

### D. Secure Configuration Management

To protect sensitive identity endpoints and signing configurations, the `issuer-uri` is injected securely via environment variables:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=${JWT_ISSUER_URI:http://localhost:8180/realms/my-enterprise-realm}
```

- **`JWT_ISSUER_URI`** — Environment variable that overrides the default
- **Default value** — `http://localhost:8180/realms/my-enterprise-realm` (Keycloak local development)
- Spring Boot derives the `jwk-set-uri` automatically: `{issuer-uri}/protocol/openid-connect/certs`

---

## 4. Endpoint Security Tiers

| Endpoint Pattern | Security Level | Required Role | Description |
|---|---|---|---|
| `/api/public/**` | Open | None | Accessible without any authentication |
| `/api/secure/**` | Authenticated | Any valid JWT | Requires a valid JWT with any role |
| `/api/admin/**` | Restricted | `ADMIN` | Requires a valid JWT with the `ADMIN` role in `realm_access.roles` |

---

## 5. Testing the Flow

### With a Running Keycloak Server

1. Start Keycloak (e.g., via Docker Compose on `localhost:8180`)
2. Obtain a token:

```bash
curl -X POST http://localhost:8180/realms/my-enterprise-realm/protocol/openid-connect/token \
  -d "username=admin-user" \
  -d "password=secret" \
  -d "grant_type=password" \
  -d "client_id=onboarding-api"
```

3. Use the token in API requests:

```bash
curl -H "Authorization: Bearer <access_token>" http://localhost:8080/api/admin/dashboard
```

### With Integration Tests (No Keycloak Required)

The `SecurityIntegrationTest` uses a mocked `JwtDecoder` to bypass signature validation:

- **Public endpoint** → `200 OK` (no token required)
- **Secure endpoint without token** → `401 Unauthorized`
- **Admin endpoint with mock JWT** → `200 OK` (mocked `ADMIN` role)

---

## 6. Architecture Summary

This OAuth2 Resource Server architecture provides:

- **Statelessness** — No server-side sessions; every request is self-contained
- **Scalability** — Any number of API instances can validate tokens independently
- **Decoupling** — Authentication logic is delegated to the IdP (Keycloak)
- **Security** — JWT signatures are validated cryptographically via JWKS
- **Role-Based Access Control** — Keycloak roles map to Spring Security authorities via the custom converter
