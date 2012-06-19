package fi.pyramus.domainmodel.modules;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ModuleComponent.class)
public abstract class ModuleComponent_ extends fi.pyramus.domainmodel.base.ComponentBase_ {

	public static volatile SingularAttribute<ModuleComponent, Module> module;

}

