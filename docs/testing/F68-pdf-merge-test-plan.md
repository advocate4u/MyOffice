# F68 acceptance tests: PDF merge

| ID | Scenario | Expected result |
|---|---|---|
| F68-01 | Open PDF A and tap Merge with other PDFs | System picker opens for additional PDFs |
| F68-02 | Select PDF B | Output contains A pages first, then B pages in order |
| F68-03 | Select multiple additional PDFs | All pages are merged in picker order after the current PDF |
| F68-04 | Select the currently open PDF again | Duplicate URI is ignored |
| F68-05 | Cancel picker or choose no additional PDF | Merge is cancelled or a clear prompt appears |
| F68-06 | Cancel Save document picker | No destination file is created |
| F68-07 | Open merged output | Every page renders and page order is preserved |
| F68-08 | Merge an encrypted, damaged or unavailable source | Clear failure message appears; app remains usable |
| F68-09 | Merge inputs totalling more than 500 pages | Merge is stopped with a clear error |
| F68-10 | Use airplane mode | Merge works offline |
| F68-11 | Inspect source files | All source PDFs remain unchanged |
| F68-12 | Inspect output content | Output is rasterized and may lose searchable/selectable text, links, forms, annotations and accessibility structure |

## Limitations

PDF merge flattens each page to an image and writes a new PDF. It is not a lossless structural merge. Device acceptance tests remain pending.
