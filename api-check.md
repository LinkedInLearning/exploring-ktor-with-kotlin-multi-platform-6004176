# API Testing Commands

## APOD API Endpoints
```bash
# Health Check
curl http://localhost:8080/health

# Get Today's APOD
curl http://localhost:8080/api/apod/today

# Get APOD by Date
curl http://localhost:8080/api/apod/date/2024-08-16

# Get Random APOD
curl http://localhost:8080/api/apod/random

# Get APOD History
curl "http://localhost:8080/api/apod/history?page=1&pageSize=10"

# Get DB Status
curl http://localhost:8080/api/admin/db-status
```