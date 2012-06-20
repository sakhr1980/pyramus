package fi.pyramus.domainmodel.grading;

import fi.pyramus.domainmodel.projects.StudentProject;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ProjectAssessment.class)
public abstract class ProjectAssessment_ extends fi.pyramus.domainmodel.grading.Credit_ {

	public static volatile SingularAttribute<ProjectAssessment, StudentProject> studentProject;

}

