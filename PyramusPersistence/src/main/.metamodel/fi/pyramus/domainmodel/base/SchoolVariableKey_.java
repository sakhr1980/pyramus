package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SchoolVariableKey.class)
public abstract class SchoolVariableKey_ {

	public static volatile SingularAttribute<SchoolVariableKey, VariableType> variableType;
	public static volatile SingularAttribute<SchoolVariableKey, Long> id;
	public static volatile SingularAttribute<SchoolVariableKey, String> variableName;
	public static volatile SingularAttribute<SchoolVariableKey, String> variableKey;
	public static volatile SingularAttribute<SchoolVariableKey, Long> version;
	public static volatile SingularAttribute<SchoolVariableKey, Boolean> userEditable;

}

