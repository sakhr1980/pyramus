package fi.pyramus.domainmodel.projects;

import fi.pyramus.domainmodel.modules.Module;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ProjectModule.class)
public abstract class ProjectModule_ {

	public static volatile SingularAttribute<ProjectModule, Long> id;
	public static volatile SingularAttribute<ProjectModule, Project> project;
	public static volatile SingularAttribute<ProjectModule, Module> module;
	public static volatile SingularAttribute<ProjectModule, ProjectModuleOptionality> optionality;
	public static volatile SingularAttribute<ProjectModule, Long> version;

}

