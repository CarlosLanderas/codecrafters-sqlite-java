package sqlite.domain;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sqlite.database.DatabaseHeader;

public class PageReader {
  public static Page read(int pageNumber, DatabaseHeader dbHeader, ByteBuffer buf) throws IOException {

    var pageBuf = pageBuffer(buf, dbHeader.pageSize(), pageNumber);

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

    return new Page(pageNumber, header,cells);
  }

  private static ByteBuffer pageBuffer(ByteBuffer buffer, int pageSize, int pageNumber) {
    var pageOffset = pageNumber == 1 ? 100 : 0;
    var offset = pageOffset + (pageNumber - 1) * pageSize;

    return buffer.slice(offset, pageSize);
  }
}
