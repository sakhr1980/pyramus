## Settings for different LDAP servers ##

**IBM Directory**<br /><br />
`authentication.ldap.uniqueIdAttr = ibm-entryUuid`<br />
`authentication.ldap.uniqueIdEncoded = 0`<br /><br />
**Sun ONE Directory Server**<br /><br />
`authentication.ldap.uniqueIdAttr = nsuniqueid`<br />
`authentication.ldap.uniqueIdEncoded = 0`<br /><br />
**Microsoft Active Directory**<br /><br />
`authentication.ldap.uniqueIdAttr = objectGUID`<br />
`authentication.ldap.uniqueIdEncoded = 1`<br /><br />
**eDirectory use**<br /><br />
`authentication.ldap.uniqueIdAttr = GUID`<br />
`authentication.ldap.uniqueIdEncoded = 1`<br /><br />