# F67 acceptance tests: PDF page-range extraction

| ID | Scenario | Expected result |
|---|---|---|
| F67-01 | Open a multi-page PDF and tap Extract pages to new PDF | First/last page inputs appear |
| F67-02 | Enter valid range 2–4 | New PDF contains source pages 2, 3 and 4 in order |
| F67-03 | Enter first page below 1, last below first, or non-numeric text | Validation message appears; export does not start |
| F67-04 | Choose a last page beyond document length | Export clamps the end to the document's last page or reports invalid range safely |
| F67-05 | Cancel the range dialog | No export starts |
| F67-06 | Cancel Save document picker | No destination file is written |
| F67-07 | Set viewer rotation and extract range | Output pages reflect the selected rotation |
| F67-08 | Open original PDF afterward | Original source remains unchanged |
| F67-09 | Open extracted output | Page count matches the selected valid range and all pages render |
| F67-10 | Use airplane mode | Extraction works offline |
| F67-11 | Use damaged/encrypted PDF or more than 500 pages | Export fails gracefully with a message |
| F67-12 | Inspect extracted PDF content | Output is rasterized; text search, links, forms and annotations may not be preserved |

## Limitations

Page extraction renders pages into a new image-based PDF. It is not a lossless structural copy, and the source file is never modified. Device acceptance tests remain pending.
