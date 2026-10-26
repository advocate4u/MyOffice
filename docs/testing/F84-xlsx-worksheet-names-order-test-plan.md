# F84 — XLSX worksheet names and workbook order

## Acceptance checks
1. Create an XLSX workbook with at least three worksheets, custom names, and a worksheet order that differs from the sheet XML filenames.
2. Open the workbook in MyOffice and confirm the chooser displays the workbook's actual worksheet names in workbook order.
3. Select each worksheet and confirm the corresponding cell values are imported.
4. Confirm names containing spaces, punctuation, and non-English characters display correctly.
5. Confirm the source workbook remains unchanged.
6. Try a workbook with missing or malformed workbook relationship metadata; confirm a safe numeric filename-order fallback and no crash.
7. Test a workbook over 10 MiB or metadata XML over the configured size limit; confirm import stops safely.

## Limitations
- The importer still imports one selected worksheet into a new CSV sheet. It does not preserve workbook formatting, formulas as formulas, charts, merged cells, or multi-sheet editing.
- Relationship targets are handled for standard internal OOXML paths; unusual or nonstandard targets may use the numeric fallback.

## CI/device verification
- CI must compile the debug APK and verify package identity.
- Device acceptance is required with custom worksheet names and reordered worksheet relationships.
