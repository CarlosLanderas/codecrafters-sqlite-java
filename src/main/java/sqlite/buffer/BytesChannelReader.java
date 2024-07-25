package sqlite.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public class BytesChannelReader {

  public static ByteBuffer read(SeekableByteChannel channel, long position, int length)
      throws IOException {

    channel.position(position);
    var buf = ByteBuffer.allocate(length);
    channel.read(buf);
    buf.rewind();

    return buf;
  }

}
