Organized `README.md` formatting without changing content. The file content is preserved; only layout and Markdown structure were improved.

```markdown
# Car Pooling Backend Authentication API

## Sample cURL Requests

```bash
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
```

---

```bash
curl --location 'http://localhost:8089/api/auth/password' \
--header 'Content-Type: application/json' \
--data '{
    "email": "janvi@gmail.com",
    "password": "Google@2026",
    "confirm_password": "Google@2026"
}'
```

---

# Login API Documentation

## Base URL

```text
http://localhost:8080/api/auth
```

---

## Login API

**Endpoint**

```http
POST /login
```

---

### Request Headers

| Key | Value |
| --- | ----- |
| Content-Type | application/json |

---

### Request Body

```json
{
  "email": "john@gmail.com",
  "password": "Google@2026"
}
```

---

### Validation Rules

#### Email

- Required
- Must be valid email format

Example:

```text
john@gmail.com
```

#### Password

Password must contain:

- Minimum 8 characters
- 1 uppercase letter
- 1 lowercase letter
- 1 number
- 1 special character

Example:

```text
Google@2026
```

---

### Success Response

**HTTP Status**

```http
202 Accepted
```

**Response Body**

```json
{
  "status": "SUCCESS",
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@gmail.com",
      "phoneNumber": "9876543210",
      "gender": "MALE",
      "role": "USER",
      "profilePicture": null,
      "createdAt": "2026-05-21T01:10:20",
      "updatedAt": "2026-05-21T01:10:20",
      "deletedAt": null
    }
  }
}
```

---

### User Not Registered

**HTTP Status**

```http
401 Unauthorized
```

**Response Body**

```json
{
  "status": "UNAUTHORIZED",
  "message": "You haven't registered yet, please register first",
  "data": null
}
```

---

### Incorrect Password

**HTTP Status**

```http
400 Bad Request
```

**Response Body**

```json
{
  "status": "INVALID_REQUEST",
  "message": "Password is incorrect",
  "data": null
}
```

---

### Validation Error Response

**HTTP Status**

```http
400 Bad Request
```

**Response Body**

```json
{
  "status": "INVALID_REQUEST",
  "message": "Validation failed",
  "data": [
    "Invalid email format"
  ]
}
```

---

### Example Invalid Password Format

```json
{
  "status": "INVALID_REQUEST",
  "message": "Validation failed",
  "data": [
    "Password must contain minimum 8 characters, 1 uppercase, 1 lowercase, 1 number and 1 special character"
  ]
}
```

---

### Example Multiple Validation Errors

```json
{
  "status": "INVALID_REQUEST",
  "message": "Validation failed",
  "data": [
    "Email is required",
    "Password is required"
  ]
}
```

---

### Internal Server Error

**HTTP Status**

```http
500 Internal Server Error
```

**Response Body**

```json
{
  "status": "FAILED",
  "message": "Something went wrong",
  "data": null
}
```

---

## Authentication Flow

```text
Client Request
↓
Validate Request Body
↓
Check User Exists
↓
Verify Password Using BCrypt
↓
Generate Access Token
↓
Generate Refresh Token
↓
Save Refresh Token
↓
Return Response
```

---

## Technologies Used

- Spring Boot
- Spring Security
- JWT Authentication
- BCrypt Password Encoder
- JPA/Hibernate
- MySQL

---

## Notes

- Password is stored in encoded format using BCrypt.
- JWT Access Token is used for API authentication.
- Refresh Token is stored in database.
- Old refresh tokens are deleted during login.
- Password field is hidden from API response using `@JsonIgnore`.

---

## Sample cURL Request

```bash
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
"email": "john@gmail.com",
"password": "Google@2026"
}'
```

---

## Car Pooling Backend Authentication API (alternate Base URL)

**Base URL**

```text
http://localhost:8089/api/auth
```

---

## Authentication Flow (summary)

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

## Token Expiry

| Token Type    | Expiry     |
| ------------- | ---------- |
| Access Token  | 15 Minutes |
| Refresh Token | 7 Days     |
```
