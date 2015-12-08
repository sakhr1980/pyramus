## Eclipse ##

  * Download and extract Eclipse (http://www.eclipse.org/downloads/)
  * Start Eclipse
  * Install following items from "http://download.eclipse.org/releases/indigo" update site
    * Collaboration
      * Subversive SVN Team Provider (Incubation)
    * Web, XML, Java EE and OSGi Enterprise Development
      * Dali Java Persistence Tools - JPA Support
      * Eclipse Faceted Project Framework
      * Eclipse Java EE Developer Tools
      * Eclipse Java Web Developer Tools
      * Eclipse Web Developer Tools
      * JavaScript Development Tools
  * Install following items from "http://download.jboss.org/jbosstools/updates/development/indigo/" update site:
    * JBoss Maven Support
      * JBoss Maven Hibernate Configurator
      * JBoss Maven Integration
    * JBoss Web and Java EE Development
      * Hibernate Tools
      * JBoss WebServices Tools
      * JBossAS Tools

## Projects ##

  * Import Pyramus project into workspace

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
  * Rename preferences.sample as preferences.properties in xml-db-updater folder
  * Edit preferences.properties -file with text editor and set: updates.folder to match your pyramus updates folder (e.g. /eclipse/workspace/Pyramus/updates) and update database settings to match your configuration.
  * Start updater and run suggested updates.

## JBoss ##

  * Download and extract JBoss (http://www.jboss.org/jbossas/downloads/)
  * Navigate to JBoss/bin folder in console (or command prompt in windows)
  * Run ./add-user.sh (or add-user.bat in windows) and add new Management User to ManagementRealm
  * Copy mysql connector jar into JBoss/standalone/deployments folder
  * Create new keystore into JBoss/standalone/configuration -folder
    * keytool -genkey -keysize 2048 -keyalg RSA -keystore jboss.keystore -alias tomcat
    * Use your host name as CN (e.g. dev.pyramus.fi)
  * Open keystore with Portecle (http://sourceforge.net/projects/portecle/)
  * Export tomcat alias in PEM format to hard drive
  * Import exported sertificate as trusted back to keystore with name root
  * Edit JBoss/standalone/configuration/standalone.xml file
  * Locate urn:jboss:domain:web:1.1 -subsystem
  * Add redirect-port="8443" attribute into http connector
  * Add https connector:
```
<connector name="https" protocol="HTTP/1.1" scheme="https" socket-binding="https" secure="true">
  <ssl name="ssl" key-alias="tomcat" password="password" certificate-key-file="../standalone/configuration/jboss.keystore" protocol="TLS" verify-client="false"/>
</connector>
```
  * Open Eclipse
  * Switch to Java EE perspective
  * Configure JBoss server to Eclipse (Servers > New Server)
  * Start JBoss (Servers > JBoss 7.1 Runtime Server > Start)
  * Navigate to http://localhost:8080 with web browser
  * Add new datasource from Profile > Connector > Datasources > Add, with name jdbc/pyramus and JNDI name java:/jdbc/pyramus
  * Enable new datasource and test that it works (Connection / Test Connection)
  * Go to the datasource's Connection tab and set "Use JTA?" to true (alternatively, edit JBoss/standalone/configuration/standalone.xml file and ensure that in the afore-mentioned datasource, the jta attribute is set to true)

## Setup ##

  * Go back to Eclipse and drag Pyramus project into JBoss server
  * Wait until project is deployed and navigate to http://localhost:8080/Pyramus/ with your web browser
  * If Pyramus loads fine, navigate to https://localhost:8443/Pyramus/system/initialdata.page with your web browser.
  * Login in with admin / querty

Happy coding :)