# F39 acceptance tests: Writer library sorting

Run on Android with several saved Writer documents having different first-line titles and modification times.

| ID | Scenario | Expected result |
|---|---|---|
| F39-01 | Open Writer list | Default order is modified newest first |
| F39-02 | Tap Sort | Documents sort by first-line title A–Z |
| F39-03 | Tap Sort again | Modified-newest order returns |
| F39-04 | Search while title-sorted | Search remains active and results keep title order |
| F39-05 | Save a document while sorted | Returning to list refreshes results |
| F39-06 | Delete a filtered document | Confirmation appears and list refreshes |
| F39-07 | Empty first-line titles | Documents use “Untitled document” for sorting/display |
| F39-08 | Use Hindi titles | Sorting remains stable and does not crash |
| F39-09 | Use airplane mode | Sorting works offline |

## Current limitations

- Sort preference is session-only.
- Title sorting uses the first line of plain text; it does not inspect DOCX formatting.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F39-01 through F39-09 on Android before marking device-verified.
