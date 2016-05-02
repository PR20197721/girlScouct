#!/bin/bash
if [ $# -eq 0 ]; then
    echo "generateVhost.sh [councilurlname] [index of vhost]"
    exit 1
fi

var0="<VirtualHost *:80>
    ServerName "{{prefix.prod}}"."$1".org
    #ServerName www."$1".org
    DocumentRoot /mnt/var/www/html
    ErrorDocument 404 /content/"$1"/en/404.html
    ErrorDocument 500 /content/"$1"/en/500.html"
    
var="
    RewriteEngine On
    RewriteLog \"logs/rewrite-www-"$1"-org.log\"
    RewriteLogLevel 1

    RewriteRule ^/en.html / [R=301,L]
    RewriteRule ^/$ /content/"$1"/en.html [PT]
    RewriteRule ^/robots.txt$ /en/robots.txt [PT]

    RewriteCond %{REQUEST_URI} !^/etc(.*) [NC]
    RewriteCond %{REQUEST_URI} !^/libs(.*) [NC]
    RewriteCond %{REQUEST_URI} !^/content(.*) [NC]
    RewriteCond %{REQUEST_URI} !^/system(.*) [NC]
    RewriteCond %{REQUEST_URI} !^/dam(.*) [NC]
    RewriteCond %{REQUEST_URI} !^/services(.*) [NC]
    RewriteRule ^/(.*) /content/"$1"/\$1 [PT]
     
    <Directory \"/mnt/var/www/html\">
            <IfModule disp_apache2.c>
                    SetHandler dispatcher-handler
                    ModMimeUsePathInfo On
            </IfModule>
            Options FollowSymLinks
            AllowOverride AuthConfig
            Order allow,deny
            Allow from all
    </Directory>
</VirtualHost>
"
echo "$var0" > "template/vhost.d/$2"_www_"$1"_org.conf
echo "$var" >> "template/vhost.d/$2"_www_"$1"_org.conf
