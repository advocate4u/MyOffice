# F37 acceptance tests: Duplicate note

Run on Android with a saved note that can safely be copied.

| ID | Scenario | Expected result |
|---|---|---|
| F37-01 | Tap Duplicate note | A new note is created with “(copy)” appended to the title |
| F37-02 | Compare original and copy bodies | Body content matches |
| F37-03 | Edit the copy and save | Original remains unchanged |
| F37-04 | Delete the copy | Original remains available |
| F37-05 | Duplicate an untitled note | Copy gets a readable “Untitled note (copy)” title |
| F37-06 | Duplicate a note containing Hindi/emoji | Text is preserved |
| F37-07 | Search notes, then duplicate a result | New copy appears after list refresh |
| F37-08 | Storage write fails | Clear error appears; app remains usable |
| F37-09 | Use airplane mode | Duplication works offline |

## Current limitations

- Duplicating creates a new local note file and uses a generic copy suffix; it does not duplicate attachments.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F37-01 through F37-09 on Android before marking device-verified.
