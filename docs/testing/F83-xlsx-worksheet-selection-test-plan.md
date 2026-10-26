# F83 — XLSX worksheet selection

## Acceptance checks
1. Open an XLSX workbook containing at least two worksheets with distinct cell contents.
2. Confirm MyOffice shows a worksheet chooser rather than automatically importing only the first worksheet.
3. Select each listed worksheet in turn and confirm the corresponding cell values are imported into a new editable local CSV sheet.
4. Confirm the source XLSX remains unchanged and the imported sheet can be edited and saved locally.
5. Test a workbook with only one worksheet; selecting its only option should work normally.
6. Test a workbook with no worksheet XML or an invalid ZIP; show an error without crashing.
7. Test an XLSX over 10 MiB; confirm the existing size limit is enforced.
8. Confirm the import still supports shared strings, inline strings, numeric values, booleans and cached formula results.

## Limitations
- Chooser labels are generic (“Worksheet 1”, etc.) and follow numeric worksheet XML filenames, not workbook display names or custom relationship order.
- Only cell values from the selected sheet are imported. Formatting, formulas as formulas, multiple-sheet editing, and workbook round-trip preservation are not supported.

## CI/device verification
- CI must compile the debug APK and verify package identity.
- Device acceptance is required using a workbook with at least two worksheets.
