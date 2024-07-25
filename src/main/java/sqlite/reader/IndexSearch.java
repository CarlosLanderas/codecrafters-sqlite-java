package sqlite.reader;

import sqlite.database.Database;
import sqlite.domain.*;
import sqlite.domain.Record;
import sqlite.query.SelectQueryPredicate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

record IndexedRecord(Record record) {
    public static IndexedRecord of(Record record) {
        return new IndexedRecord(record);
    }
}

public class IndexSearch {

    private final Database db;

    public IndexSearch(Database db) {
        this.db = db;
    }

    public void query(Page page, List<IndexedRecord> queryRecords, SelectQueryPredicate predicate) throws IOException {
        var searchValue = predicate.filterValue();
        var cellKeys = page.getRecords().stream().map(r -> r.values().get(0).toString()).toList();

        if (page.header().pageType() == PageType.InteriorIndex) {
            var records = page.getRecords();
            for (var i = 0; i < cellKeys.size(); i++) {
                var cellKey = cellKeys.get(i);
                if (searchValue.compareTo(cellKey) <= 0) {
                    if (searchValue.equals(cellKey)) {
                        queryRecords.add(IndexedRecord.of(records.get(i)));
                    }
                    var childPage = getPage(page.cells().get(i).leftChild());
                    query(childPage, queryRecords, predicate);
                }
            }
            if (searchValue.compareTo(cellKeys.get(cellKeys.size() - 1)) > 0) {
                var rightPage = getPage(page.header().rightMostPointer());
                query(rightPage, queryRecords, predicate);
            }
        } else {
            var records = page.getRecords();

            for (int i = 0; i < cellKeys.size(); ++i) {
                var cellKey = cellKeys.get(i);

                if (searchValue.compareTo(cellKey) <= 0) {
                    if (searchValue.equals(cellKey)) {
                        queryRecords.add(IndexedRecord.of(records.get(i)));
                    }
                }
            }
        }
    }

    protected List<TableRow> getResults(Page page, List<IndexedRecord> indexRecords, SelectQueryPredicate predicate) throws IOException {
        var results = new ArrayList<TableRow>();
        for(var key: indexRecords) {
         var searchResult = indexedSearch(page, key.record(), predicate);
          results.add(new TableRow(searchResult.record().values(), searchResult.record().rowId()));
        }

        return results.stream()
                .map(r -> new TableRow(r.values(), r.rowId()))
                .toList();
    }

    private IndexedRecord indexedSearch(Page page, Record record, SelectQueryPredicate predicate) throws IOException {
        var pageType = page.header().pageType();
        var cellKeys = page.cells().stream().map(Cell::rowId).toList();
        var searchValue = Long.valueOf(record.values().get(1).toString());

        if (pageType == PageType.InteriorTable) {

            for (int i = 0; i < cellKeys.size(); ++i) {
                if(searchValue <= cellKeys.get(i)){
                    var childPage = getPage(page.cells().get(i).leftChild());
                    return indexedSearch(childPage, record, predicate);
                }
            }
            if(searchValue >= cellKeys.get(cellKeys.size()-1)){
                var rightMostPage = getPage(page.header().rightMostPointer());
                return indexedSearch(rightMostPage, record, predicate);
            }

        } else { 
        for (int i = 0; i < cellKeys.size(); ++i) {
            if (searchValue.equals(cellKeys.get(i))) {
                return new IndexedRecord(page.getRecords().get(i));
            }
        }
    }

        return null;
    }

    private Page getPage(long pageNumber) throws IOException {
        return PageReader.read(pageNumber, db.getHeader(), db.byteChannel());
    }
}
