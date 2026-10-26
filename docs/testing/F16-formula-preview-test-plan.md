# F16 acceptance tests: Spreadsheet aggregate formula preview

Use a CSV sheet with numeric cells such as A1=2, A2=3, A3=5 and formulas such as B1=`=SUM(A1:A3)`.

| ID | Scenario | Expected result |
|---|---|---|
| F16-01 | Enter `=SUM(A1:A3)` and tap Preview formula results | Dialog shows B1 = 10 if formula is in B1 |
| F16-02 | Enter `=AVERAGE(A1:A3)` | Result is 3.333333 (trimmed to 3.333333) |
| F16-03 | Enter `=MIN(A1:A3)` | Result is 2 |
| F16-04 | Enter `=MAX(A1:A3)` | Result is 5 |
| F16-05 | Enter `=COUNT(A1:A3)` | Result is 3 |
| F16-06 | Range includes blanks/text | Only numeric cells are included; COUNT counts numeric cells |
| F16-07 | Range has no numeric cells for SUM/AVERAGE/MIN/MAX | Clear range error appears |
| F16-08 | Enter malformed range | Error/unsupported formula is shown, app remains usable |
| F16-09 | Preview formulas | Cell text is not changed by the preview |
| F16-10 | Use in airplane mode | Formula preview works offline |

## Current limitations

- Preview-only: calculated values are not written back into cells or CSV.
- Supports SUM, AVERAGE, MIN, MAX and COUNT over a single rectangular cell range.
- No dependencies, nested formulas, relative references, date/time functions or XLSX formulas.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F16-01 through F16-10 on Android before marking device-verified.
