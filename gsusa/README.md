Girl Scouts USA
========

Only for the first time or if someone adds some import to an scss file or if you see any css error.
Install bower: http://bower.io/
cd content/src/main/theme
bower install


Also, if you'd like to have bootstrap content, go into the bootstrap folder, build the project, and upload the artifact through the package manager.


SSL
======
The dispatchers will require mod_ssl and SSL certificates.  By default, they are not installed since all SSL is handled by the loadbalancer.  However, GSUSA requires the creation of SSL proxy connections to third party sites and the SSL configuration is required. 



# Install mod_ssl
> yum install mod_ssl

# Set the following default values in /etc/pki/tls/openssl.cnf
countryName_default = US
stateOrProvinceName_default = New York
localityName_default = New York
0.organizationName_default = Girl Scouts of the United States of America

# Use `hostname` command on server for hostname
# Generate CA Keys
> openssl req -config /etc/pki/tls/openssl.cnf -new -x509 -keyout /etc/pki/CA/private/cakey.pem -out /etc/pki/CA/cacert.pem -days 3650

# Any fields with default values already set can be left blank
CA certificate filename (or enter to create)
writing new private key to './demoCA/private/cakey.pem'
Enter PEM pass phrase: g0g1rls
Country Name (2 letter code) [US]:
State or Province Name (full name) [New York]:
Locality Name (eg, city) [New York]:
Organization Name (eg, company) [Girl Scouts of the United States of America]:
Organizational Unit Name (eg, section) []:gsusa
Common Name (eg, your name or your server's hostname) []:girlscouts-dev2-dispatcher1useast1
Email Address []: echandle@adobe.com

Using configuration from /etc/pki/tls/openssl.cnf

# note: you may need to run this a second time after moving index.txt into target location to update the certificate database

# Enable mod_ssl in httpd.conf
LoadModule ssl_module modules/mod_ssl.so

# Create an SSL Certificate
> openssl genrsa -out /etc/pki/tls/private/apachekey.pem 2048

# Generate a Certificate Signing Request (CSR)
# Use `hostname` command on server for hostname
> openssl req -new -key /etc/pki/tls/private/apachekey.pem -out /etc/pki/tls/certs/apachereq.csr
SAME AS ABOVE except Organization Unit Name: gsweb
#Leave challenge password and optional company name blank

# Create Signed Web Server Certificate
> openssl ca -in /etc/pki/tls/certs/apachereq.csr -out /etc/pki/tls/certs/apachecert.pem
# if you get an error for a missing index.txt file, use touch /etc/pki/CA/index.txt 
# if you get another error for a missing serial file, create a file called /etc/pki/CA/serial containing 01 and a linebreak
# if you get an error for TXT_DB error number 2, remove entries inside  /etc/pki/CA/index.txt


# Create Self Signed SSL Certificate
> openssl x509 -req -days 3650 -in /etc/pki/tls/certs/apachereq.csr -signkey /etc/pki/tls/private/apachekey.pem -out /etc/pki/tls/certs/apache.crt

# Update Apache config /etc/httpd/conf.d/ssl.conf
SSLCertificateFile /etc/pki/tls/certs/apache.crt
SSLCertificateKeyFile /etc/pki/tls/private/apachekey.pem

