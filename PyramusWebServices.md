## Configuring PyramusWebServices ##

  * Go to JBoss Administration Console (http://localhost:8080/)
  * Go to Profile > Security > Security Domains
  * Add a new security domain with name **WebServices** and cache type **default**
  * Click View for the newly created security domain
  * Add a new login module with code **RealmUsersRoles** and flag **required**
  * Click the newly created login module and select the Module Options tab
  * Add the following module options:
    * **realm = WebServices**
    * **password-stacking = useFirstPass**
    * **rolesProperties = ${jboss.server.config.dir}/application-roles.properties**
    * **usersProperties = ${jboss.server.config.dir}/application-users.properties**
  * Go to JBoss bin folder and run add-user
    * Type of user = **Application User**
    * Realm = **WebServices**
    * Username = (user)
    * Password = (password)
    * Roles = **WebServices**
  * Edit JBoss standalone/configuration/standalone.xml
    * Locate subsystem urn:jboss:domain:webservices:1.1 and change wsdl-host to **jbossws.undefined.host**
    * Locate interfaces and change the contents of public interface to **`<any-address/>`**
    * Locate interfaces and change the contents of unsecure interfacde to **`<any-address/>`**
    * Add **`<property name="PyramusWSAllowedIPs" value="127.0.0.1"/>`**under **`<system-properties>`** and set value as needed to allow access from remote computers. Several IP addresses can be entered with comma separator.

  * Restart JBoss