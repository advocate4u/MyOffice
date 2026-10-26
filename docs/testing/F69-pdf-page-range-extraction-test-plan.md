# F69 acceptance tests: PDF page-range extraction

| ID | Scenario | Expected result |
|---|---|---|
| F69-01 | Tap Extract pages to new PDF | First/last page inputs appear |
| F69-02 | Enter valid range 2–4 | New PDF contains pages 2, 3 and 4 in order |
| F69-03 | Enter invalid range or non-numeric input | Validation message appears; export does not start |
| F69-04 | Choose last page beyond document length | Export clamps to last page or reports invalid range safely |
| F69-05 | Cancel dialog or Save picker | No destination file is created |
| F69-06 | Set viewer rotation and extract range | Output pages reflect the selected rotation |
| F69-07 | Open original PDF afterward | Original source remains unchanged |
| F69-08 | Open extracted output | Page count matches the valid selected range |
| F69-09 | Use airplane mode | Extraction works offline |
| F69-10 | Use damaged/encrypted PDF or more than 500 pages | Export fails gracefully |
| F69-11 | Inspect output | Rasterized output may lose searchable text, links, forms and annotations |

## Limitations

Page extraction renders pages into a new image-based PDF. It is not a lossless structural copy. Device acceptance tests remain pending.
