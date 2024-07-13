import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

public enum PageType {
  InteriorIndex(0x02),
  InteriorTable(0x05),
  LeafIndex(0x0a),
  LeafTable(0x0d);

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