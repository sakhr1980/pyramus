package fi.pyramus.domainmodel.students;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentImage.class)
public abstract class StudentImage_ {

	public static volatile SingularAttribute<StudentImage, Long> id;
	public static volatile SingularAttribute<StudentImage, Student> student;
	public static volatile SingularAttribute<StudentImage, byte[]> data;
	public static volatile SingularAttribute<StudentImage, String> contentType;

}

