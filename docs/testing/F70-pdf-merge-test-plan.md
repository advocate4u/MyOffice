# F70 acceptance tests: PDF merge

| ID | Scenario | Expected result |
|---|---|---|
| F70-01 | Open PDF A and tap Merge with other PDFs | System picker opens for additional PDFs |
| F70-02 | Select PDF B | Output contains A pages first, then B pages in order |
| F70-03 | Select multiple additional PDFs | All pages merge in picker order after the current PDF |
| F70-04 | Select the current PDF again | Duplicate URI is ignored |
| F70-05 | Cancel picker or choose no additional PDF | Merge cancels or a clear prompt appears |
| F70-06 | Cancel Save picker | No destination file is created |
| F70-07 | Open merged output | Every page renders and order is preserved |
| F70-08 | Use encrypted, damaged or unavailable source | Clear error appears; app remains usable |
| F70-09 | Merge over 500 pages | Merge stops with a clear error |
| F70-10 | Use airplane mode | Merge works offline |
| F70-11 | Inspect source files | Source PDFs remain unchanged |
| F70-12 | Inspect output | Rasterized output may lose selectable text, links, forms, annotations and accessibility structure |

## Limitations

Merge flattens each page to an image and writes a new PDF. It is not a lossless structural merge. Device acceptance tests remain pending.
