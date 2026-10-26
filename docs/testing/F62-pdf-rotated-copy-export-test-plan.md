# F62 acceptance tests: PDF rotated-copy export

| ID | Scenario | Expected result |
|---|---|---|
| F62-01 | Export a local PDF at 0° | A new PDF copy is saved to the chosen destination |
| F62-02 | Rotate viewer to 90° and export | Every output page is rotated by 90° |
| F62-03 | Rotate to 180° or 270° and export | All output pages reflect the selected orientation |
| F62-04 | Cancel the Save document picker | No destination file is created |
| F62-05 | Reopen original PDF | Original document remains unchanged |
| F62-06 | Open exported copy | Pages render in the expected orientation |
| F62-07 | Export a multi-page PDF | Page count and order are preserved |
| F62-08 | Export encrypted, damaged or unavailable PDF | A clear failure message appears and the app remains usable |
| F62-09 | Export over 500 pages | Export is refused with a clear error |
| F62-10 | Use airplane mode | Export works offline |
| F62-11 | Inspect output | Pages are rasterized; searchable/selectable text may not be preserved |

## Limitation

The source is never modified. Output is a rasterized PDF, so text selection/search, links, forms, accessibility structure and existing annotations may be lost. Device acceptance tests remain pending.
