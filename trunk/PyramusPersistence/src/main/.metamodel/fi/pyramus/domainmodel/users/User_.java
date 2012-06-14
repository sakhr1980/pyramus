package fi.pyramus.domainmodel.users;

import fi.pyramus.domainmodel.base.BillingDetails;
import fi.pyramus.domainmodel.base.ContactInfo;
import fi.pyramus.domainmodel.base.Tag;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(User.class)
public abstract class User_ {

	public static volatile SetAttribute<User, Tag> tags;
	public static volatile SingularAttribute<User, Long> id;
	public static volatile SingularAttribute<User, String> lastName;
	public static volatile SingularAttribute<User, String> title;
	public static volatile SingularAttribute<User, ContactInfo> contactInfo;
	public static volatile SingularAttribute<User, Role> role;
	public static volatile SingularAttribute<User, String> firstName;
	public static volatile ListAttribute<User, BillingDetails> billingDetails;
	public static volatile SingularAttribute<User, String> externalId;
	public static volatile SingularAttribute<User, String> authProvider;
	public static volatile SingularAttribute<User, Long> version;
	public static volatile ListAttribute<User, UserVariable> variables;

}

