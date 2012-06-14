package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudyProgramme.class)
public abstract class StudyProgramme_ {

	public static volatile SingularAttribute<StudyProgramme, Long> id;
	public static volatile SingularAttribute<StudyProgramme, StudyProgrammeCategory> category;
	public static volatile SingularAttribute<StudyProgramme, Boolean> archived;
	public static volatile SingularAttribute<StudyProgramme, String> name;
	public static volatile SingularAttribute<StudyProgramme, String> code;
	public static volatile SingularAttribute<StudyProgramme, Long> version;

}

