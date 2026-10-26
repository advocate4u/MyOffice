# F60 acceptance tests: PDF rotated-copy export

| ID | Scenario | Expected result |
|---|---|---|
| F60-01 | Export a local PDF at 0° | A new PDF copy is saved to the chosen destination |
| F60-02 | Rotate viewer to 90° and export | Every output page is rotated by 90° |
| F60-03 | Rotate to 180° or 270° and export | All output pages reflect the selected orientation |
| F60-04 | Cancel the Save document picker | No output is written to the chosen destination |
| F60-05 | Reopen the original PDF | Original document remains unchanged |
| F60-06 | Open the exported copy | Pages render in the expected orientation |
| F60-07 | Export a multi-page PDF | Page count and page order are preserved |
| F60-08 | Export an encrypted, damaged or unavailable PDF | A clear failure message appears and the app remains usable |
| F60-09 | Export more than 500 pages | Export is refused with a clear error |
| F60-10 | Use airplane mode | Rendering and export work offline |
| F60-11 | Inspect output content | Pages are rasterized; text search/selectability may not be preserved |

## Important limitation

This feature renders each source page to a bitmap and writes a new PDF containing those images. It does not edit the original PDF's internal page objects. The output may be larger and loses selectable/searchable text, links, forms, accessibility structure and existing annotations. Device acceptance tests remain pending.
