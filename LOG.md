# Spring Boot Logging — Complete Guide for AuthService

Spring Boot uses **SLF4J** (Simple Logging Facade) with **Logback** as the default backend.
No extra dependency needed — it comes bundled with `spring-boot-starter`.

---

## Step 1 — Add the Logger to Your Class

The cleanest way (since you're already using Lombok) is the `@Slf4j` annotation:

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j                          // ← Lombok generates: private static final Logger log = ...
@Service
@RequiredArgsConstructor
public class AuthServiceImplements implements AuthService {
    // Now you can use `log` anywhere inside this class
}
```

If you are **not** using Lombok, declare it manually:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServiceImplements implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImplements.class);
}
```

---

## Step 2 — The 5 Log Levels (in order of severity)

```
TRACE → DEBUG → INFO → WARN → ERROR
```

| Level   | When to use                                              |
|---------|----------------------------------------------------------|
| `TRACE` | Ultra-fine details (loop iterations, internal state)     |
| `DEBUG` | Developer-facing info (method entry/exit, variable values)|
| `INFO`  | Business events (login success, token created)           |
| `WARN`  | Recoverable problems (wrong password, token expired)     |
| `ERROR` | Actual failures (exception caught, DB error)             |

---

## Step 3 — Configure Log Level in `application.properties`

```properties
# Set level for your whole package
logging.level.com.carPooling.backend=DEBUG

# Set level for a specific class
logging.level.com.carPooling.backend.service.impl.AuthServiceImplements=TRACE

# Spring Security logs (very useful for auth debugging)
logging.level.org.springframework.security=DEBUG

# Log to a file as well (optional)
logging.file.name=logs/app.log
```

> In **dev**, use `DEBUG`. In **production**, use `INFO` or `WARN`.

---

## Step 4 — AuthService with Logging Added

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImplements implements AuthService {

    // -------------------------------------------------------------------------
    // createPassword
    // -------------------------------------------------------------------------
    @Override
    public GenricDTO<CreatePasswordResponse> createPassword(CreatePasswordRequest request) {
        log.debug("createPassword() called for email: {}", request.getEmail());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Password mismatch for email: {}", request.getEmail());
            return new GenricDTO<>(StringConstant.INVALID_REQUEST,
                    "Password and confirm password do not match", null);
        }

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            log.info("No existing user found, creating new user for email: {}", request.getEmail());
        } else {
            log.info("Existing user found, updating password for email: {}", request.getEmail());
        }

        User user = optionalUser.orElse(new User());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        refreshTokenRepository.deleteByUser(user);

        String accessToken       = jwtUtil.generateAccessToken(user.getEmail());
        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .user(user)
                .build();
        refreshTokenRepository.save(refreshToken);

        CreatePasswordResponse response = new CreatePasswordResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenValue);

        log.info("Password created/updated successfully for email: {}", request.getEmail());
        return new GenricDTO<>(StringConstant.SUCCESS, "Password created successfully", response);
    }

    // -------------------------------------------------------------------------
    // login
    // -------------------------------------------------------------------------
    @Override
    @Transactional
    public GenricDTO<LogInResponse> login(LoginRequest req) {
        log.debug("login() called for email: {}", req.getEmail());

        Optional<User> optionalUser = userRepository.findByEmail(req.getEmail());
        if (optionalUser.isEmpty()) {
            log.warn("Login attempt for unregistered email: {}", req.getEmail());
            return new GenricDTO<>(StringConstant.UNAUTHORIZED,
                    "You haven't registered yet, please register first", null);
        }

        User user = optionalUser.get();
        boolean isPasswordCorrect = passwordEncoder.matches(req.getPassword(), user.getPassword());

        if (!isPasswordCorrect) {
            log.warn("Incorrect password attempt for email: {}", req.getEmail());
            return new GenricDTO<>(StringConstant.INVALID_REQUEST, "Password is incorrect", null);
        }

        refreshTokenRepository.deleteByUser(user);

        String accessToken       = jwtUtil.generateAccessToken(user.getEmail());
        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .user(user)
                .build();
        refreshTokenRepository.save(refreshToken);

        LogInResponse response = new LogInResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenValue);
        response.setUser(user);

        log.info("Login successful for email: {}", req.getEmail());
        return new GenricDTO<>(StringConstant.SUCCESS, "Login successful", response);
    }

    // -------------------------------------------------------------------------
    // refreshToken
    // -------------------------------------------------------------------------
    @Override
    @Transactional
    public GenricDTO<RefreshTokenResponse> refreshToken(RefreshTokenRequest request) {
        log.debug("refreshToken() called");

        GenricDTO<RefreshTokenResponse> unauthorizedResponse = new GenricDTO<>(
                StringConstant.UNAUTHORIZED, "Unauthorized Access - try to login/register first", null);

        Optional<RefreshToken> optionalRefreshToken =
                refreshTokenRepository.findByToken(request.getRefreshToken());

        if (optionalRefreshToken.isEmpty()) {
            log.warn("Refresh token not found in DB");
            return unauthorizedResponse;
        }

        RefreshToken storedToken = optionalRefreshToken.get();

        if (storedToken.isRevoked()) {
            log.warn("Refresh token is revoked — token id: {}", storedToken.getId());
            return unauthorizedResponse;
        }

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token expired at: {} — token id: {}",
                    storedToken.getExpiryDate(), storedToken.getId());
            return unauthorizedResponse;
        }

        String email = jwtUtil.extractEmail(request.getRefreshToken());
        log.debug("Email extracted from refresh token: {}", email);

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("No user found for email extracted from token: {}", email);
            return unauthorizedResponse;
        }

        User user = optionalUser.get();

        if (!jwtUtil.isTokenValid(request.getRefreshToken(), user.getEmail())) {
            log.warn("JWT validation failed for email: {}", email);
            return unauthorizedResponse;
        }

        String newAccessToken    = jwtUtil.generateAccessToken(user.getEmail());
        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getEmail());

        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .user(user)
                .build();
        refreshTokenRepository.save(refreshToken);

        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(refreshTokenValue);

        log.info("Token refreshed successfully for email: {}", email);
        return new GenricDTO<>(StringConstant.SUCCESS, "Token refreshed successfully", response);
    }

    // -------------------------------------------------------------------------
    // logout
    // -------------------------------------------------------------------------
    @Override
    @Transactional
    public GenricDTO<Void> logout(LogoutRequest request) {
        log.debug("logout() called");
        try {
            Optional<RefreshToken> optionalToken =
                    refreshTokenRepository.findByToken(request.getRefreshToken());

            if (optionalToken.isEmpty()) {
                log.warn("Logout failed — refresh token not found in DB");
                return new GenricDTO<>(StringConstant.UNAUTHORIZED,
                        "Unauthorized Access - try to login/register first", null);
            }

            RefreshToken refreshToken = optionalToken.get();
            refreshTokenRepository.deleteById(refreshToken.getId());

            log.info("Logout successful — token deleted, id: {}", refreshToken.getId());
            return new GenricDTO<>(StringConstant.SUCCESS, "Logout successful", null);

        } catch (Exception e) {
            log.error("Exception during logout: {}", e.getMessage(), e); // 'e' prints full stack trace
            return new GenricDTO<>(StringConstant.FAILED,
                    "Something went wrong during logout, please try again", null);
        }
    }

    // -------------------------------------------------------------------------
    // updateProfile
    // -------------------------------------------------------------------------
    @Override
    @Transactional
    public GenricDTO<UpdateProfileResponse> updateProfile(UpdateProfileRequest req) {
        Authentication auth  = SecurityContextHolder.getContext().getAuthentication();
        String email         = auth.getName();
        log.debug("updateProfile() called for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found in DB for email: {}", email);
                    return new RuntimeException("User not found");
                });

        user.setGender(req.getGender());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setEmergencyContactName(req.getEmergencyContactName());
        user.setEmergencyContactNumber(req.getEmergencyContactNumber());
        log.debug("Profile fields set — gender: {}, phone: {}", req.getGender(), req.getPhoneNumber());

        userRepository.save(user);

        log.info("Profile updated successfully for email: {}", email);
        return new GenricDTO<>(StringConstant.SUCCESS, "Profile updated successfully", new UpdateProfileResponse());
    }
}
```

---

## Step 5 — The Golden Rule: Never Log Sensitive Data

```java
// ❌ BAD — never log passwords, raw tokens, or card numbers
log.debug("Password entered: {}", req.getPassword());
log.debug("Access token: {}", accessToken);

// ✅ GOOD — log only identifiers and status
log.debug("Login attempt for email: {}", req.getEmail());
log.info("Token generated successfully for user: {}", email);
```

---

## Step 6 — Reading the Console Output

When you run the app, logs look like this:

```
2025-05-24 10:30:01 DEBUG c.c.b.s.AuthServiceImplements - login() called for email: test@gmail.com
2025-05-24 10:30:01 WARN  c.c.b.s.AuthServiceImplements - Incorrect password attempt for email: test@gmail.com
2025-05-24 10:30:05 INFO  c.c.b.s.AuthServiceImplements - Login successful for email: test@gmail.com
2025-05-24 10:30:10 ERROR c.c.b.s.AuthServiceImplements - Exception during logout: Token not found
    at com.carPooling.backend.service.impl.AuthServiceImplements.logout(AuthServiceImplements.java:142)
    at com.carPooling.backend.service.impl.AuthServiceImplements.logout(AuthServiceImplements.java:98)
    ...
```

The `e` passed as the **third argument** to `log.error()` is what prints the full stack trace — always include it when logging inside a catch block.

---

## Quick Reference Cheat Sheet

| What                        | Method                                   |
|-----------------------------|------------------------------------------|
| Add logger (Lombok)         | `@Slf4j` on the class                    |
| Add logger (manual)         | `LoggerFactory.getLogger(MyClass.class)` |
| Set log level               | `logging.level.your.package=DEBUG`       |
| Method entry / exit         | `log.debug(...)`                         |
| Business success events     | `log.info(...)`                          |
| Bad input / auth failures   | `log.warn(...)`                          |
| Caught exceptions           | `log.error("msg: {}", e.getMessage(), e)`|
| **NEVER log**               | Passwords, raw tokens, card numbers      |