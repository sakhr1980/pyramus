package fi.pyramus.updater.core;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class UpdateVersion {

  public UpdateVersion(int major, int minor) {
    this.major = major;
    this.minor = minor;
  }
  
  public int getMinor() {
    return minor;
  }
  
  public int getMajor() {
    return major;
  }
  
  public boolean isNewerThan(UpdateVersion version) {
    return toFloat() > version.toFloat();
  }
  
  @Override
  public String toString() {
    return String.valueOf(getMajor()) + '.' + String.valueOf(getMinor());
  }

  public float toFloat() {
    return Float.valueOf(toString());
  }
  
  public static UpdateVersion parseVersion(String string) {
    if (!StringUtils.isBlank(string)) {
      String[] tmp = string.split("\\.");
      if (tmp.length == 2 && StringUtils.isNumeric(tmp[0]) && StringUtils.isNumeric(tmp[1])) {
        return new UpdateVersion(NumberUtils.createInteger(tmp[0]), NumberUtils.createInteger(tmp[1]));
      }
    }
    
    return null;
  }
  
  private int minor;
  private int major;
}
