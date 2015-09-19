GitBucket added support for LDAP Authentication in the version 1.5.
This Wiki page describes how to configure and troubleshoot it.

## Configuration

1. Login as Admin
2. Go to **Administration** and click **System Settings**
3. Check **LDAP** and enter the LDAP configuration:

| Name | Description | Example Value |
|:-----|:------------|:--------------|
| LDAP Host | LDAP host name | 192.168.32.1 |
| LDAP Port | LDAP port | 389 (default 389) |
| Bind DN | Username that has read access to the LDAP | uid=binduser,cn=bindclients,dc=example,dc=com |
| Bind Password | Password for Bind DN account | password |
| Base DN | Top level DN of your LDAP directory tree (used for user search) | dc=example,dc=com |
| User name attribute | Name of the LDAP attribute. This is used as the GitBucket username | uid |
| Mail address attribute | Email address of LDAP attribute | mail |
| Enable TLS | Whether to use encrypted connection | checked |
| Keystore | Path to the Java keystore | /etc/pki/java/cacerts |


### Keystore

Default system keystore is in:

```
${JAVA_HOME}/lib/security/jssecacerts
```

or in:

```
${JAVA_HOME}/lib/security/cacerts
```

Custom keystore can be set either in `/etc/sysconfig/gitbucket` by
specifying the following option:

```
GITBUCKET_JVM_OPTS="-Djavax.net.ssl.trustStore=/path/to/your/keystore"
```

or in Gitbucket's **System Settings** as described above.

You can use the following command to add your CA certificate to the
keystore:

```
$ keytool -import \
-file /path/to/your/cacert.pem \
-alias <your_cert_alias> \
-keystore /path/to/your/keystore
```

Older versions of Java don't know how to import certificate if it
contains anything else than the certificate itself. Then it's necessary
to strip everything else out like this:

```
$ cat /path/to/your/cacert.pem | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > /tmp/cacert.pem
$ keytool -import -trustcacerts -alias <your_cert_alias> -file /tmp/cacert.pem -keystore /path/to/your/keystore
```

You can list all keys from the keystore like this:

```
$ keytool -list -keystore /path/to/your/keystore
```


## Troubleshooting

LDAP debugging was enabled in the version 1.7. So if something goes
wrong, you should see the error in the log file:

```
15:43:56.529 [qtp1820788751-15] INFO  service.AccountService - LDAP Authentication Failed: System LDAP authentication failed.
15:43:37.386 [qtp1820788751-15] INFO  service.AccountService - LDAP Authentication Failed: User does not exist
15:49:34.565 [qtp1820788751-18] INFO  service.AccountService - LDAP Authentication Failed: User LDAP Authentication Failed.
15:41:09.370 [qtp1820788751-16] INFO  service.AccountService - LDAP Authentication Failed: Can't find mail address.
```

The following is the explanation for the above error messages:

* **System LDAP authentication failed**
    * Failed to access LDAP server by using _Bind DN_ and _Bind Password_
    * Wrong _LDAP Host_, _LDAP Port_, _Bind DN_ or _Bind Password_
* **User does not exists**
    * LDAP searched the user account from _Base DN_ but did not find it
* **User LDAP Authentication Failed**
    * Found user but failed to login (maybe wrong _Bind Password_)
* **Can't find mail address**
    * Found user and user authentication succeed but did not find email address (wrong _Mail address attribute_)