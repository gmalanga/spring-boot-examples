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

1. Generate your own Self-Signed certificate using these commands:
   ```shell
   # subject=C=UK, ST=London, L=London, O=acme, OU=sectionA, CN=acme.ftp, emailAddress=test@test.com
   # go to folder src/main/resources/ftp/certs
   cd src/main/resources/ftp/certs
   # create both the private key and CSR with a single command
   openssl req -newkey rsa:2048 -keyout domain.key -out domain.csr
   # create a Self-Signed Certificate
   openssl x509 -signkey domain.key -in domain.csr -req -days 365 -out domain.crt
   # Create a Self-Signed Root CA
   openssl req -x509 -sha256 -days 1825 -newkey rsa:2048 -keyout rootCA.key -out rootCA.crt
   # sign out CSR (domain.csr) with the root CA certificate and its private key
   openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in domain.csr -out domain.crt -days 365 -CAcreateserial -extfile domain.ext
   # View Certificates
   openssl x509 -text -noout -in domain.crt
   # Convert PEM to PKCS12
   openssl pkcs12 -inkey domain.key -in domain.crt -export -out domain.pfx
   # Generate the JKS file
   keytool -importkeystore -srckeystore domain.pfx -srcstoretype pkcs12 -srcalias 1 -srcstorepass password -destkeystore domain.jks -deststoretype jks -deststorepass password -destalias myalias
   
   ```
2. Update your /etc/hosts file to include the name of your server "acme.ftp"
   ```shell
   sudo vim /etc/hosts 
   # add the following line
   127.0.0.1       acme.ftp
   ```
3. Start the FTPS server (it starts on port 990)
   ```shell
   mvn clean spring-boot:run -Dspring.profiles.active=local
   ```
4. CURL commands for the embedded FTPS server
   ```shell
   # list the remote directory
   curl -v --cacert src/main/resources/ftp/certs/domain.crt ftps://demo:secret1234@acme.ftp:990/
   
   # upload a new file to the remote directory
   curl -v --cacert src/main/resources/ftp/certs/domain.crt -T README.md ftps://demo:secret1234@acme.ftp:990/
   ```





