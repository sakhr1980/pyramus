## Normal Google auth ##

_Set following setting to your System Settings:_

`authentication.OpenID.identifier = https://www.google.com/accounts/o8/id`

## Google Apps ##

_Set following setting to your System Settings (replace mydomain.com with your real domain name):_

`authentication.OpenID.identifier = https://www.google.com/accounts/o8/site-xrds?hd=mydomain.com`

OpenId requires that you have _http://mydomain.com/openid_ and _http://mydomain.com/.well-known/host-meta_ -files responding from your domain.

Contents of this files should be

openid:
```
<?xml version="1.0" encoding="UTF-8"?>
<xrds:XRDS xmlns:xrds="xri://$xrds" xmlns="xri://$xrd*($v*2.0)">
  <XRD>
    <Service priority="0">
      <Type>http://specs.openid.net/auth/2.0/signon</Type>
      <URI>https://www.google.com/a/mydomain.com/o8/ud?be=o8</URI>
    </Service>
  </XRD>
</xrds:XRDS>
```

```
Link: <https://www.google.com/accounts/o8/site-xrds?hd=mydomain.com>; rel="describedby http://reltype.google.com/openid/xrd-op"; type="application/xrds+xml"
```

**Note!** openid -file has to of mime-type _application/xrds+xml_. For example in Apache this can be done by adding following directives into .htaccess -file:
```
<Files "openid">
ForceType application/xrds+xml
</Files>   
```