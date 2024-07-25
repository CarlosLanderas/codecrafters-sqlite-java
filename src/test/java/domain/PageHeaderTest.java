package domain;

import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import sqlite.domain.PageHeader;
import sqlite.domain.PageType;

public class PageHeaderTest {

    @Test
    void parseLeaf() {

        byte pageType = (byte)0x0d;
        short blockStart = 358;
        short numCells = 155;
        short contentStart = 200;
        byte fragmentedBytes =  0;

        var buf = ByteBuffer.allocate(100);
        buf.put(pageType);
        buf.putShort(blockStart);
        buf.putShort(numCells);
        buf.putShort(contentStart);
        buf.putShort(fragmentedBytes);
    
        var header = PageHeader.parse(buf.rewind());

        assertEquals(PageType.LeafTable, header.pageType());
        assertEquals(blockStart, header.freeBlockStart());
        assertEquals(numCells, header.numCells());
        assertEquals(contentStart, header.cellContentStart());
        assertEquals(fragmentedBytes, header.fragmentedBytes());
    }

    @Test
    void parseInterior() {

        byte pageType = (byte)0x05;
        short blockStart = 358;
        short numCells = 155;
        short contentStart = 200;
        byte fragmentedBytes =  0;
        int rightPointer = 500;

        var buf = ByteBuffer.allocate(100);
        buf.put(pageType);
        buf.putShort(blockStart);
        buf.putShort(numCells);
        buf.putShort(contentStart);
        buf.put(fragmentedBytes);
        buf.putInt(rightPointer);

        var header = PageHeader.parse(buf.rewind());

        assertEquals(PageType.InteriorTable, header.pageType());
        assertEquals(blockStart, header.freeBlockStart());
        assertEquals(numCells, header.numCells());
        assertEquals(contentStart, header.cellContentStart());
        assertEquals(fragmentedBytes, header.fragmentedBytes());
        assertEquals(rightPointer, header.rightMostPointer());
    }
}
