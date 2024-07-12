import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;

public class Database {

  private final Header header;
  private final ByteBuffer dbBuff;
  int rootPageCellCount = 0; // Temporal since page type is 13

  public Database(ByteBuffer dbBuff) throws IOException {
    this.dbBuff = dbBuff;
    this.header = readHeader();
   }

  public Header getHeader() {
    return header;
  }

  Page getPage(int pageNumber) throws IOException {
    var pagePosition = 100 + (header.pageSize * pageNumber);
    dbBuff.position(pagePosition);

    var pageType = dbBuff.get();

    dbBuff.position(pagePosition + 3);

    var numberOfCells = dbBuff.getShort();
    var cellContentStart = dbBuff.getShort();

    dbBuff.position(pagePosition + 8);

    var contents = new int[numberOfCells];

    for (var i = 0; i < numberOfCells; i++) {
      contents[i] = dbBuff.getShort();
    }

    for (var i = 0; i < contents.length; i++) {

      dbBuff.position(contents[i]);
      var payloadL = VarInt.parse(dbBuff);
      var rowId = VarInt.parse(dbBuff);

      var b = new byte[payloadL.value()];
      dbBuff.get(b);

      //System.out.println(new String(b));
    }

    var pHeader = new Page.PageHeader(pageType, numberOfCells, cellContentStart);

    var page = new Page(pHeader, pageNumber);

    return page;
  }

  private Header readHeader() throws IOException {
    dbBuff.position(0);

    short pageSize = dbBuff.position(16).getShort();
    int pageCount = dbBuff.position(28).getInt();

    int n = dbBuff.position(56).getInt();

    TextEncoding encoding = switch (n) {
      case 1 -> TextEncoding.Utf8;
      case 2 -> TextEncoding.Utf16;
      case 3 -> TextEncoding.Utf32;
      default -> TextEncoding.Utf8;
    };

    return new Header(pageSize, pageCount, encoding);
  }

  public enum TextEncoding {
    Utf8, Utf16, Utf32
  }

  public record Header(short pageSize, int pageCount, TextEncoding encoding) {

  }
}
