# Reference Documentation

## Local development

1. Start the required containers
    ```shell
    docker-compose up
    ```
2. Start the application
   ```shell
   mvn clean spring-boot:run -Dspring.profiles.active=local
   ```
3. Check the health - http://localhost:8080/actuator/health/

4. Run the example
   ```shell
   curl -v http://localhost:8080/count/hello%20world
   ```

