package fi.pyramus.domainmodel.users;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(UserVariable.class)
public abstract class UserVariable_ {

	public static volatile SingularAttribute<UserVariable, Long> id;
	public static volatile SingularAttribute<UserVariable, String> value;
	public static volatile SingularAttribute<UserVariable, User> user;
	public static volatile SingularAttribute<UserVariable, UserVariableKey> key;
	public static volatile SingularAttribute<UserVariable, Long> version;

}

