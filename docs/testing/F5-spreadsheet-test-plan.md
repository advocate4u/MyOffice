# F5 acceptance tests: Offline CSV spreadsheet grid

Run on the debug APK in airplane mode. This is a basic CSV grid, not a formula engine or XLSX editor.

| ID | Scenario | Expected result |
|---|---|---|
| F5-01 | Tap Spreadsheet from home | CSV sheet list opens |
| F5-02 | Create a new sheet | Editable grid opens with 4 rows and 4 columns |
| F5-03 | Edit several cells | Cell text remains visible while editing |
| F5-04 | Add a row | New blank row appears with matching column count |
| F5-05 | Add a column | New blank cell appears in each row |
| F5-06 | Save new sheet | CSV file appears in list |
| F5-07 | Reopen saved sheet | Cell values load correctly |
| F5-08 | Enter commas, quotes and line breaks in a cell | CSV output escapes those values; reopening restores the same cell content |
| F5-09 | Open a CSV with quoted commas | Quoted comma remains inside its cell |
| F5-10 | Open a CSV with escaped quotes | Doubled quotes are parsed to a single quote |
| F5-11 | Leave after editing | Unsaved-changes dialog appears |
| F5-12 | Use in airplane mode | Create/edit/save/reopen works offline |
| F5-13 | Open malformed CSV | Reader remains usable; no crash |
| F5-14 | Inspect manifest permissions | Core CSV grid requires no internet permission |

## Current limitations

- No formulas, recalculation, charts, sort/filter, formatting or multi-sheet workbooks.
- No XLS/XLSX support; this milestone reads/writes UTF-8 CSV.
- CSV records containing line breaks inside quoted fields are parsed; validate round-trip behavior on device before relying on complex datasets.

## Build verification

- GitHub Actions run completes successfully.
- Artifact `MyOffice-debug-apk` contains `app-debug.apk`.
- Install and run F5-01 through F5-14 on Android before marking the feature device-verified.
