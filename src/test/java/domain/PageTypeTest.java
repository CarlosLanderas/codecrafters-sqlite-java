package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


import sqlite.domain.PageType;

public class PageTypeTest {
    
    @Test
    void parse() {
        assertEquals(PageType.InteriorTable, PageType.from(0x05));
        assertEquals(PageType.InteriorIndex, PageType.from(0x02));
        assertEquals(PageType.LeafTable, PageType.from(0x0d));
        assertEquals(PageType.LeafIndex, PageType.from(0x0a));
    }
}
