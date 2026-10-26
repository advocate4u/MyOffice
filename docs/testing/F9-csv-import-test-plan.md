# F9 acceptance tests: Import existing CSV files

Run on Android using test CSV files you control.

| ID | Scenario | Expected result |
|---|---|---|
| F9-01 | Open a small UTF-8 CSV through Open a file | Spreadsheet grid opens with parsed rows and columns |
| F9-02 | CSV contains quoted commas | Commas inside quotes remain in the same cell |
| F9-03 | CSV contains escaped quotes | Quotes are restored correctly |
| F9-04 | CSV contains Hindi/emoji | Text remains readable |
| F9-05 | Edit imported data and save | A new local sheet is created |
| F9-06 | Check the source CSV after saving | Original source bytes remain unchanged |
| F9-07 | Open CSV larger than 1 MiB | App declines import with a clear message; app remains usable |
| F9-08 | Cancel the file picker | Home remains usable |
| F9-09 | Open malformed CSV | Error is recoverable; no crash |
| F9-10 | Use in airplane mode with local storage | Import/edit/save works offline |

## Current limitations

- Import limit is 1 MiB.
- No XLS/XLSX support, formulas or automatic type conversion.
- Source file is imported as a new internal sheet; saving does not overwrite the source.

## Build verification

- GitHub Actions run completes successfully.
- Artifact `MyOffice-debug-apk` contains `app-debug.apk`.
- Execute F9-01 through F9-10 on Android before marking device-verified.
