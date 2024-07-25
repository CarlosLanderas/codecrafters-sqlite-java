package sqlite.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sqlite.database.Database;
import sqlite.domain.*;
import sqlite.query.SelectQueryPredicate;

public class IndexRowReader implements Iterator<TableRow> {

    private final Database db;
    private final Table index;
    private final Table table;
    private final SelectQueryPredicate queryPredicate;
    private final IndexSearch indexSearch;
    private List<IndexedRecord> indexRecords = new ArrayList<>();
    private List<TableRow> rows = null;
    private int position = 0;

    public IndexRowReader(Database database, Table index,
                          SelectQueryPredicate queryPredicate) throws IOException {
        this.db = database;
        this.index = index;
        this.table = db.getSchema().findTable(index.name()).get();
        this.queryPredicate = queryPredicate;
        this.indexSearch = new IndexSearch(db);
        this.initializeIndex();
    }

    private void initializeIndex() throws IOException {
        var page = PageReader.read(index.rootPage(), db.getHeader(), db.byteChannel());
        indexSearch.query(page, indexRecords, queryPredicate);
    }

    @Override
    public boolean hasNext() {
        return position < indexRecords.size();
    }

    @Override
    public TableRow next() {

        if(rows == null) {
            try {
                var page = PageReader.read(table.rootPage(), db.getHeader(), db.byteChannel());
                rows = indexSearch.getResults(page, indexRecords, queryPredicate);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        var row =  rows.get(position);
        position++;

        return row;
    }

}
