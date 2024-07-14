package sqlite.database;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import sqlite.domain.Page;
import sqlite.domain.PageReader;
import sqlite.domain.Table;
import sqlite.domain.Record;

public class Database {

  private final DatabaseHeader header;
  private final ByteBuffer dbBuff;
  private final Schema schema;

  public Database(ByteBuffer dbBuff) throws IOException {
    this.dbBuff = dbBuff;
    this.header = DatabaseHeader.parse(dbBuff);
    this.schema = new Schema(getSchema(PageReader.read(1, header, dbBuff)));
  }

  public DatabaseHeader getHeader() {
    return this.header;
  }

  public Schema getSchema() {
    return this.schema;
  }

  public ByteBuffer getBuffer() {
    return this.dbBuff;
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
}
