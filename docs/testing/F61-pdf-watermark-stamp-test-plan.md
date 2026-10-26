# F61 acceptance tests: PDF watermark and stamp export

| ID | Scenario | Expected result |
|---|---|---|
| F61-01 | Open a readable local PDF and tap Add watermark / stamp | Stamp text dialog appears |
| F61-02 | Cancel dialog | No export begins |
| F61-03 | Submit blank stamp text | Clear prompt appears; no export begins |
| F61-04 | Enter DRAFT and export | A new PDF named with a _stamped suffix is offered for saving |
| F61-05 | Open stamped output | Every page contains the DRAFT stamp |
| F61-06 | Use Hindi/English/Unicode stamp text | Text renders where supported by the system font; verify glyphs visually |
| F61-07 | Set viewer rotation to 90° and stamp | Output pages include both the selected rotation and stamp |
| F61-08 | Cancel destination picker | No destination file is created |
| F61-09 | Reopen original | Original PDF remains unchanged |
| F61-10 | Use airplane mode | Export works offline |
| F61-11 | Use encrypted/damaged PDF or a PDF over 500 pages | Export fails gracefully with a message |
| F61-12 | Inspect stamped output | Output is rasterized and may lose searchable text, links, forms, accessibility structure and existing annotations |

## Important limitation

The stamp is permanently drawn into a new rasterized copy; it is not a native editable PDF annotation and cannot be removed after export. The source document is never modified. Device acceptance tests remain pending.
