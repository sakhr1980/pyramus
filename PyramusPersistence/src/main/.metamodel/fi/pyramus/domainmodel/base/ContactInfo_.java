package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ContactInfo.class)
public abstract class ContactInfo_ {

	public static volatile SingularAttribute<ContactInfo, Long> id;
	public static volatile ListAttribute<ContactInfo, ContactURL> contactURLs;
	public static volatile ListAttribute<ContactInfo, PhoneNumber> phoneNumbers;
	public static volatile ListAttribute<ContactInfo, Email> emails;
	public static volatile ListAttribute<ContactInfo, Address> addresses;
	public static volatile SingularAttribute<ContactInfo, String> additionalInfo;
	public static volatile SingularAttribute<ContactInfo, Long> version;

}

