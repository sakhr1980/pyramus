package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudyProgrammeCategory.class)
public abstract class StudyProgrammeCategory_ {

	public static volatile SingularAttribute<StudyProgrammeCategory, Long> id;
	public static volatile SingularAttribute<StudyProgrammeCategory, EducationType> educationType;
	public static volatile SingularAttribute<StudyProgrammeCategory, Boolean> archived;
	public static volatile SingularAttribute<StudyProgrammeCategory, String> name;
	public static volatile SingularAttribute<StudyProgrammeCategory, Long> version;

}

