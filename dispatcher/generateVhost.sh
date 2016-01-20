#!/bin/bash
if [ $# -eq 0 ]; then
    echo "generateVhost.sh [prod/preview/stage] [councilurlname] [index of vhost]"
    exit 1
fi

if [ "$1" = "prod" ] 
then
    var0="<VirtualHost *:80>
    ServerName "$1"."$2".org
    #ServerName www."$2".org
    DocumentRoot /mnt/var/www/html
    ErrorDocument 404 /content/"$2"/en/404.html
    ErrorDocument 500 /content/"$2"/en/500.html"

else
    var0="<VirtualHost *:80>
    ServerName "$1"."$2".org
    DocumentRoot /mnt/var/www/html
    ErrorDocument 404 \"404 GirlScouts\""
fi
    
var="
    RewriteEngine On
    RewriteLog \"logs/rewrite-www-"$2"-org.log\"
    RewriteLogLevel 1

    RewriteRule ^/en.html / [R=301,L]
    RewriteRule ^/$ /content/"$2"/en.html [PT]
    RewriteRule ^/robots.txt$ /en/robots.txt [PT]

    RewriteCond %{REQUEST_URI} !^/etc(.*) [NC]
    RewriteCond %{REQUEST_URI} !^/libs(.*) [NC]
    RewriteCond %{REQUEST_URI} !^/content(.*) [NC]
    RewriteCond %{REQUEST_URI} !^/system(.*) [NC]
    RewriteCond %{REQUEST_URI} !^/dam(.*) [NC]
    RewriteCond %{REQUEST_URI} !^/services(.*) [NC]
    RewriteRule ^/(.*) /content/"$2"/\$1 [PT]
     
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
 echo "$var0" > "$1"/vhost.d/"$3"_www_"$2"_org.conf
 echo "$var" >> "$1"/vhost.d/"$3"_www_"$2"_org.conf


