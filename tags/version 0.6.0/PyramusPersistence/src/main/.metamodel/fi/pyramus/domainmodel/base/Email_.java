package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Email.class)
public abstract class Email_ {

	public static volatile SingularAttribute<Email, Long> id;
	public static volatile SingularAttribute<Email, ContactInfo> contactInfo;
	public static volatile SingularAttribute<Email, String> address;
	public static volatile SingularAttribute<Email, Boolean> defaultAddress;
	public static volatile SingularAttribute<Email, ContactType> contactType;
	public static volatile SingularAttribute<Email, Long> version;

}

