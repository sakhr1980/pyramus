package fi.pyramus.domainmodel.students;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentVariable.class)
public abstract class StudentVariable_ {

	public static volatile SingularAttribute<StudentVariable, Long> id;
	public static volatile SingularAttribute<StudentVariable, Student> student;
	public static volatile SingularAttribute<StudentVariable, String> value;
	public static volatile SingularAttribute<StudentVariable, StudentVariableKey> key;
	public static volatile SingularAttribute<StudentVariable, Long> version;

}

