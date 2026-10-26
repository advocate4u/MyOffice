# F45 acceptance tests: Duplicate Writer document

Run on Android with a saved local plain-text Writer document.

| ID | Scenario | Expected result |
|---|---|---|
| F45-01 | Tap Duplicate document | A new local .txt document is created |
| F45-02 | Compare original and duplicate bytes | Files match immediately after copying |
| F45-03 | Edit and save the duplicate | Original remains unchanged |
| F45-04 | Delete the duplicate | Original remains available |
| F45-05 | Duplicate Hindi/emoji content | Text remains intact |
| F45-06 | Search the Writer list then duplicate a result | List refreshes and new copy appears |
| F45-07 | Storage write fails | Partial target is cleaned up when possible and an error appears |
| F45-08 | Use airplane mode | Duplication works offline |

## Current limitations

- This duplicates MyOffice's internal plain-text Writer format, not DOCX files selected from external providers.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F45-01 through F45-08 on Android before marking device-verified.
