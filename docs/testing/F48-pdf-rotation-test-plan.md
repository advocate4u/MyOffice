# F48 acceptance tests: PDF page rotation

Run on Android with local PDFs that include portrait and landscape pages.

| ID | Scenario | Expected result |
|---|---|---|
| F48-01 | Open a readable PDF | Current page renders normally |
| F48-02 | Tap Rotate page 90° once | Current page rotates clockwise by 90° |
| F48-03 | Tap rotation repeatedly | Page cycles through 180°, 270° and 360°/original orientation |
| F48-04 | Rotate after zooming | Page remains readable and zoom control still works |
| F48-05 | Rotate then navigate to another page | Page navigation remains functional; viewer rotation applies to the next page |
| F48-06 | Use page jump after rotation | Page jump still navigates to the requested page |
| F48-07 | Close and reopen the PDF | Source document is unchanged; rotation is viewer state, not a PDF rewrite |
| F48-08 | Rotate a large page | No crash; render error is reported if device resources are insufficient |
| F48-09 | Use airplane mode | Rotation works locally without network |

## Current limitations

- Rotation currently applies to all pages while the PDF reader is open, and resets when leaving the reader.
- This is a viewing transform only; it does not permanently rotate pages in the source PDF.
- Device acceptance tests must be performed before marking this feature verified.
