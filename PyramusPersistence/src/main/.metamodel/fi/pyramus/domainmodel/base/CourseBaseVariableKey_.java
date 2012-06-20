package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseBaseVariableKey.class)
public abstract class CourseBaseVariableKey_ {

	public static volatile SingularAttribute<CourseBaseVariableKey, VariableType> variableType;
	public static volatile SingularAttribute<CourseBaseVariableKey, Long> id;
	public static volatile SingularAttribute<CourseBaseVariableKey, String> variableName;
	public static volatile SingularAttribute<CourseBaseVariableKey, String> variableKey;
	public static volatile SingularAttribute<CourseBaseVariableKey, Long> version;
	public static volatile SingularAttribute<CourseBaseVariableKey, Boolean> userEditable;

}

