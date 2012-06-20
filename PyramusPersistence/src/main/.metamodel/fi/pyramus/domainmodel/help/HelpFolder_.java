package fi.pyramus.domainmodel.help;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(HelpFolder.class)
public abstract class HelpFolder_ extends fi.pyramus.domainmodel.help.HelpItem_ {

	public static volatile ListAttribute<HelpFolder, HelpItem> children;

}

