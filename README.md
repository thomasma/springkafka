See blog post http://blogs.justenougharchitecture.com/metrics-with-spring-boot-prometheus-and-grafana/

## Updates made Feb 2026
- Removed unused Spring Cloud dependency
- Various minor code cleanup to tests
- Upgrade Testcontainers from 1.19.3 to 2.0.3 (major version bump)
  - Rename artifact IDs: junit-jupiter -> testcontainers-junit-jupiter, kafka -> testcontainers-kafka
  - Migrate KafkaContainer to new org.testcontainers.kafka package
  - Switch from Confluent cp-kafka with Zookeeper to apache/kafka with KRaft
- Override commons-lang3 to 3.17.0 to fix ArrayFill class missing error caused by commons-compress 1.26.2 (from Avro) needing a newer version than Spring Boot 3.2.0 manages


## Updates made in Aug 2025 
 1. The code has been updated for Java 21.
 2. Blog updated to use  open open-source Apache Kafka version instead of the Confluent package.
 3. Added Testcontainer integration tests.

