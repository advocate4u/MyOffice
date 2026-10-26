# F85 — Spreadsheet formula help

## Acceptance checks
1. Open or create a CSV sheet and confirm the editor shows a Formula help action.
2. Open Formula help and verify the guide lists supported two-cell arithmetic: +, -, *, /.
3. Verify the guide lists SUM, AVERAGE, MIN, MAX, and COUNT with range examples.
4. Confirm the guide clearly says preview results are read-only and formulas are not persisted as spreadsheet formulas.
5. Close the dialog and confirm the sheet remains editable and unchanged.
6. Use Preview formula results and confirm existing formula-preview behavior is unchanged.
7. Confirm the editor's subtitle no longer claims formulas are unsupported.

## Limitations
- This feature documents the currently supported preview syntax; it does not add full spreadsheet calculation, recalculation, or formula-preserving XLSX/CSV storage.

## CI/device verification
- CI must compile the debug APK and verify its package identity and version.
- Device acceptance should verify the dialog fits on a phone screen, scrolls when necessary, and closes without altering the sheet.
