package fi.pyramus.persistence.usertypes;

public enum CourseOptionality {
  MANDATORY       (0),
  OPTIONAL        (1);
  
  private CourseOptionality(int value) {
    this.value = value;
  }
  
  public int getValue() {
    return value;
  }
  
  public static CourseOptionality getOptionality(int value) {
    for (CourseOptionality optionality : values()) {
      if (optionality.getValue() == value) {
        return optionality;
      }
    }
    return MANDATORY;
  }
  
  private int value;
}
