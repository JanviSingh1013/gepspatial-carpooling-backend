```
curl --location 'http://localhost:8080/api/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "fullName": "Janvi Singh",
  "email": "Janvi@gmail.com",
  "phoneNumber": "6392199459",
  "password": "Google@2026",
  "gender": "FEMALE",
  "role": "PASSENGER"
}'


# ------------------------------------------------------------\n\n


curl --location 'http://localhost:8089/api/auth/password' \
--header 'Content-Type: application/json' \
--data '{
    "email": "janvi@gmail.com",
    "password": "Google@2026",
    "confirm_password": "Google@2026"
}'

```




# Car Pooling Backend Authentication API

## Base URL

```text
http://localhost:8089/api/auth
```

---

# Authentication Flow

```text
Register/Login
      ↓
Receive Access Token + Refresh Token
      ↓
Use Access Token for Protected APIs
      ↓
Access Token Expires
      ↓
Call Refresh API using Refresh Token
      ↓
Receive New Access Token
      ↓
Continue Without Login
```

---

# Token Expiry

| Token Type    | Expiry     |
| ------------- | ---------- |
| Access Token  | 15 Minutes |
| Refresh Token | 7 Days     |

---

# 1. Register API

## Endpoint

```http
POST /api/auth/register
```

## Request Body

```json
{
    "fullName": "Janvi Singh",
    "email": "janvi@gmail.com",
    "phoneNumber": "9876543210",
    "password": "Google@2026",
    "gender": "FEMALE",
    "role": "USER",
    "profilePicture": "https://example.com/profile.jpg"
}
```

## cURL

```bash
curl --location 'http://localhost:8089/api/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "fullName": "Janvi Singh",
    "email": "janvi@gmail.com",
    "phoneNumber": "9876543210",
    "password": "Google@2026",
    "gender": "FEMALE",
    "role": "USER",
    "profilePicture": "https://example.com/profile.jpg"
}'
```

## Success Response

```json
{
    "accessToken": "jwt-access-token",
    "refreshToken": "jwt-refresh-token",
    "tokenType": "Bearer",
    "email": "janvi@gmail.com",
    "fullName": "Janvi Singh",
    "roleType": "USER",
    "message": "Registration successful!"
}
```

---

# 2. Login API

## Endpoint

```http
POST /api/auth/login
```

## Request Body

```json
{
    "email": "janvi@gmail.com",
    "password": "Google@2026"
}
```

## cURL

```bash
curl --location 'http://localhost:8089/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "janvi@gmail.com",
    "password": "Google@2026"
}'
```

## Success Response

```json
{
    "accessToken": "jwt-access-token",
    "refreshToken": "jwt-refresh-token",
    "tokenType": "Bearer",
    "email": "janvi@gmail.com",
    "fullName": "Janvi Singh",
    "roleType": "USER",
    "message": "Login successful!"
}
```

---

# 3. Refresh Token API

## Endpoint

```http
POST /api/auth/refresh
```

## Request Body

```json
{
    "refreshToken": "your-refresh-token"
}
```

## cURL

```bash
curl --location 'http://localhost:8089/api/auth/refresh' \
--header 'Content-Type: application/json' \
--data-raw '{
    "refreshToken": "your-refresh-token"
}'
```

## Success Response

```json
{
    "accessToken": "new-access-token",
    "refreshToken": "your-refresh-token",
    "tokenType": "Bearer",
    "email": "janvi@gmail.com",
    "fullName": "Janvi Singh",
    "roleType": "USER",
    "message": "Token refreshed successfully"
}
```

---

# 4. Logout API

## Endpoint

```http
POST /api/auth/logout
```

## Request Body

```json
{
    "refreshToken": "your-refresh-token"
}
```

## cURL

```bash
curl --location 'http://localhost:8089/api/auth/logout' \
--header 'Content-Type: application/json' \
--data-raw '{
    "refreshToken": "your-refresh-token"
}'
```

## Success Response

```json
{
    "message": "Logout successful"
}
```

---

# 5. Protected API Example

Use the access token in Authorization header.

## Example Header

```http
Authorization: Bearer your-access-token
```

## cURL

```bash
curl --location 'http://localhost:8089/api/your-protected-api' \
--header 'Authorization: Bearer your-access-token'
```

---

# Validation Rules

## Register API Validation

| Field       | Validation                                                 |
| ----------- | ---------------------------------------------------------- |
| fullName    | Required, 2-100 chars                                      |
| email       | Valid email required                                       |
| phoneNumber | Valid Indian 10-digit number                               |
| password    | Minimum 8 chars, uppercase, lowercase, digit, special char |
| gender      | Required                                                   |
| role        | Required                                                   |

---

# Notes

* Access Token is used for secured APIs
* Refresh Token is used to generate new access tokens
* Refresh Tokens are stored in database
* Logout revokes refresh token
* Use `Authorization: Bearer <access_token>` for secured APIs
* Access Token is short-lived
* Refresh Token keeps user logged in without repeated login
