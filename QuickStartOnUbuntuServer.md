## Prerequisites ##

This guide assumes that you have a fresh Ubuntu Server 12.04 LTS running in your computer and domain name pointing to it.

## JBossAS 7 ##

We are installing Pyramus on top of JBossAS 7.1.1-final so first we need to get it running.

Install OpenJDK and unzip packages
```
# sudo apt-get install openjdk-7-jdk unzip
```

Fetch JBoss tar ball
```
# cd /tmp
# wget http://download.jboss.org/jbossas/7.1/jboss-as-7.1.1.Final/jboss-as-7.1.1.Final.tar.gz
```
Extract JBoss into /opt -folder
```
# sudo tar -xvf /tmp/jboss-as-7.1.1.Final.tar.gz -C /opt
```

Add user for jboss
```
# sudo adduser jboss
```

Change jboss -folder to be owned by jboss -user
```
# sudo chown -R jboss:jboss /opt/jboss-as-7.1.1.Final/
```
Add new administrative user for JBoss
  * Type of user: Management user
  * Realm: ManagementRealm

```
# sudo -u jboss /opt/jboss-as-7.1.1.Final/bin/add-user.sh
```

Download MySQL driver
```
# cd /tmp
# wget http://pyramus.googlecode.com/files/jboss-as-mysql-module-7.1.1.zip
```
Extract driver into JBoss
```
# sudo -u jboss unzip jboss-as-mysql-module-7.1.1.zip -d /opt/jboss-as-7.1.1.Final/modules/
```


Edit /opt/jboss-as-7.1.1.Final/standalone/configuration/standalone.xml with your favourite text editor

```
# sudo -u jboss vi /opt/jboss-as-7.1.1.Final/standalone/configuration/standalone.xml 
```

Locate "urn:jboss:domain:datasources:1.0" -subsystem. Find drivers inside in and add mysql driver inside that.

```
<driver name="mysql" module="com.mysql.jdbc">
  <driver-class>com.mysql.jdbc.Driver</driver-class>
</driver>
```

Add datasource for Pyramus before drivers -tag.
```
<datasource jta="true" jndi-name="java:/jdbc/pyramus" pool-name="jdbc/pyramus" enabled="true" use-ccm="false">
  <connection-url>jdbc:mysql://localhost:3306/pyramus</connection-url>
  <driver>mysql</driver>
  <security>
    <user-name>user</user-name>
    <password>password</password>
  </security>
  <validation>
    <validate-on-match>false</validate-on-match>
    <background-validation>false</background-validation>
  </validation>
  <statement>
    <share-prepared-statements>false</share-prepared-statements>
  </statement>
</datasource>
```

Locate "urn:jboss:domain:web:1.1"" -subsystem and change enable-welcome-root to false in virtual-server and change alias example.com to match your address.

```
<virtual-server name="default-host" enable-welcome-root="false">
  <alias name="localhost"/>
  <alias name="test.pyramus.fi"/>
</virtual-server>  
```

Locate interfaces and change the contents of public interface to `<any-address/>`

```
<interface name="public">
  <any-address/>
</interface>  
```

## Database ##

Install MySQL
```
sudo apt-get install mysql-server
```
Login to MySQL as root and create new database and user for it.
```
# mysql -p -u root
> create database pyramus default charset utf8;  
> create user 'pyramus'@'localhost' identified by 'password';
> grant all privileges on pyramus.* to 'pyramus'@'localhost';
> flush privileges;
> exit;
```

TODO: Write how to install xmldb-updater

Download latest database updates from http://code.google.com/p/pyramus/downloads/ and extract them to /opt/pyramus-updates
```
# cd /tmp
# wget https://pyramus.googlecode.com/files/updates-0.6.3.zip
# sudo mkdir /opt/pyramus-updates
# sudo unzip /tmp/updates-0.6.3.zip -d /opt/pyramus-updates
```
Run updater to create database
```
# sudo java -jar /opt/pyramus-updater/xmldb-updater-2.0.0.jar --databaseDriversFolder /opt/jboss-as-7.1.1.Final/modules/com/mysql/jdbc/main/ --databaseUrl jdbc:mysql://localhost:3306/pyramus --databaseUsername pyramus --databasePassword pyramus --databaseVendor MySQL --force /opt/pyramus-updates/updates/
```
## WebServices ##

Edit /opt/jboss-as-7.1.1.Final/standalone/configuration/standalone.xml with your favourite text editor

```
# sudo -u jboss vi /opt/jboss-as-7.1.1.Final/standalone/configuration/standalone.xml 
```

Locate "urn:jboss:domain:security:1.1" -subsystem and add new security domain inside security-domains tag:

```
<security-domain name="WebServices" cache-type="default">
  <authentication>
    <login-module code="RealmUsersRoles" flag="required">
      <module-option name="realm" value="WebServices"/>
      <module-option name="password-stacking" value="useFirstPass"/>
      <module-option name="rolesProperties" value="${jboss.server.config.dir}/application-roles.properties"/>
      <module-option name="usersProperties" value="${jboss.server.config.dir}/application-users.properties"/>
    </login-module>
  </authentication>
</security-domain> 
```

Locate "urn:jboss:domain:webservices:1.1" -subsystem and change wsdl-host to `jbossws.undefined.host`.

Locate "extensions" -tag and add system-properties tag below it:
```
<system-properties>
  <property name="PyramusWSAllowedIPs" value="127.0.0.1"/>
</system-properties>
```
If webservices should be accessible from other locations more IP addresses can be added. Values should be separated by comma.

Save and close the file.

Add new user with following settings:
  * Type of user = Application User
  * Realm = WebServices
  * Username = (user)
  * Password = (password)
  * Roles = WebServices

```
  sudo -u jboss /opt/jboss-as-7.1.1.Final/bin/add-user.sh
```

### Sertificate ###
**Note** this guide uses unsigned sertificate which should not be used in production environment

Create new keystore into /opt/jboss-as-7.1.1.Final/standalone/configuration/.
**Use your host name as CN (e.g. www.pyramus.dev)**
```
# sudo keytool -genkey -keysize 2048 -keyalg RSA -keystore /opt/jboss-as-7.1.1.Final/standalone/configuration/jboss.keystore -alias tomcat
```
Export cer file from keystore
```
sudo keytool -exportcert -keystore /opt/jboss-as-7.1.1.Final/standalone/configuration/jboss.keystore -alias tomcat -file /tmp/tmp.cer
```
Import cer file back to keystore as trusted
```
sudo keytool -import -v -trustcacerts -alias root -keystore /opt/jboss-as-7.1.1.Final/standalone/configuration/jboss.keystore -file /tmp/tmp.cer
```

Edit /opt/jboss-as-7.1.1.Final/standalone/configuration/standalone.xml with your favourite text editor

```
# sudo -u jboss vi /opt/jboss-as-7.1.1.Final/standalone/configuration/standalone.xml 
```

Locate "urn:jboss:domain:web:1.1" -subsystem. Add redirect-port="443" attribute into http connector and add https connector:
```
<connector name="https" protocol="HTTP/1.1" scheme="https" socket-binding="https" secure="true">
  <ssl name="ssl" key-alias="tomcat" password="password" certificate-key-file="${jboss.server.config.dir}/jboss.keystore" protocol="TLS" verify-client="false"/>
</connector>
```
Locate "socket-binding-group" and change socket bindings for http and https 80 and 443
## Pyramus ##
Download pyramus and pyramusreports wars from Maven repository.
```
# cd /tmp
# wget http://maven.otavanopisto.fi:7070/nexus/content/repositories/releases/fi/pyramus/pyramus/0.6.4/pyramus-0.6.4.war
# wget http://maven.otavanopisto.fi:7070/nexus/content/repositories/releases/fi/pyramus/reports/0.6.4/reports-0.6.4.war
```
Copy wars into deployments -folder as ROOT.war and PyramusReports.war.
```
# sudo -u jboss cp pyramus-0.6.4.war /opt/jboss-as-7.1.1.Final/standalone/deployments/ROOT.war
# sudo -u jboss cp reports-0.6.4.war /opt/jboss-as-7.1.1.Final/standalone/deployments/PyramusReports.war
```

## Running JBoss as service ##

Create new file /etc/init.d/jboss with following contents:

```
#!/bin/sh

JBOSS_HOME=/opt/jboss-as-7.1.1.Final/
JBOSS_USER=root

case "$1" in
  start)
    echo "Starting JBoss AS 7.1.1" 
    su -c "${JBOSS_HOME}/bin/standalone.sh >& /dev/null &" ${JBOSS_USER}
    ;;
  stop)
    echo "Stopping JBoss AS 7.1.1" 
    su -c "${JBOSS_HOME}/bin/jboss-cli.sh --connect command=:shutdown" ${JBOSS_USER}
    ;;
  *)
  echo "Usage: /etc/init.d/jboss {start|stop}"
    exit 1
  ;;
esac  
```

Add execute permission to that file and set JBoss to start in Linux start up and shutdown in Linux shutdown
```
# sudo chmod a+x /etc/init.d/jboss
# cd /etc/rc2.d/
# sudo ln -s ../init.d/jboss ./S99jboss
# cd /etc/rc1.d/
# sudo ln -s ../init.d/jboss ./K99jboss
# cd /etc/rc0.d/
# sudo ln -s ../init.d/jboss ./K99jboss
```

Test by rebooting computer.

## Setup ##
Open Pyramus in your browser (e.g. http://test.pyramus.fi). It should redirect you to https. If it does not there is something wrong in your configuration.

If you are using unsigned sertificate your browser will warn you about it and you need to confirm security exception before proceeding.

If Pyramus loads fine, navigate to https://youraddress/system/initialdata.page with your web browser (e.g. https://test.pyramus.fi/system/initialdata.page).

Login in with admin / querty. This should trigger an entity indexing task. When indexing is complete your Pyramus is installed but does not have any settings so proceed set them up next.