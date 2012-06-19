package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SchoolVariable.class)
public abstract class SchoolVariable_ {

	public static volatile SingularAttribute<SchoolVariable, Long> id;
	public static volatile SingularAttribute<SchoolVariable, School> school;
	public static volatile SingularAttribute<SchoolVariable, String> value;
	public static volatile SingularAttribute<SchoolVariable, SchoolVariableKey> key;
	public static volatile SingularAttribute<SchoolVariable, Long> version;

}

