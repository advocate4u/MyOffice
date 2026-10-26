# F2 acceptance tests: Offline notes

Run on the debug APK, including airplane mode. Prefer a real-device pass on Samsung Galaxy S21 FE / Android before marking device-verified.

| ID | Scenario | Expected result |
|---|---|---|
| F2-01 | Launch in airplane mode and tap Create a note | Editor opens without login or network |
| F2-02 | Save a note with an English title/body | Save confirmation appears and note is listed |
| F2-03 | Save Hindi text (Devanagari) and emoji | Characters persist without corruption after reopen |
| F2-04 | Open a saved note from My notes | Title and body load into editable fields |
| F2-05 | Edit a saved note and save again | Existing note is updated, not duplicated |
| F2-06 | Back out after changing title/body | Unsaved-changes dialog appears |
| F2-07 | Choose Keep editing in dialog | Editor remains open with entered content |
| F2-08 | Choose Discard in dialog | Changes are discarded and notes list opens |
| F2-09 | Save with blank title | Note is saved as “Untitled note” |
| F2-10 | Force-stop and relaunch app | Previously saved notes remain available |
| F2-11 | Create several notes | Notes list sorts by last modified time, newest first |
| F2-12 | Use core notes while offline | Create/read/edit/save works without internet |
| F2-13 | Inspect manifest permissions | No network permission is required for core notes |
| F2-14 | Simulate storage write failure / low space if feasible | User sees a clear save error; existing note is not silently reported as saved |

## Build verification

- GitHub Actions run completes successfully.
- Artifact `MyOffice-debug-apk` contains `app-debug.apk`.
- Install on Android and execute F2-01 through F2-14 before marking F2 device-verified.
