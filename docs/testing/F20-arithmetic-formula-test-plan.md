# F20 acceptance tests: Cell-reference arithmetic preview

Use a CSV sheet with numeric cells A1=10, B1=2, and formulas such as C1=`=A1+B1`.

| ID | Scenario | Expected result |
|---|---|---|
| F20-01 | Formula `=A1+B1` | Preview result is 12 |
| F20-02 | Formula `=A1-B1` | Preview result is 8 |
| F20-03 | Formula `=A1*B1` | Preview result is 20 |
| F20-04 | Formula `=A1/B1` | Preview result is 5 |
| F20-05 | Divide by zero | Clear division-by-zero error |
| F20-06 | One referenced cell is blank/text | Clear numeric-cell error |
| F20-07 | Formula references a cell outside the current grid | Clear numeric-cell error |
| F20-08 | Formula preview runs | Formula text remains unchanged |
| F20-09 | Use in airplane mode | Preview works offline |

## Current limitations

- Preview-only, no recalculation chain or formula results written back.
- Supports direct arithmetic between two cell references and F16 aggregate range functions.
- No parentheses, operator precedence, numeric literals, chained formulas or spreadsheet-compatible recalculation.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F20-01 through F20-09 on Android before marking device-verified.
