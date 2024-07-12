import java.nio.ByteBuffer;

public record VarInt(int value, int size) {

  public static VarInt parse(ByteBuffer src) {
    int result = 0;
    int shift = 0;
    int tmp;
    int size = 0;

    do {
      tmp = src.get();
      size++;
      result |= (tmp & 0x7F) << shift;
      if (tmp >= 0) {
        return new VarInt(result, size);
      }
      shift += 7;
    } while (shift < 32);

    // Handle overflow scenario
    while ((src.get()) < 0) {
      size++;
    }
    size++; // for the last byte read

    return new VarInt(result, size);
  }

  public static int intSize(int i) {
    int result = 0;
    do {
      result++;
      i >>>= 7;
    } while (i != 0);
    return result;
  }

  public static void putInt(int v, byte[] sink) {
    int pos = 0;
    do {
      // Encode next 7 bits + terminator bit
      int bits = v & 0x7F;
      v >>>= 7;
      byte b = (byte) (bits + ((v != 0) ? 0x80 : 0));
      sink[pos++] = b;
    } while (v != 0);
  }
}
