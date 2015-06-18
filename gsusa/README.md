Girl Scouts USA
========

Only for the first time:
Install bower: http://bower.io/
cd content/src/main/theme
bower install


Also, if you'd like to have bootstrap content, go into the bootstrap folder, build the project, and upload the artifact through the package manager.


SSL
======
The dispatchers will require mod_ssl and SSL certificates.  By default, they are not installed since all SSL is handled by the loadbalancer.  However, GSUSA requires the creation of SSL proxy connections to third party sites and the SSL configuration is required. 



# Install mod_ssl
> yum install mod_ssl

# Generate CA Keys
> openssl req -config /etc/pki/tls/openssl.cnf -new -x509 -keyout /etc/pki/CA/private/cakey.pem -out /etc/pki/CA/cacert.pem -days 3650

CA certificate filename (or enter to create)
writing new private key to './demoCA/private/cakey.pem'
Enter PEM pass phrase: g0g1rls
Country Name (2 letter code) [XX]:US
State or Province Name (full name) []:New York
Locality Name (eg, city) [Default City]:New York
Organization Name (eg, company) [Default Company Ltd]:Girl Scouts of the United States of America
Organizational Unit Name (eg, section) []:gsusa
Common Name (eg, your name or your server's hostname) []:girlscouts-dev2-dispatcher1useast1
Email Address []: echandle@adobe.com

Using configuration from /etc/pki/tls/openssl.cnf

# note: you may need to run this a second time after  oving index.txt into target location to update the certificate database

# Enable mod_ssl in httpd.conf
LoadModule ssl_module modules/mod_ssl.so

# Create an SSL Certificate
> openssl genrsa -out /etc/pki/tls/private/apachekey.pem 2048

# Generate a Certificate Signing Request (CSR)
# Use `hostname` command on server for hostname
> openssl req -new -key /etc/pki/tls/private/apachekey.pem -out /etc/pki/tls/certs/apachereq.csr
SAME AS ABOVE except Organization Unit Name: gsweb

# Create Signed Web Server Certificate
> openssl ca -in /etc/pki/tls/certs/apachereq.csr -out /etc/pki/tls/certs/apachecert.pem

# Create Self Signed SSL Certificate
> openssl x509 -req -days 3650 -in /etc/pki/tls/certs/apachereq.csr -signkey /etc/pki/tls/private/apachekey.pem -out /etc/pki/tls/certs/apache.crt

# Update Apache config /etc/http/conf.d/ssl.conf
SSLCertificateFile /etc/pki/tls/certs/apache.crt
SSLCertificateKeyFile /etc/pki/tls/private/apachekey.pem

