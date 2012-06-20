package fi.pyramus.domainmodel.help;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(HelpPage.class)
public abstract class HelpPage_ extends fi.pyramus.domainmodel.help.HelpItem_ {

	public static volatile ListAttribute<HelpPage, HelpPageContent> contents;

}

