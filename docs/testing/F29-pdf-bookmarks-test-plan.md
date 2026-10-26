# F29 acceptance tests: Local PDF bookmarks

Run on Android with a readable multi-page PDF.

| ID | Scenario | Expected result |
|---|---|---|
| F29-01 | Tap Bookmark current page | Current page is stored locally |
| F29-02 | Bookmark the same page again | Existing bookmark is removed (toggle behavior) |
| F29-03 | Add bookmarks on several pages | Bookmark list shows all saved page numbers in ascending order |
| F29-04 | Tap Bookmarks with none saved | Informational message appears; no crash |
| F29-05 | Open Bookmarks and select a page | Viewer jumps to that page |
| F29-06 | Reopen the same PDF | Bookmarks remain saved |
| F29-07 | Open a different PDF | Its bookmarks are separate |
| F29-08 | Use page jump or Previous/Next after opening a bookmark | Page controls remain synchronized |
| F29-09 | Use airplane mode | Bookmark save/load works offline |

## Current limitations

- Bookmarks are local page numbers keyed to the selected document URI.
- No bookmark labels, notes, cloud sync or PDF annotation layer yet.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F29-01 through F29-09 on Android before marking device-verified.
