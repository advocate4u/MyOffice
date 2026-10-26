# F63 acceptance tests: PDF watermark and stamp export

| ID | Scenario | Expected result |
|---|---|---|
| F63-01 | Tap Add watermark / stamp | Stamp text dialog appears |
| F63-02 | Cancel dialog | No export begins |
| F63-03 | Submit blank stamp text | Prompt appears; no export begins |
| F63-04 | Enter DRAFT and export | Save picker suggests a `_stamped.pdf` filename |
| F63-05 | Open stamped output | Every page contains the stamp |
| F63-06 | Use Unicode stamp text | Text renders for glyphs supported by the system font; inspect visually |
| F63-07 | Rotate viewer and stamp | Output includes the chosen rotation and stamp on every page |
| F63-08 | Cancel destination picker | No destination file is created |
| F63-09 | Reopen original | Original PDF remains unchanged |
| F63-10 | Use airplane mode | Export works offline |
| F63-11 | Use encrypted/damaged PDF or over 500 pages | Export fails gracefully |
| F63-12 | Inspect output | Output is rasterized and may lose searchable text, links, forms, accessibility structure and annotations |

## Limitation

The stamp is permanently drawn into a new rasterized copy, not a native editable PDF annotation. It cannot be removed from the exported copy. Device acceptance tests remain pending.
