package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ContactType.class)
public abstract class ContactType_ {

	public static volatile SingularAttribute<ContactType, Long> id;
	public static volatile SingularAttribute<ContactType, Boolean> archived;
	public static volatile SingularAttribute<ContactType, String> name;
	public static volatile SingularAttribute<ContactType, Long> version;

}

