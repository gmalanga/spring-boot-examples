# Reference Documentation

## FTP client - No SSL/TLS - Using docker container

### Local development with containers

1. Run the containers - This command will run two containers:
      - postgresSQL DB - localhost:5432
      - FTP server - localhost:21
   ```shell
   docker-compose up -d
   ```
2. Run the application - This command will start the Spring boot application and the embedded FTP server
   ```shell
   mvn clean spring-boot:run -Dspring.profiles.active=local
   ```
### Test the application

There are two channels defined in the FtpConfiguration class:
- The inbound channel - "FromFtpChannel" - To be able to read from the remote server
- The outbound channel - "ToFtpChannel" - To be able to write to the remote server

1. CURL command to create a CSV file and upload it to the FTP server
   ```shell
   curl -H 'Content-Type: application/json' -s -XPOST http://localhost:8080/api/ftp/file -d '{"key1":"value1", "key2":"value2"}'
   
   ```
2. CURL command to list file from the remote server
   ```shell
   curl -XGET http://localhost:8080/api/ftp/list
   ```

## FTPS client with SSL/TLS - Using Apache Mina project with embedded FTP server

### Integrated FTP Server using Apache Mina project

Reference - https://mina.apache.org/ftpserver-project/embedding_ftpserver.html

1. Run the FTPS server using the spring boot project located [here](../ftpsServer)
2. Copy the certificates from the FTPS server to the ftp client and update the application.yaml with the certificate path and the FTP/FTPS server configurations
   ```shell
   cp ../ftpsServer/src/main/resources/ftps/certs/* src/main/resources/ftp2/certs/
   ```
3. Start the client
   ```shell
   mvn clean spring-boot:run -Dspring.profiles.active=local
   ```
4. CURL commands to test the embedded FTPS server
   ```shell
   # list the remote directory
   curl -v --cacert src/main/resources/ftp2/certs/domain.crt ftps://demo:secret1234@acme.ftp:990/
   
   # upload a new file to the remote directory
   curl -v --cacert src/main/resources/ftp2/certs/domain.crt -T README.md ftps://demo:secret1234@acme.ftp:990/
   ```
5. CURL commands to test the client
   ```shell
   # This will call the POST REST API to start FTP and FTPS request
   curl -H 'Content-Type: application/json' -s -XPOST http://localhost:8080/api/ftp/file -d '{"key1":"value1", "key2":"value2"}'
   
   # This will cal the GET REST API to list the files from the remote FTP/FTPS server
   curl -XGET http://localhost:8080/api/ftp/list 
   ```
