# F66 acceptance tests: Basic XLSX worksheet import

| ID | Scenario | Expected result |
|---|---|---|
| F66-01 | Tap Import XLSX from Spreadsheet | Android document picker opens filtered to XLSX files |
| F66-02 | Import an XLSX with shared strings | Text values are imported into the new editable sheet |
| F66-03 | Import an XLSX with inline strings | Inline text values are imported |
| F66-04 | Import numeric and boolean cells | Values appear as readable cell text |
| F66-05 | Import formula cells with cached values | Cached values appear; formulas are not recalculated |
| F66-06 | Import a sparse worksheet with empty cells | Empty positions and row/column layout are retained within supported limits |
| F66-07 | Save the imported sheet | MyOffice saves a new CSV copy; original XLSX remains unchanged |
| F66-08 | Import an XLSX with more than 100 rows or 30 columns | Import is rejected with a clear message instead of creating an oversized grid |
| F66-09 | Import a malformed or non-XLSX ZIP | Import fails safely and offers file details |
| F66-10 | Import a file without the first worksheet XML entry | Clear unsupported-layout error; app remains usable |
| F66-11 | Import in airplane mode | Parsing works locally without network access |
| F66-12 | Inspect memory and decompression limits | Import rejects files above 10 MiB or worksheet XML above the configured uncompressed cap |

## Current limitations

- Only the first worksheet XML entry is read; workbook relationships and alternate sheet numbering are not resolved.
- Cell styles, merged cells, charts, images, multiple sheets, formula calculation and XLSX export are not supported yet.
- Dates may appear as Excel serial numbers because number formats are not parsed.
- Device acceptance tests remain pending.
