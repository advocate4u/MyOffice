# F58 acceptance tests: PDF freehand annotation

| ID | Scenario | Expected result |
|---|---|---|
| F58-01 | Open a PDF and choose Draw / sign by hand | Drawing pad opens |
| F58-02 | Draw strokes with a finger | Blue ink appears inside the pad |
| F58-03 | Tap Clear drawing | Pad becomes empty |
| F58-04 | Export with no strokes | Validation message appears; no export starts |
| F58-05 | Draw and export | New PDF copy is saved |
| F58-06 | Open exported PDF | Handwriting appears near the lower portion of the selected page |
| F58-07 | Annotate one page of a multi-page PDF | Only the selected page receives the drawing |
| F58-08 | Inspect original PDF | Original remains unchanged |
| F58-09 | Use airplane mode | Drawing and export work offline |

## Current limitations

- The drawing is rasterized and flattened into the PDF page; it is not an editable vector annotation.
- The current drawing pad is placed in a fixed lower-page region rather than an arbitrary touch position on the page.
- Export can reduce the selectability/searchability of original PDF text and must be reviewed for quality.
- Device acceptance tests remain pending.
