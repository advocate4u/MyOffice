# F40 acceptance tests: Spreadsheet library search and sorting

Run on Android with multiple saved CSV sheets.

| ID | Scenario | Expected result |
|---|---|---|
| F40-01 | Open Spreadsheet list | Search field and saved sheets appear |
| F40-02 | Search filename | Matching sheet appears |
| F40-03 | Search first-row text | Matching sheet appears |
| F40-04 | Search with different case | Search is case-insensitive |
| F40-05 | Tap Sort | Sheets sort by filename A–Z |
| F40-06 | Tap Sort again | Modified-newest order returns |
| F40-07 | Search while sorted | Search and sorting apply together |
| F40-08 | Delete filtered result | Confirmation appears and list refreshes |
| F40-09 | Open filtered result | Correct CSV sheet opens |
| F40-10 | No matches | Clear empty-state message appears |
| F40-11 | Use airplane mode | Search and sorting work offline |

## Current limitations

- Search includes filename and first row only, not every cell in large sheets.
- Sort preference is session-only.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F40-01 through F40-11 on Android before marking device-verified.
