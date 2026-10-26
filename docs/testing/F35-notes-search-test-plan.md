# F35 acceptance tests: Notes library search

Run on Android with several saved notes containing different titles and text.

| ID | Scenario | Expected result |
|---|---|---|
| F35-01 | Open My notes | Search field and saved notes appear |
| F35-02 | Search a title phrase | Only matching notes appear |
| F35-03 | Search text inside note content | Matching note appears |
| F35-04 | Search using different letter case | Search is case-insensitive |
| F35-05 | Search with no matches | “No notes match your search” appears |
| F35-06 | Clear search | All saved notes return |
| F35-07 | Open a filtered note | Correct note opens |
| F35-08 | Delete a filtered note | Confirmation appears and list refreshes |
| F35-09 | Use Hindi/emoji search text | Search remains usable |
| F35-10 | Use airplane mode | Search works offline |

## Current limitations

- Search is local and limited to saved notes.
- No advanced search operators or full-device search.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F35-01 through F35-10 on Android before marking device-verified.
