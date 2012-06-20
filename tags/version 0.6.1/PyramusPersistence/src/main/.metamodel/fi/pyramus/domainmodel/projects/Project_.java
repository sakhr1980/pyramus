package fi.pyramus.domainmodel.projects;

import fi.pyramus.domainmodel.base.EducationalLength;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Project.class)
public abstract class Project_ {

	public static volatile SetAttribute<Project, Tag> tags;
	public static volatile SingularAttribute<Project, Long> id;
	public static volatile SingularAttribute<Project, Boolean> archived;
	public static volatile SingularAttribute<Project, Date> lastModified;
	public static volatile ListAttribute<Project, ProjectModule> projectModules;
	public static volatile SingularAttribute<Project, Date> created;
	public static volatile SingularAttribute<Project, EducationalLength> optionalStudiesLength;
	public static volatile SingularAttribute<Project, String> description;
	public static volatile SingularAttribute<Project, String> name;
	public static volatile SingularAttribute<Project, User> lastModifier;
	public static volatile SingularAttribute<Project, Long> version;
	public static volatile SingularAttribute<Project, User> creator;

}

