# F71 acceptance tests: PDF page text annotation

| ID | Scenario | Expected result |
|---|---|---|
| F71-01 | Open a readable PDF and tap Add text to current page | Annotation dialog names the current page number |
| F71-02 | Cancel the dialog | No export begins |
| F71-03 | Submit blank text | A validation message appears |
| F71-04 | Enter annotation text and export | Save picker suggests an `_annotated.pdf` copy |
| F71-05 | Open exported PDF | Text is drawn on the selected page only |
| F71-06 | Export from page 2 of a multi-page PDF | Other pages remain without the new annotation |
| F71-07 | Set viewer rotation then annotate | Output page uses selected rotation and includes annotation |
| F71-08 | Cancel destination picker | No destination file is created |
| F71-09 | Reopen original PDF | Source is unchanged |
| F71-10 | Use airplane mode | Annotation export works offline |
| F71-11 | Inspect output | Annotation is flattened into the page and cannot be edited or removed; text selection/search may be lost |

## Limitations

This creates a rasterized, flattened PDF copy. It is not a native annotation object, handwritten signature or cryptographic digital signature. Device acceptance tests remain pending.
