## Prerequisites ##

  * Pyramus is JavaEE application and thus requires that Java runtime environment is installed on the computer.

## Database ##

  * Get MySQL server up and running.
  * Make sure MySQL uses InnoDB as default
  * Login to MySQL as root
  * Create new database with name of your choosing
    * In example we use pyramus.
    * Encoding should be utf8 (create database pyramus default charset utf8)
  * Download Xmldb-updater from (http://code.google.com/p/java-xmldb-updater/downloads/list)
  * Extract Xmldb-updater somewhere in your computer
  * Download MySQL Connector (http://dev.mysql.com/downloads/connector/j/)
  * Extract mysql-connector-java-x.x.x-bin.jar from connector zip and copy it to xml-db-updater/drivers folder
  * Download latest database updates from http://code.google.com/p/pyramus/downloads/ and extract them to some folder (e.g. /PyramusUpdates/)
  * Rename preferences.sample as preferences.properties in xml-db-updater folder
  * Edit preferences.properties -file with text editor and set: updates.folder to match your pyramus updates folder (e.g. /PyramusUpdates/) and update database settings to match your configuration.
  * Start updater and run suggested updates.

## JBoss ##

  * Download and extract JBoss (http://www.jboss.org/jbossas/downloads/)
  * Navigate to JBoss/bin folder in console (or command prompt in windows)
  * Run ./add-user.sh (or add-user.bat in windows) and add new Management User to ManagementRealm
  * Copy mysql connector jar into JBoss/standalone/deployments -folder
  * Locate urn:jboss:domain:datasources:1.0 subsystem from JBoss/standalone/configuration/standalone.xml. Add new datasource inside datasources tag. Use following example. Note that you need to change DATABASE from connection-url, VERSION (e.g. 5.1.22) from driver and USER and PASSWORD from security.
```
<datasource jta="true" jndi-name="java:/jdbc/pyramus" pool-name="jdbc/pyramus" enabled="true" use-ccm="false">
  <connection-url>jdbc:mysql://localhost:3306/DATABASE</connection-url>
  <driver-class>com.mysql.jdbc.Driver</driver-class>
  <driver>mysql-connector-java-VERSION-bin.jar</driver>
  <security>
    <user-name>USER</user-name>
    <password>PASSWORD</password>
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

  * Locate urn:jboss:domain:web:1.1 -subsystem and find virtual-server within it. Change enable-welcome-root attribute into false and change example.com into your domain name in alias tag (e.g. www.pyramus.dev).
  * Add https sertificate

### Unofficial Sertificate ###

  * Create new keystore into JBoss/standalone/configuration -folder
    * keytool -genkey -keysize 2048 -keyalg RSA -keystore jboss.keystore -alias tomcat
    * Use your host name as CN (e.g. www.pyramus.dev)
  * Open keystore with Portecle (http://sourceforge.net/projects/portecle/)
  * Export tomcat alias in PEM format to hard drive
  * Import exported sertificate as trusted back to keystore with name root
  * Edit JBoss/standalone/configuration/standalone.xml file
  * Locate urn:jboss:domain:web:1.1 -subsystem
  * Add redirect-port="443" attribute into http connector
  * Add https connector:
```
<connector name="https" protocol="HTTP/1.1" scheme="https" socket-binding="https" secure="true">
  <ssl name="ssl" key-alias="tomcat" password="password" certificate-key-file="../standalone/configuration/jboss.keystore" protocol="TLS" verify-client="false"/>
</connector>
```
  * Change http and https ports into 80 and 443 in socket-binding-group

### Official Sertificate ###

TODO

## WebServices ##

  * Locate urn:jboss:domain:security:1.1 subsystem from JBoss/standalone/configuration/standalone.xml
  * Add new security domain inside security-domains tag:
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
  * Go to JBoss/bin folder and run add-user.sh (or add-user.bat in Windows)
    * Type of user = **Application User**
    * Realm = **WebServices**
    * Username = (user)
    * Password = (password)
    * Roles = **WebServices**
  * Edit JBoss standalone/configuration/standalone.xml
    * Locate subsystem urn:jboss:domain:webservices:1.1 and change wsdl-host to **jbossws.undefined.host**
    * Locate interfaces and change the contents of public interface to **`<any-address/>`**
    * Locate interfaces and change the contents of unsecure interfacde to **`<any-address/>`**
    * Insert new tag **`<system-properties>`** below extensions tag (if does not already exist) and add **`<property name="PyramusWSAllowedIPs" value="127.0.0.1"/>`** under it. If WebServices should be accessible from other locations more IP addresses can be added. Values should be separated by comma.

## Setup ##
  * Download latest version of Pyramus and Pyramus reports from http://code.google.com/p/pyramus/downloads/
  * Copy both files into JBoss/standalone/deployments -folder and rename them to ROOT.war and PyramusReports.war
  * Start JBoss by running standalone.sh (or standalone.bat in Windows) from JBoss/bin folder. Note that you might need to run JBoss as a root user to have permission to use ports below 1024
  * Wait until project is deployed and navigate to http://youraddress/ with your web browser (e.g. www.pyramus.dev)
  * If Pyramus loads fine, navigate to http://youraddress/system/initialdata.page with your web browser (e.g. www.pyramus.dev/system/initialdata.page).
  * Login in with admin / querty. This should trigger an entity indexing task. When indexing is complete your Pyramus is installed but does not have any settings so proceed set them up next.