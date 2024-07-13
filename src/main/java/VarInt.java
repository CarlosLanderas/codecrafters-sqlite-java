import java.nio.ByteBuffer;

public record VarInt(long value, int size) {

  public static VarInt parse(ByteBuffer buff) {
    long result = 0L;
    int bytesRead = 0;
    for (int i = 0; i < 9; ++i) {
      byte b = buff.get();
      bytesRead++;
      result = (result << 7) + (b & 0x7f);
      if (((b >> 7) & 1) == 0) {
        break;
      }
    }
    return new VarInt(result, bytesRead);
  }
}
