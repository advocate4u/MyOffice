# F50 acceptance tests: Reset PDF rotation

| ID | Scenario | Expected result |
|---|---|---|
| F50-01 | Rotate PDF to 90° and tap Reset rotation | Page returns to 0° |
| F50-02 | Rotate PDF to 180° and reset | Page returns to original orientation |
| F50-03 | Reset an already unrotated PDF | No error; orientation remains 0° |
| F50-04 | Reset rotation then leave and reopen the PDF | 0° orientation is restored |
| F50-05 | Reset PDF A, open PDF B | B retains its independent rotation setting |
| F50-06 | Reset rotation after zooming | Zoom setting remains usable and page redraws |
| F50-07 | Use in airplane mode | Reset works offline |
| F50-08 | Inspect source PDF | Source file remains unchanged |

## Current limitations

- Rotation changes are viewer-only and do not rewrite the PDF file.
- Device acceptance tests remain pending.
