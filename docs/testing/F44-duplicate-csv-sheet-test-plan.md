# F44 acceptance tests: Duplicate CSV sheet

Run on Android with a saved local CSV sheet that can safely be copied.

| ID | Scenario | Expected result |
|---|---|---|
| F44-01 | Tap Duplicate sheet | A new local CSV file is created |
| F44-02 | Compare original and duplicate bytes | Files are identical immediately after copy |
| F44-03 | Edit the duplicate and save | Original remains unchanged |
| F44-04 | Delete the duplicate | Original remains available |
| F44-05 | Duplicate CSV with Hindi/emoji/quoted commas | Bytes and text remain intact |
| F44-06 | Search the list then duplicate a result | List refreshes and new sheet is available |
| F44-07 | Storage write fails | Partial target is cleaned up when possible and an error appears |
| F44-08 | Use airplane mode | Duplication works offline |

## Current limitations

- Duplicate creates a new MyOffice-local CSV file; it does not duplicate the external source file selected through Android's picker.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F44-01 through F44-08 on Android before marking device-verified.
