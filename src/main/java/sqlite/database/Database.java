package sqlite.database;

import static java.util.stream.Collectors.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.List;

import sqlite.buffer.BytesChannelReader;
import sqlite.domain.Page;
import sqlite.domain.PageReader;
import sqlite.domain.Table;
import sqlite.domain.Record;

public class Database {

  private final DatabaseHeader header;
  private final SeekableByteChannel channel;
  private final Schema schema;

  public Database(SeekableByteChannel channel) throws IOException {
    this.header = DatabaseHeader.parse(BytesChannelReader.read(channel, 0, 100));
    this.channel = channel;
    var schemaTables = getSchema(PageReader.read(1, header, channel));

    var tables = schemaTables.stream().filter(Table::isTable).toList();
    var indexes = schemaTables.stream().filter(Table::isIndex).toList();
    this.schema = new Schema(tables, indexes);
  }

  public DatabaseHeader getHeader() {
    return this.header;
  }

  public Schema getSchema() {
    return this.schema;
  }

  public SeekableByteChannel byteChannel() {
    return this.channel;
  }

  private List<Table> getSchema(Page rootPage) {
    var tables = rootPage.getCells()
        .stream()
        .map(Record::parse)
        .map(Table::fromRecord)
        .collect(toList());

    return tables;
  }
}
