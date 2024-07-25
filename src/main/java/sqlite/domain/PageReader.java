package sqlite.domain;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import sqlite.buffer.BytesChannelReader;
import sqlite.database.DatabaseHeader;

public class PageReader {

  public static Page read(long pageNumber, DatabaseHeader dbHeader, SeekableByteChannel channel)
      throws IOException {

    var pageBuf = pageBuffer(channel, dbHeader.pageSize(), pageNumber);

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

    return new Page(pageNumber, header, cells);
  }


  private static ByteBuffer pageBuffer(SeekableByteChannel chan, int pageSize, long pageNumber)
      throws IOException {
    var pageOffset = pageNumber == 1 ? 100 : 0;
    long offset = pageOffset + (pageNumber - 1) * pageSize;

    return BytesChannelReader.read(chan, offset, pageSize);
  }
}
