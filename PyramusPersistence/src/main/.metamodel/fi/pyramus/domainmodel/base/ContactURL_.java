package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ContactURL.class)
public abstract class ContactURL_ {

	public static volatile SingularAttribute<ContactURL, Long> id;
	public static volatile SingularAttribute<ContactURL, ContactInfo> contactInfo;
	public static volatile SingularAttribute<ContactURL, ContactURLType> contactURLType;
	public static volatile SingularAttribute<ContactURL, String> url;
	public static volatile SingularAttribute<ContactURL, Long> version;

}

