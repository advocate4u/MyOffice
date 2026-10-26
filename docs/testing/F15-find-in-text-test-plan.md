# F15 acceptance tests: Find in text preview

Run on Android with a small text file containing repeated words.

| ID | Scenario | Expected result |
|---|---|---|
| F15-01 | Open a text file | Find field and Find in text action appear |
| F15-02 | Search a word appearing once | One match is highlighted |
| F15-03 | Search a word appearing multiple times | All matches are highlighted and count is shown |
| F15-04 | Search with different letter case | Matches are found case-insensitively |
| F15-05 | Search for a missing word | “No matches found” appears |
| F15-06 | Search with empty query | Prompt asks for text; preview remains readable |
| F15-07 | Clear query and search again | Original text returns when query is empty |
| F15-08 | Search Hindi text | Matching Devanagari text is highlighted where exact character sequences match |
| F15-09 | Use in airplane mode | Search works offline |

## Current limitations

- Search applies only to the visible text preview, capped at 1 MiB.
- No next/previous navigation, whole-word option or document-wide indexing.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F15-01 through F15-09 on Android before marking device-verified.
