# Air Quality Index (AQI) Monitoring System

A Spring Boot application that provides real-time air quality data for cities worldwide with intelligent caching capabilities.

## 🌟 Features

- **Real-time AQI Data**: Fetches current air quality information from AQICN API
- **Smart Caching**: Implements Caffeine cache with configurable expiration policies
- **Cache Statistics**: Monitor cache performance with detailed metrics
- **RESTful API**: Clean API endpoints for air quality data
- **Error Handling**: Comprehensive exception handling and validation
- **Cross-Origin Support**: Configured for frontend integration

## 🏗️ Architecture

```
┌─────────────┐    ┌───────────────┐    ┌─────────────┐    ┌─────────────┐
│   Controller │ -> │ Cached Service │ -> │ AQICN Service │ -> │ External API │
└─────────────┘    └───────────────┘    └─────────────┘    └─────────────┘
         │                  │
         │                  └───┐
         │                      │
         ▼                      ▼
┌─────────────┐        ┌─────────────┐
│   DTOs      │        │   Cache     │
│   (Data     │        │   (Caffeine)│
│   Transfer) │        └─────────────┘
└─────────────┘
```
## Results
## Searh_With_City_Name API
<img width="800" height="550" alt="1" src="https://github.com/user-attachments/assets/39b88115-54f6-4d9b-9023-720484b0051b" />

## Cache status API
<img width="800" height="550" alt="image" src="https://github.com/user-attachments/assets/64c13231-926e-4057-a4d8-c8785c9301dd" />

## Cache_MaxSize
<img width="651" height="372" alt="3" src="https://github.com/user-attachments/assets/fe2fab35-7212-4bff-8fa3-4eb7dcd6ec93" /> 

## Cache DetailInfo
<img width="670" height="394" alt="4" src="https://github.com/user-attachments/assets/7e5ed41e-87f4-4d3b-8986-f5f5c2103ba2" />

## Terminal Output SpringBoot
<img width="740" height="374" alt="5" src="https://github.com/user-attachments/assets/29873c8a-aa70-47f5-bf21-78f632ae3ff7" />





## 📦 Dependencies

- **Spring Boot 3.x** - Application framework
- **Caffeine** - High-performance caching
- **Lombok** - Reduced boilerplate code
- **Spring Validation** - Request validation
- **Spring Web** - REST API support

## ⚙️ Configuration

### Cache Configuration
- **Initial Capacity**: 100 entries
- **Maximum Size**: 500 entries
- **Write Expiration**: 10 minutes
- **Access Expiration**: 5 minutes
- **Statistics**: Enabled for monitoring

### API Configuration
```properties
aqicn.api.key=your_api_key_here
aqicn.api.url=https://api.waqi.info
```

## 🚀 API Endpoints

### Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/air-quality/search` | Get air quality data for a city |
| `GET` | `/api/v1/air-quality/cache/stats` | Get cache statistics |
| `DELETE` | `/api/v1/air-quality/cache/{city}` | Evict specific city from cache |

### Cache Testing Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/air-quality/cache/test/max-size` | Test cache maximum size limits |
| `GET` | `/api/v1/air-quality/cache/test/expiration` | Test cache expiration policies |
| `POST` | `/api/v1/air-quality/cache/test/force-expire` | Force cache expiration for testing |
| `GET` | `/api/v1/air-quality/cache/test/check-expired` | Manually check for expired entries |
| `GET` | `/api/v1/air-quality/cache/detailed-info` | Get detailed cache information |

## 📊 Usage Examples

### 1. Search for Air Quality Data
```bash
curl -X POST http://localhost:8080/api/v1/air-quality/search \
  -H "Content-Type: application/json" \
  -d '{"city": "london"}'
```

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "city": "London",
    "aqi": 45,
    "quality": "Good",
    "dominantPollutant": "pm25",
    "pollutants": {
      "pm25": 12.5,
      "pm10": 20.1,
      "o3": 45.2
    },
    "timestamp": "2024-01-15T10:30:00",
    "weather": null
  },
  "timestamp": 1705314600000
}
```

### 2. Get Cache Statistics
```bash
curl http://localhost:8080/api/v1/air-quality/cache/stats
```

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "hits": 15,
    "misses": 5,
    "evictions": 2,
    "hitRatio": 0.75,
    "size": 3
  },
  "timestamp": 1705314600000
}
```

### 3. Test Cache Maximum Size
```bash
curl http://localhost:8080/api/v1/air-quality/cache/test/max-size
```

### 4. Force Cache Expiration
```bash
curl -X POST http://localhost:8080/api/v1/air-quality/cache/test/force-expire \
  -H "Content-Type: application/json" \
  -d '{"city": "london"}'
```

## 🔧 Cache Management

### Cache Eviction Policies
- **Size-based**: Maximum 500 entries
- **Time-based**: 
  - Remove after 10 minutes of writing
  - Remove if not accessed for 5 minutes
- **Manual**: Programmatic cache eviction

### Cache Monitoring
The application provides comprehensive cache monitoring:
- Hit/miss ratios
- Eviction counts
- Current cache size
- Access timestamps
- Manual expiration controls

## 🛠️ Development

### Project Structure
```
src/main/java/com/example/aqi_project/
├── Config/
│   └── CacheConfig.java          # Cache configuration
├── Controllers/
│   └── AirQualityController.java # REST API endpoints
├── DTOs/
│   ├── ApiResponse.java          # Standard API response
│   ├── AqicnResponse.java        # External API response mapping
│   └── SearchRequest.java        # Search request validation
├── Exceptions/
│   └── GlobalExceptionHandler.java # Global error handling
├── Models/
│   └── AirQualityData.java       # Domain model
└── Service/
    ├── AirQualityService.java    # Service interface
    ├── AqicnService.java         # External API integration
    └── CachedAirQualityService.java # Caching layer
```

### Building and Running
```bash
# Build the project
./mvnw clean compile

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test
```

## 📈 Performance Features

- **Intelligent Caching**: Reduces external API calls
- **Statistics Tracking**: Monitor cache effectiveness
- **Memory Management**: Automatic eviction of old entries
- **Concurrent Access**: Thread-safe cache operations

## 🔍 Troubleshooting

### Common Issues

1. **API Rate Limiting**: Ensure you have a valid AQICN API key
2. **Cache Not Evicting**: Check expiration configuration
3. **City Not Found**: Verify city name spelling and API coverage

### Logs
The application provides detailed logging for:
- Cache hits/misses
- API requests/responses
- Eviction events
- Error conditions

## 📝 License

This project is for educational purposes. Ensure you comply with AQICN API terms of service when using in production.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

**Note**: Replace `your_api_key_here` with an actual AQICN API token for production use. The demo token has limited usage.
