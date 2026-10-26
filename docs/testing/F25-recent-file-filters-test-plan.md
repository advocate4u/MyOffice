# F25 acceptance tests: Recent file-type filters

Run on Android with a mix of local PDF, TXT/MD, CSV, DOCX/XLSX/PPTX files where available.

| ID | Scenario | Expected result |
|---|---|---|
| F25-01 | Select All | All recent items that match the search are shown |
| F25-02 | Select PDF | Only PDF items are shown |
| F25-03 | Select Text | Text, Markdown, JSON, XML and log files are shown; CSV is excluded |
| F25-04 | Select CSV | CSV items are shown |
| F25-05 | Select Office | Common Word, Excel, PowerPoint and WPS extensions are shown |
| F25-06 | Change sort order while a filter is active | Filter remains active and results reorder |
| F25-07 | Search while a filter is active | Both constraints apply together |
| F25-08 | No items match filter/search | Clear empty-state message appears |
| F25-09 | Tap a filtered file | The selected file opens |
| F25-10 | Use in airplane mode | Filters work offline |

## Current limitations

- File type is inferred from provider MIME type and/or filename extension; providers can report incomplete metadata.
- This filters recent files only and does not scan the whole device.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F25-01 through F25-10 on Android before marking device-verified.
