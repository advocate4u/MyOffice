# F57 acceptance tests: PDF text note overlay

| ID | Scenario | Expected result |
|---|---|---|
| F57-01 | Open a local PDF and choose Add text to current page | Multi-line annotation dialog opens for the current page |
| F57-02 | Submit empty text | Validation message appears; no output file is created |
| F57-03 | Enter a short note and export | Save dialog creates a new annotated PDF copy |
| F57-04 | Open exported copy | Note appears in a pale-yellow box near the lower portion of the selected page |
| F57-05 | Enter a long multi-line note | Text wraps to a bounded number of lines without crashing |
| F57-06 | Navigate to another page and annotate | Only the selected page receives the note |
| F57-07 | Inspect original PDF | Source file remains unchanged |
| F57-08 | Use airplane mode | Annotation works locally |

## Current limitations

- Note is flattened into a rendered page image, not a native PDF comment annotation.
- PDF export flattens page content and can reduce selectable/searchable text and image fidelity.
- Device acceptance tests remain pending.
