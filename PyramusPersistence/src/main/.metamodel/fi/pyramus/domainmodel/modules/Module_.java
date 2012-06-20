package fi.pyramus.domainmodel.modules;

import fi.pyramus.domainmodel.base.Tag;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Module.class)
public abstract class Module_ extends fi.pyramus.domainmodel.base.CourseBase_ {

	public static volatile SetAttribute<Module, Tag> tags;
	public static volatile ListAttribute<Module, ModuleComponent> moduleComponents;

}

