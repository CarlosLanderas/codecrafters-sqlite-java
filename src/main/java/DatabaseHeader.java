

import java.nio.ByteBuffer;

public record DatabaseHeader(
    String headerString,
    int pageSize,
    byte fileFormatWriteVersion,
    byte fileFormatReadVersion,
    int pageCount,
    TextEncoding encoding
) {

  public static DatabaseHeader parse(ByteBuffer buf) {
    buf.position(0);

    var headerBytes = new byte[16];
    buf.get(headerBytes);

    String headerString = new String(headerBytes);
    short pageSize = buf.getShort();
    pageSize = (short) (pageSize == 1 ? 65536 : pageSize);

    byte fileWriteVersion = buf.get();
    byte fileReadVersion = buf.get();

    int pageCount = buf.position(28).getInt() & 0xFFFF;
    int encoding = buf.position(56).getInt() & 0xFFFF;

    TextEncoding textEncoding = switch (encoding) {
      case 1 -> TextEncoding.Utf8;
      case 2 -> TextEncoding.Utf16;
      case 3 -> TextEncoding.Utf32;
      default -> TextEncoding.Utf8;
    };

    return new DatabaseHeader(
        headerString,
        pageSize,
        fileWriteVersion,
        fileReadVersion,
        pageCount,
        textEncoding);
  }
}