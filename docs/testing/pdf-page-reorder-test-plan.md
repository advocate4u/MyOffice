# PDF page reorder acceptance tests

| ID | Scenario | Expected result |
|---|---|---|
| REORDER-01 | Open a multi-page PDF and choose Reorder / select pages | Page-order dialog opens |
| REORDER-02 | Enter `3,1,2` | New PDF pages appear in that order |
| REORDER-03 | Enter a single page number | New PDF containing that page is created |
| REORDER-04 | Enter duplicate numbers such as `1,1` | Validation message appears and no output is created |
| REORDER-05 | Enter page 0, negative, letters or a page beyond document count | Invalid input is rejected with a helpful message |
| REORDER-06 | Enter more than 500 requested pages | Input is rejected |
| REORDER-07 | Cancel the Save PDF picker | Temporary output is discarded and original remains unchanged |
| REORDER-08 | Save reordered copy and reopen it | Output opens and page count/order match requested list |
| REORDER-09 | Use airplane mode | Reordering works locally |

## Current limitations

- Output is rasterized/flattened; original selectable text, links, forms, bookmarks and digital signatures are not preserved as native PDF objects.
- Duplicate page numbers are deliberately rejected.
- Device acceptance tests remain pending.
