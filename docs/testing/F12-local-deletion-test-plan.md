# F12 acceptance tests: Delete local documents

Run on Android. Use only test notes/documents/sheets/decks that can safely be deleted.

| ID | Scenario | Expected result |
|---|---|---|
| F12-01 | Tap Delete note | Confirmation dialog appears |
| F12-02 | Cancel note deletion | Note remains in list |
| F12-03 | Confirm note deletion | Local note is removed from list |
| F12-04 | Delete Writer document | Confirmation then local document removed |
| F12-05 | Delete CSV sheet | Confirmation then internal sheet removed |
| F12-06 | Delete slide deck | Confirmation then local deck removed |
| F12-07 | Cancel deletion on any item type | Item remains |
| F12-08 | Try deleting an unavailable file | Error is shown; list remains usable |
| F12-09 | Delete an imported CSV sheet | Original picked source file remains unchanged |
| F12-10 | Use deletion in airplane mode | Local deletion works offline |

## Current limitations

- Deletion permanently removes the app-private copy; there is no trash/undo yet.
- This does not delete original documents picked from external providers.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F12-01 through F12-10 on Android before marking device-verified.
