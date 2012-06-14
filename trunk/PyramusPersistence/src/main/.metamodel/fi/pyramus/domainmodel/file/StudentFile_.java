package fi.pyramus.domainmodel.file;

import fi.pyramus.domainmodel.students.Student;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentFile.class)
public abstract class StudentFile_ extends fi.pyramus.domainmodel.file.File_ {

	public static volatile SingularAttribute<StudentFile, Student> student;

}

