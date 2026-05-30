```
Access token expires (15 min)
→ Flutter gets TOKEN_EXPIRED
→ Flutter calls /refresh_token with refresh token
→ Gets new access token
→ Retries original request

Refresh token expires (7 days)
→ User must log in again
```