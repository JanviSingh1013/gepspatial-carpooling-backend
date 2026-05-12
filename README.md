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
```