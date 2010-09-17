package fi.pyramus.persistence.usertypes;

public enum StudentProjectModuleOptionality {
  MANDATORY       (0),
  OPTIONAL        (1);
  
  private StudentProjectModuleOptionality(int value) {
    this.value = value;
  }
  
  public int getValue() {
    return value;
  }
  
  public static StudentProjectModuleOptionality getOptionality(int value) {
    for (StudentProjectModuleOptionality optionality : values()) {
      if (optionality.getValue() == value) {
        return optionality;
      }
    }
    return MANDATORY;
  }
  
  private int value;
}
