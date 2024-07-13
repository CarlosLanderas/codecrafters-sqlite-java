import java.nio.ByteBuffer;

public record PageHeader(
    PageType pageType,
    short freeBlockStart,
    short numCells,
    short cellContentStart,
    byte fragmentedBytes,
    int rightMostPointer
){
  public static PageHeader parse(ByteBuffer buf) {
    var pageType = PageType.from(buf.get() & 0xFF);
    var freeBlockStart = buf.getShort();
    var numCells = buf.getShort();
    var cellContentStart = buf.getShort();
    var fragmentedBytes = buf.get();
    int rightMostPointer = 0;

    if (pageType == PageType.InteriorTable || pageType == PageType.InteriorIndex) {
      rightMostPointer = buf.getInt();
    }

    return new PageHeader(
        pageType,
        freeBlockStart,
        numCells,
        cellContentStart,
        fragmentedBytes,
        rightMostPointer
    );
  }
}