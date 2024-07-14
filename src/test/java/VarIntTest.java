import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import sqlite.buffer.VarInt;


public class VarIntTest {

  @Test
  void testVarInt() {

    assertEquals(127, VarInt.parse(ByteBuffer.wrap(new byte[] {
        (byte) 0b0111_1111
    })).value());

    assertEquals(16382,VarInt.parse(ByteBuffer.wrap(new byte[] {
        (byte) 0b1111_1111,
        (byte) 0b0111_1110,
    })).value());

    assertEquals(2097150, VarInt.parse(ByteBuffer.wrap(new byte[] {
        (byte) 0b1111_1111,
        (byte) 0b1111_1111,
        (byte) 0b0111_1110,
    })).value());

  }
}






