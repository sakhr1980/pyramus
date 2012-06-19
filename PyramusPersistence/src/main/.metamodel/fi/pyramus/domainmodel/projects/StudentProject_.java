package fi.pyramus.domainmodel.projects;

import fi.pyramus.domainmodel.base.CourseOptionality;
import fi.pyramus.domainmodel.base.EducationalLength;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentProject.class)
public abstract class StudentProject_ {

	public static volatile SetAttribute<StudentProject, Tag> tags;
	public static volatile SingularAttribute<StudentProject, Date> lastModified;
	public static volatile SingularAttribute<StudentProject, EducationalLength> optionalStudiesLength;
	public static volatile SingularAttribute<StudentProject, Long> version;
	public static volatile SingularAttribute<StudentProject, User> creator;
	public static volatile SingularAttribute<StudentProject, Long> id;
	public static volatile ListAttribute<StudentProject, StudentProjectModule> studentProjectModules;
	public static volatile SingularAttribute<StudentProject, Project> project;
	public static volatile SingularAttribute<StudentProject, Student> student;
	public static volatile SingularAttribute<StudentProject, Boolean> archived;
	public static volatile SingularAttribute<StudentProject, Date> created;
	public static volatile SingularAttribute<StudentProject, String> description;
	public static volatile SingularAttribute<StudentProject, String> name;
	public static volatile SingularAttribute<StudentProject, User> lastModifier;
	public static volatile SingularAttribute<StudentProject, CourseOptionality> optionality;

}

