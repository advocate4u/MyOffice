# F68 acceptance tests: Basic XLSX workbook export

| ID | Scenario | Expected result |
|---|---|---|
| F68-01 | Tap Export XLSX copy from the sheet editor | Android Save document picker opens with an .xlsx filename |
| F68-02 | Save the exported file and open it in Excel or LibreOffice | Workbook opens with a single MyOffice worksheet |
| F68-03 | Export text with XML special characters | Values are XML-escaped and remain readable |
| F68-04 | Export numeric cells | Simple numeric strings are written as numeric values |
| F68-05 | Export text with leading zeros | Values such as 00123 remain text rather than being converted to a number |
| F68-06 | Export an empty grid cell | Cell remains blank in the output |
| F68-07 | Export a cell beginning with '=' | Value is exported as text; no formula is executed |
| F68-08 | Export a grid above 500 rows or 50 columns | Export is rejected with a clear message |
| F68-09 | Cancel the Save document picker | Temporary file is cleaned up and the editor remains usable |
| F68-10 | Export then reopen the original internal CSV | Internal CSV sheet remains unchanged |
| F68-11 | Use in airplane mode | Workbook generation is local and requires no network |

## Current limitations

- Exports one worksheet with values only; formatting, formulas, merged cells, charts, images and multiple sheets are not supported.
- XLSX export creates a new file and does not convert or overwrite the internal CSV sheet.
- Device acceptance tests remain pending.
