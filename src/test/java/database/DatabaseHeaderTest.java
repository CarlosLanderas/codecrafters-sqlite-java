package database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import sqlite.database.DatabaseHeader;
import sqlite.domain.TextEncoding;

public class DatabaseHeaderTest {
    
    @Test
    void parse() throws IOException {

        var buf = ByteBuffer.allocate(100);

        var hString = "sqlite header123".getBytes();
        short pageSize = 4096;
        byte fileWriteVersion = 1;
        byte fileReadVersion = 1;
        int numPages = 4;
        int encoding = 1;

        buf.put(hString);
        buf.putShort(pageSize);
        buf.put(fileWriteVersion);
        buf.put(fileReadVersion);

        buf.position(28);
        buf.putInt(numPages);
        buf.position(56);
        buf.putInt(encoding);
        
        var header = DatabaseHeader.parse(buf.rewind());

        assertEquals(new String(hString), header.headerString());
        assertEquals(pageSize, header.pageSize());
        assertEquals(fileWriteVersion, header.fileFormatWriteVersion());
        assertEquals(fileReadVersion, header.fileFormatReadVersion());
        assertEquals(numPages, header.pageCount());
        assertEquals(TextEncoding.Utf8, header.encoding());
    }
}
