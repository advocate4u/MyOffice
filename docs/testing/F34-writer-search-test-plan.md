# F34 acceptance tests: Writer library search

Run on Android with several saved local Writer documents.

| ID | Scenario | Expected result |
|---|---|---|
| F34-01 | Open Writer list | Search field and saved documents appear |
| F34-02 | Search a unique title phrase | Only matching document appears |
| F34-03 | Search text in the document preview | Matching document appears |
| F34-04 | Search using different letter case | Search remains case-insensitive |
| F34-05 | Search with no matches | “No documents match your search” appears |
| F34-06 | Clear the search | All saved documents return |
| F34-07 | Open a filtered result | Correct document opens |
| F34-08 | Delete a filtered result | Confirmation appears; deletion refreshes filtered list |
| F34-09 | Use airplane mode | Search works offline |

## Current limitations

- Search indexes the first-line title, filename and the loaded text preview/content of each local Writer document.
- No full-device search or advanced query operators.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F34-01 through F34-09 on Android before marking device-verified.
