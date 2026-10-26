# F58 acceptance tests: PDF rotated-copy export

| ID | Scenario | Expected result |
|---|---|---|
| F58-01 | Open an ordinary local PDF and choose 0° export | A new PDF copy is prepared and saved to the chosen destination |
| F58-02 | Rotate viewer to 90° and export | Every page in the output is rotated by 90° |
| F58-03 | Rotate to 180° or 270° and export | All output pages reflect the selected orientation |
| F58-04 | Cancel the Save document picker | No output is written to the chosen destination; temporary file is discarded on cancellation |
| F58-05 | Reopen original PDF | Original document remains unchanged |
| F58-06 | Open exported copy | All pages render and have the expected orientation |
| F58-07 | Export a multi-page PDF | Page count and page order are preserved |
| F58-08 | Export an encrypted, damaged or unavailable PDF | A clear failure toast is shown; app remains usable |
| F58-09 | Export a PDF with more than 500 pages | Export is refused with a clear error |
| F58-10 | Export while viewing another screen after completion | Save picker is shown only while the activity is still alive |
| F58-11 | Use airplane mode | Rendering and export work offline |
| F58-12 | Verify output content | Pages are rasterized; text search/selectability may not be preserved |

## Important limitation

This feature renders each source page to a bitmap and writes a new PDF containing those images. It does not edit the original PDF's internal page objects. The output may be larger and loses selectable/searchable text, links, forms, accessibility structure and existing annotations. Device acceptance tests remain pending.
