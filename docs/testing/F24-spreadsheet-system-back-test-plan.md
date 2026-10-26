# F24 acceptance tests: Spreadsheet system Back

Run on Android using a disposable CSV sheet.

| ID | Scenario | Expected result |
|---|---|---|
| F24-01 | Open sheet and press system Back without edits | Return to spreadsheet list |
| F24-02 | Edit a cell and press system Back | Unsaved-changes dialog appears |
| F24-03 | Choose Keep editing | Editor remains open and cell value is preserved |
| F24-04 | Choose Discard | Return to list without saving the edits |
| F24-05 | Choose Save | Sheet saves and returns to list |
| F24-06 | Add row/column then press system Back | New structure is included in change detection |
| F24-07 | Use the in-screen Back control | Same save/discard/keep-editing options appear |
| F24-08 | Use airplane mode | Protection works offline |
| F24-09 | Open a different app screen and press system Back | Existing screen navigation remains unchanged |

## Current limitations

- Android may hide or change system Back behavior depending on navigation mode; test gesture and button navigation.
- Device-level validation is required before release.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F24-01 through F24-09 on Android before marking device-verified.
