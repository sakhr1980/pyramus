package fi.pyramus.domainmodel.projects;

import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.base.CourseOptionality;
import fi.pyramus.domainmodel.modules.Module;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentProjectModule.class)
public abstract class StudentProjectModule_ {

	public static volatile SingularAttribute<StudentProjectModule, Long> id;
	public static volatile SingularAttribute<StudentProjectModule, Module> module;
	public static volatile SingularAttribute<StudentProjectModule, CourseOptionality> optionality;
	public static volatile SingularAttribute<StudentProjectModule, AcademicTerm> academicTerm;
	public static volatile SingularAttribute<StudentProjectModule, Long> version;
	public static volatile SingularAttribute<StudentProjectModule, StudentProject> studentProject;

}

