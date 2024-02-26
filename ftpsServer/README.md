# Reference Documentation

Spring boot project with an embedded FTPS server using Apache Mina project - https://mina.apache.org/ftpserver-project/embedding_ftpserver.html

## How to generate the SSL certificates

1. Generate your own Self-Signed certificate using these commands:

    ```shell
    # subject=C=UK, ST=London, L=London, O=acme, OU=sectionA, CN=acme.ftp, emailAddress=test@test.com
    # go to folder src/main/resources/ftps/certs
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

## How to run the project

1. Start the FTPS server (it starts on port 990)
   ```shell
   mvn clean spring-boot:run -Dspring.profiles.active=local
   ```
2. CURL commands for the embedded FTPS server
   ```shell
   # list the remote directory
   curl -v --cacert src/main/resources/ftps/certs/domain.crt ftps://demo:secret1234@acme.ftp:990/
   
   # upload a new file to the remote directory
   curl -v --cacert src/main/resources/ftps/certs/domain.crt -T README.md ftps://demo:secret1234@acme.ftp:990/
   ```


