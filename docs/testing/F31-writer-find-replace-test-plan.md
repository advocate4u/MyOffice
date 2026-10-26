# F31 acceptance tests: Writer Find & Replace

Run on Android with a disposable text document.

| ID | Scenario | Expected result |
|---|---|---|
| F31-01 | Tap Find & Replace | Dialog shows find and replacement fields |
| F31-02 | Replace one exact match | Matching text is replaced |
| F31-03 | Replace multiple matches | All non-overlapping exact matches are replaced |
| F31-04 | Search text is empty | Validation message appears; document remains unchanged |
| F31-05 | Search text is absent | “No matching text found” appears; document remains unchanged |
| F31-06 | Replacement text is empty | All matching text is removed |
| F31-07 | Replace Hindi text/emoji | Unicode text remains intact |
| F31-08 | Replace and then leave editor | Unsaved-change prompt detects the replacement |
| F31-09 | Save after replacement and reopen | Replacements persist |
| F31-10 | Use airplane mode | Find & Replace works offline |

## Current limitations

- Matching is exact and case-sensitive.
- No regular expressions, whole-word matching, incremental search or undo stack yet.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F31-01 through F31-10 on Android before marking device-verified.
