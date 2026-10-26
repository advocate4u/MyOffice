# F41 acceptance tests: Presentation library search and sorting

Run on Android with multiple saved slide decks with different titles and body text.

| ID | Scenario | Expected result |
|---|---|---|
| F41-01 | Open Presentation list | Search and sort controls appear |
| F41-02 | Search deck title | Matching deck appears |
| F41-03 | Search first-slide body text | Matching deck appears |
| F41-04 | Search filename | Matching deck appears |
| F41-05 | Search using different letter case | Search is case-insensitive |
| F41-06 | Tap Sort | Decks sort by first-slide title A–Z |
| F41-07 | Tap Sort again | Modified-newest order returns |
| F41-08 | Search while sorted | Search and sort remain combined |
| F41-09 | Delete a filtered deck | Confirmation appears and list refreshes |
| F41-10 | Open a filtered result | Correct deck opens |
| F41-11 | Empty/corrupt deck file | Recoverable display behavior; no crash |
| F41-12 | Use airplane mode | Search and sorting work offline |

## Current limitations

- Search checks the title, first-slide body and filename, not all slide contents.
- Sort preference is session-only.
- This milestone still stores text-only slides, not PPTX-compatible presentation files.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F41-01 through F41-12 on Android before marking device-verified.
