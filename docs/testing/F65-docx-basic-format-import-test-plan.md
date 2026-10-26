# F65 acceptance tests: Basic DOCX formatting import

| ID | Scenario | Expected result |
|---|---|---|
| F65-01 | Open DOCX with bold text | Imported text has bold span |
| F65-02 | Open DOCX with italic, underline and strike | Corresponding character spans are applied |
| F65-03 | Open DOCX with explicit six-digit font color | Supported RGB color is imported |
| F65-04 | Open DOCX with yellow/green/red highlight | Supported highlight color is imported |
| F65-05 | Open DOCX with font size in half-points | Relative text size is imported within safe bounds |
| F65-06 | Open DOCX containing tabs and line breaks | Tabs and line breaks remain readable |
| F65-07 | Open DOCX with Hindi and English text | Unicode text remains readable |
| F65-08 | Open malformed or encrypted DOCX | Clear error appears; app remains usable |
| F65-09 | Open DOCX larger than 10 MiB | Import is rejected by the size limit |
| F65-10 | Save imported content as HTML | Supported imported formatting remains in the HTML copy |
| F65-11 | Export imported content as DOCX | Supported character styles can round-trip through the focused exporter |
| F65-12 | Inspect source DOCX | Source file remains unchanged |
| F65-13 | Import DOCX run with w:rFonts ascii/hAnsi/eastAsia | Available font family is represented by a Writer TypefaceSpan |
| F65-14 | Import paragraphs styled as Title, Heading 1, Heading 2 or Heading 3 | Basic visual hierarchy is represented with bold and relative text size |

## Limitations

This is a basic run-style subset. It does not preserve images, tables, page layout, section properties, headers/footers, comments, tracked changes, fields, numbering definitions, styles inheritance, embedded objects or complete DOCX fidelity. Device and third-party office-app tests remain pending.
