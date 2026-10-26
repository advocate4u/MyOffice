# F56 acceptance tests: PDF form checkmark

| ID | Scenario | Expected result |
|---|---|---|
| F56-01 | Open a local PDF and tap Fill form · Add checkmark | Confirmation explains that a flattened copy will be created |
| F56-02 | Cancel confirmation | No output file is created |
| F56-03 | Confirm checkmark on current page | A new PDF copy contains a green checkmark near the lower-right of that page |
| F56-04 | Open output PDF | The checkmark is visible and page count remains unchanged |
| F56-05 | Inspect original PDF | Original file is unchanged |
| F56-06 | Use on first and last page | Mark is applied only to the selected page |
| F56-07 | Use with a rotated viewer page | Output copy follows the chosen rotation setting |
| F56-08 | Use in airplane mode | Checkmark export works locally |
| F56-09 | Open a damaged or encrypted PDF | A useful error is shown instead of crashing |

## Current limitations

- This is a flattened visual checkmark, not an interactive AcroForm checkbox.
- Placement is fixed near the lower-right; arbitrary tap-to-place positioning is not implemented yet.
- Exported pages are rasterized, so text selection/search and accessibility may be reduced in the output copy.
- Device acceptance tests remain pending.
