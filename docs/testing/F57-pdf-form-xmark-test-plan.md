# F57 acceptance tests: PDF form X mark

| ID | Scenario | Expected result |
|---|---|---|
| F57-01 | Open a PDF and choose Fill form · Add X mark | Confirmation describes a new flattened copy |
| F57-02 | Cancel | No output is created |
| F57-03 | Confirm on the current page | Red X is drawn near the lower-right of that page |
| F57-04 | Reopen the output PDF | Mark is visible and page count is unchanged |
| F57-05 | Inspect original PDF | Original bytes remain unchanged |
| F57-06 | Mark first and last pages | Only the selected page is marked |
| F57-07 | Use offline | Mark export works locally |
| F57-08 | Open invalid/encrypted source | User gets an error rather than a crash |

## Current limitations

- This is a visual flattened mark, not an interactive form control.
- Position is fixed near the lower-right and cannot be dragged yet.
- Output pages are rasterized, so text search/selectability and accessibility can be reduced.
- Device acceptance tests remain pending.
