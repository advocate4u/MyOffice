# F27 acceptance tests: PDF page jump

Run on Android with a local PDF containing at least three pages.

| ID | Scenario | Expected result |
|---|---|---|
| F27-01 | Open a multi-page PDF | Page number field starts at 1 |
| F27-02 | Enter page 3 and tap Go to page | Third page renders and label reports page 3 |
| F27-03 | Enter 1 | First page renders |
| F27-04 | Enter 0 or a negative number | Validation message appears; current page stays open |
| F27-05 | Enter non-numeric/empty value | Validation message appears; no crash |
| F27-06 | Enter a number beyond page count | Page is clamped to the final page and label reflects actual page |
| F27-07 | Navigate with Previous/Next after a jump | Page navigation works and page field stays synchronized |
| F27-08 | Jump while zoomed | Selected page keeps current zoom level |
| F27-09 | Use airplane mode | Local page jump works offline |

## Current limitations

- Page number is one-based.
- This is direct page navigation, not text search or a PDF table of contents.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F27-01 through F27-09 on Android before marking device-verified.
