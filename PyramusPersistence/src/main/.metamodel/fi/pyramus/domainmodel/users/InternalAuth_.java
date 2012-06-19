package fi.pyramus.domainmodel.users;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(InternalAuth.class)
public abstract class InternalAuth_ {

	public static volatile SingularAttribute<InternalAuth, Long> id;
	public static volatile SingularAttribute<InternalAuth, String> username;
	public static volatile SingularAttribute<InternalAuth, String> password;
	public static volatile SingularAttribute<InternalAuth, Long> version;

}

