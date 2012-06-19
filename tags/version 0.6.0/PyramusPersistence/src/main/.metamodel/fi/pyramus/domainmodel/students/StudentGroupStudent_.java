package fi.pyramus.domainmodel.students;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentGroupStudent.class)
public abstract class StudentGroupStudent_ {

	public static volatile SingularAttribute<StudentGroupStudent, Long> id;
	public static volatile SingularAttribute<StudentGroupStudent, Student> student;
	public static volatile SingularAttribute<StudentGroupStudent, StudentGroup> studentGroup;
	public static volatile SingularAttribute<StudentGroupStudent, Long> version;

}

