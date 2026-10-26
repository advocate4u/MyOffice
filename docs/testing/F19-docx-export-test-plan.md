# F19 acceptance tests: Export Writer text as DOCX

Run on Android and verify output in at least one compatible office application.

| ID | Scenario | Expected result |
|---|---|---|
| F19-01 | Tap Export as DOCX in Writer | Android Save document picker opens with a .docx filename |
| F19-02 | Export English text with multiple paragraphs | DOCX contains the paragraph text and line breaks |
| F19-03 | Export Hindi/Unicode text | UTF-8 text is preserved in document.xml |
| F19-04 | Export text containing &, <, > and quotes | XML is escaped and package remains well-formed |
| F19-05 | Export tabs | Tabs are represented as WordprocessingML tab elements |
| F19-06 | Cancel Save picker | Editor remains usable; temporary file is cleaned up |
| F19-07 | Open exported DOCX in a compatible office app | Document opens and text is visible |
| F19-08 | Re-import exported DOCX into MyOffice | Text round-trips through F18 |
| F19-09 | Use in airplane mode with local destination | Export works offline |

## Current limitations

- Plain-text DOCX only; no rich formatting, images, tables, headers/footers or styles.
- This is a minimal OOXML package; compatibility must be validated in actual office apps.
- Export creates a new destination file and does not overwrite the internal Writer document.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F19-01 through F19-09 on Android and open the result in another office app before marking complete.
