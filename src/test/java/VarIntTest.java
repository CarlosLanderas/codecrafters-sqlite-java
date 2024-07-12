import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;


public class VarIntTest {

  @Test
  void parse() {

    var num = 60000;
    var length = VarInt.intSize(num);
    var data = ByteBuffer.allocate(length);

    VarInt.putInt(num, data.array());

    var r = VarInt.parse(data);

    assertEquals(r.value(), 60000);
    assertEquals(r.size(), 3);
  }
}






