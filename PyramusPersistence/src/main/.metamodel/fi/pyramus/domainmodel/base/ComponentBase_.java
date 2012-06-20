package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ComponentBase.class)
public abstract class ComponentBase_ {

	public static volatile SingularAttribute<ComponentBase, Long> id;
	public static volatile SingularAttribute<ComponentBase, Boolean> archived;
	public static volatile SingularAttribute<ComponentBase, String> description;
	public static volatile SingularAttribute<ComponentBase, String> name;
	public static volatile SingularAttribute<ComponentBase, EducationalLength> length;
	public static volatile SingularAttribute<ComponentBase, Long> version;

}

