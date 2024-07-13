import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Database {

  private final DatabaseHeader header;
  private final ByteBuffer dbBuff;
  private final Schema schema;

  public Database(ByteBuffer dbBuff) throws IOException {
    this.dbBuff = dbBuff;
    this.header = DatabaseHeader.parse(dbBuff);
    this.schema = new Schema(getSchema(getPage(1)));

//    schema.tables.forEach(t -> {
//      try {
//        var page = getPage((int) t.rootPage());
//        for (var c : page.getCells()) {
//          var r = Record.parse(c);
//          var table = Table.fromRecord(r);
//          System.out.println(table);
//        }
//      } catch (IOException e) {
//        throw new RuntimeException(e);
//      }
//    });
  }

  public DatabaseHeader getHeader() {
    return this.header;
  }

  public Schema getSchema() {
    return this.schema;
  }

  private List<Table> getSchema(Page rootPage) {
    var tables = rootPage.getCells()
        .stream()
        .map(Record::parse)
        .filter(Record::isTable)
        .map(Table::fromRecord)
        .collect(toList());

    return tables;
  }

  Page getPage(int pageNumber) throws IOException {

    var pageBuf = pageBuffer(dbBuff, header.pageSize(), pageNumber);

    PageHeader header = PageHeader.parse(pageBuf);

    var cellOffsets = new int[header.numCells()];
    for (var i = 0; i < header.numCells(); i++) {
      cellOffsets[i] = pageBuf.getShort() & 0xFFFF;
    }

    var cells = new ArrayList<Cell>();
    for (var offset : cellOffsets) {
      if (pageNumber == 1) {
        offset -= 100;
      }

      pageBuf.position(offset);
      cells.add(Cell.parse(header.pageType(), pageBuf));
    }

    return new Page(pageNumber, header, Collections.unmodifiableCollection(cells));
  }

  private ByteBuffer pageBuffer(ByteBuffer buffer, int pageSize, int pageNumber) {
    var pageOffset = pageNumber == 1 ? 100 : 0;
    var offset = pageOffset + (pageNumber - 1) * pageSize;

    return buffer.slice(offset, pageSize);
  }

  record Schema(List<Table> tables) { }
}
