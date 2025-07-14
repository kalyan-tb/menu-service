# 🧾 Design Document - Menu Service

---

## 🚀 Technology Stack

| Layer            | Technology              |
| ---------------- | ----------------------- |
| Language         | Java 17                 |
| Framework        | Spring Boot 3.4.8       |
| Database         | MySQL (Dockerized)      |
| Caching (L1)     | Caffeine *(planned)*    |
| Caching (L2)     | Redis (Dockerized)      |
| API Layer        | REST (under `/api/v1/`) |
| Containerization | Docker, Docker Compose  |

---

## 🏗️ Current Implementation

### 🧱 Architecture: Monolith (per assignment scope)

### 🧩 Component Responsibilities

| Component      | Responsibilities                                        |
| -------------- | ------------------------------------------------------- |
| **Controller** | REST endpoints, input validation                        |
| **Service**    | Business logic, transactional boundaries                |
| **Repository** | JPA-based DB access                                     |
| **Mapper**     | DTO ↔ Entity conversions                                |
| **Cache**      | Redis (L2), future: Caffeine (L1) for performance boost |

---

## 🏛️ High-Level Architecture

```
Client ──> REST API ──> Service Layer ──> Repository ──> MySQL
                      │               └─> Redis (cache layer)
                      └─> Caching Layer (L1 + L2)
```

---

## 🗄️ Data Modeling

### 📋 Restaurant Table

```sql
CREATE TABLE restaurant (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  address VARCHAR(255),
  city VARCHAR(255),
  pincode VARCHAR(20),
  created_on DATETIME(6) DEFAULT CURRENT_TIMESTAMP,
  updated_on DATETIME(6) DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);
CREATE INDEX idx_restaurant_city ON restaurant (city);
```

### 🍽️ MenuItem Table

```sql
CREATE TABLE menu (
  id BIGINT NOT NULL AUTO_INCREMENT,
  availability BIT(1) NOT NULL,
  created_on DATETIME(6) DEFAULT NULL,
  dish_name VARCHAR(255) NOT NULL,
  price DOUBLE NOT NULL,
  type ENUM('NON_VEG','VEG') NOT NULL,
  updated_on DATETIME(6) DEFAULT NULL,
  restaurant_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  KEY FK_menu_restaurant (restaurant_id),
  CONSTRAINT FK_menu_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant (id)
);
CREATE INDEX idx_menu_dish_name ON menu (dish_name);
CREATE INDEX idx_menu_restaurant_type ON menu (restaurant_id, type);
```

---

## 📦 API Data Models

### 📥 `MenuItemRequest`

```json
{
  "dishName": "Margherita Pizza",
  "price": 299.99,
  "availability": true,
  "type": "VEG"
}
```

### 📥 `CreateRestaurantRequest`

```json
{
  "name": "Pizza Haven",
  "address": "123 Food Street, Koramangala",
  "city": "Bangalore",
  "pincode": "560034",
  "menuItems": [
    { "dishName": "Margherita Pizza", "price": 299.99, "availability": true, "type": "VEG" },
    { "dishName": "Chicken Tikka Pizza", "price": 399.99, "availability": false, "type": "NON_VEG" }
  ]
}
```

### 📤 `PaginatedMenuItemResponse`

```json
{
  "restaurantId": 1,
  "restaurantName": "Pizza Haven",
  "menuItems": [...],
  "currentPage": 0,
  "totalPages": 1,
  "totalElements": 2
}
```

### 📤 `RestaurantMenuResponse`

```json
{
  "id": 1,
  "name": "Pizza Haven",
  "menuItems": [...]
}
```

### 📤 `RestaurantResponse`

```json
{
  "id": 1,
  "name": "Pizza Haven",
  "address": "123 Food Street, Koramangala",
  "city": "Bangalore",
  "pincode": "560034"
}
```

### 📤 `MenuItemBasicResponse`

```json
{
  "id": 1,
  "dishName": "Margherita Pizza",
  "price": 299.99,
  "availability": true,
  "type": "VEG"
}
```

---

## 🧠 Caching Strategy

| Layer  | Technology           | Description                                         |
| ------ | -------------------- | --------------------------------------------------- |
| **L1** | Caffeine *(planned)* | In-memory cache for high-frequency reads            |
| **L2** | Redis                | Distributed cache, TTL-configured, stores menu data |

* **Pattern**: Cache-aside
* **On Miss**: DB → Cache
* **On Update/Delete**: Invalidate cache

---

## 📊 Rate Limiting, Pagination & Versioning

| Feature           | Description                                                 |
| ----------------- | ----------------------------------------------------------- |
| **Rate Limiting** | TO-DO: Redis-based, per-IP/client key throttling            |
| **Pagination**    | Menu fetch APIs support `page` and `size` query parameters  |
| **Versioning**    | URL-based: `/api/v1/restaurant/` for backward compatibility |

---

## 💡 Assumptions & Trade-offs

### ✔️ Assumptions

* Menus change less frequently than they're read
* Restaurant info is less volatile than menus
* Eventual consistency is acceptable for menu data

### ⚖️ Trade-offs

* **Cache vs Consistency**: Slight staleness is okay for better performance
* **Memory vs Speed**: Caching increases memory usage but decreases DB load

---

## 📈 Monitoring & Observability

| Tool         | Description                                     |
| ------------ | ----------------------------------------------- |
| **Actuator** | Health checks, metrics, info, caches, etc.      |
| **Logging**  | Structured logs with correlation ID & errors    |
| **Metrics**  | Response time, error rate, cache stats          |
| **Planned**  | Prometheus, Grafana, ELK stack for full tracing |

---

✅ *This design document serves as a technical blueprint for developers, testers, and architects working on the Menu microservice.*
