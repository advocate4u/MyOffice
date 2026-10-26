# F18 acceptance tests: Import DOCX text into Writer

Run on Android with legally obtained DOCX test documents.

| ID | Scenario | Expected result |
|---|---|---|
| F18-01 | Open a small DOCX file through Open a file | Writer opens with extracted paragraph text |
| F18-02 | DOCX contains multiple paragraphs | Paragraph boundaries are preserved as line breaks |
| F18-03 | DOCX contains tabs and manual line breaks | Tabs/line breaks are represented in plain text |
| F18-04 | DOCX contains Hindi/Unicode text | Unicode content is preserved |
| F18-05 | Save imported Writer copy | A new local text document is created |
| F18-06 | Check source DOCX after import | Original source remains unchanged |
| F18-07 | Open a damaged or non-DOCX ZIP renamed .docx | Clear import error; app remains usable |
| F18-08 | Open DOCX larger than 10 MiB | Import is declined with a recoverable error |
| F18-09 | DOCX has XML with a DOCTYPE/external entity | Parser rejects it; no external entity is resolved |
| F18-10 | Use local DOCX in airplane mode | Import works without network |

## Current limitations

- Text-only import: styles, fonts, tables' layout, images, headers/footers and page layout are not preserved.
- Does not export DOCX and does not edit the original package.
- DOCX text XML is limited to 5 MiB after decompression.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F18-01 through F18-10 on Android before marking device-verified.
