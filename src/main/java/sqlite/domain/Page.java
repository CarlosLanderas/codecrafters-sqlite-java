package sqlite.domain;

import java.util.Collection;
import java.util.List;

public record Page(
        long base,
        PageHeader header,
        List<Cell> cells
) {

    public List<Cell> getCells() {
        return this.cells;
    }

    public List<Record> getRecords() {
        return this.cells.stream().map(Record::parse).toList();
    }
}





