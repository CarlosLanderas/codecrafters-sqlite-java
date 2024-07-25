package sqlite.domain;

import java.security.InvalidParameterException;

public enum PageType {
  InteriorTable(0x05),
  LeafTable(0x0d),
  LeafIndex(0x0a),
  InteriorIndex(0x02);


  private int type;

  PageType(int type) {
    this.type = type;
  }

  public static PageType from(int type) {
    for(var value : values()) {
      if (value.type == type) {
        return value;
      }
    }

    throw new InvalidParameterException("Invalid pageType: " + type);
  }
}