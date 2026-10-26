# F10 acceptance tests: Export CSV grid

Run on Android and save to a writable local destination.

| ID | Scenario | Expected result |
|---|---|---|
| F10-01 | Tap Export CSV copy | Android Save document picker opens |
| F10-02 | Export a simple grid | CSV file contains rows and columns in the correct order |
| F10-03 | Export cells containing commas | Values are quoted and remain in one cell on re-import |
| F10-04 | Export cells containing quotes | Quotes are escaped as doubled quotes |
| F10-05 | Export cells containing line breaks | Field is quoted and line breaks survive round-trip |
| F10-06 | Export Hindi/emoji text | UTF-8 text is preserved |
| F10-07 | Cancel export picker | Grid remains open and no success toast is shown |
| F10-08 | Save to a read-only/unavailable location | Clear error appears |
| F10-09 | Use in airplane mode | Export to local storage works offline |
| F10-10 | Re-import exported CSV | Cell values match the original grid |

## Current limitations

- No formulas, formatting, XLSX, multiple sheets or automatic data types.
- Export creates a copy in a user-chosen destination; it does not overwrite the internal sheet.

## Build verification

- GitHub Actions run completes successfully.
- Artifact `MyOffice-debug-apk` contains `app-debug.apk`.
- Execute F10-01 through F10-10 on Android before marking device-verified.
