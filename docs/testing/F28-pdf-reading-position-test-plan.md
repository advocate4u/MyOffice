# F28 acceptance tests: Remember PDF reading position

Run on Android with a multi-page local PDF selected through the system document picker.

| ID | Scenario | Expected result |
|---|---|---|
| F28-01 | Open PDF and navigate to page 3 | Page 3 is displayed |
| F28-02 | Return to home and reopen the same URI | PDF opens on page 3 |
| F28-03 | Open a different PDF | Its independent saved position is used, defaulting to page 1 |
| F28-04 | Navigate to page 1 and leave | Reopening starts at page 1 |
| F28-05 | Open a PDF whose saved page exceeds its current page count | Viewer clamps to the last valid page |
| F28-06 | Change zoom and navigate | Reading position saves without changing page content |
| F28-07 | Use airplane mode | Position is stored and restored offline |
| F28-08 | Inspect network access | No network access is needed for reading-position persistence |

## Current limitations

- Position is keyed by the selected document URI; a different URI for the same underlying file has a separate position.
- This stores a local page index only, not annotations or scroll position within a page.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F28-01 through F28-08 on Android before marking device-verified.
