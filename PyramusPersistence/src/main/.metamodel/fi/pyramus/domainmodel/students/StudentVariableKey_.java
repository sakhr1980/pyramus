package fi.pyramus.domainmodel.students;

import fi.pyramus.domainmodel.base.VariableType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentVariableKey.class)
public abstract class StudentVariableKey_ {

	public static volatile SingularAttribute<StudentVariableKey, VariableType> variableType;
	public static volatile SingularAttribute<StudentVariableKey, Long> id;
	public static volatile SingularAttribute<StudentVariableKey, String> variableName;
	public static volatile SingularAttribute<StudentVariableKey, String> variableKey;
	public static volatile SingularAttribute<StudentVariableKey, Long> version;
	public static volatile SingularAttribute<StudentVariableKey, Boolean> userEditable;

}

