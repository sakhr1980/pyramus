package fi.pyramus.domainmodel.users;

import fi.pyramus.domainmodel.base.VariableType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(UserVariableKey.class)
public abstract class UserVariableKey_ {

	public static volatile SingularAttribute<UserVariableKey, VariableType> variableType;
	public static volatile SingularAttribute<UserVariableKey, Long> id;
	public static volatile SingularAttribute<UserVariableKey, String> variableName;
	public static volatile SingularAttribute<UserVariableKey, String> variableKey;
	public static volatile SingularAttribute<UserVariableKey, Long> version;
	public static volatile SingularAttribute<UserVariableKey, Boolean> userEditable;

}

