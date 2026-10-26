# F13 acceptance tests: Recent-file search

Run on Android after opening several files through the system picker.

| ID | Scenario | Expected result |
|---|---|---|
| F13-01 | Open home after adding recent files | Search field and recent list appear |
| F13-02 | Enter a partial filename | Only matching recent files remain visible |
| F13-03 | Search with different letter case | Matching is case-insensitive |
| F13-04 | Search for a name with no matches | “No recent files match that name” appears |
| F13-05 | Clear the search text | Full recent list returns |
| F13-06 | Tap a filtered result | Correct file opens |
| F13-07 | Use search in airplane mode | Filtering works offline |
| F13-08 | Use search with no recent files | Empty-state message appears; app remains usable |
| F13-09 | Inspect network permissions | Search does not require network access |

## Current limitations

- Search filters recent-file names only; it does not index document contents or search all device storage.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F13-01 through F13-09 on Android before marking device-verified.
