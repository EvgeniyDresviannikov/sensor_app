# sensor_app
API:
- POST /api/v1/sensors/{uuid}/measurements
- GET /api/v1/sensors/{uuid}
- GET /api/v1/sensors/{uuid}/metrics

Swagger-ui:
http://localhost:8080/swagger-ui/index.html

DB:
http://localhost:8080/h2-console (credentials: sa/password)

This is a simple version of application received sensor data. 
Obviously it needs to be improved to work properly at the scale of hundreds of 
thousands of sensors sending 1 request per minute each.
The main focus of this version is to meet acceptance criteria mentioned in task.

Out of scope:
- Scalability
- Data partitioning
- Performance
- Security
- Validation


