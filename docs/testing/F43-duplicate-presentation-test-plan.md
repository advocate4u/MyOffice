# F43 acceptance tests: Duplicate presentation

Run on Android with a saved local text slide deck.

| ID | Scenario | Expected result |
|---|---|---|
| F43-01 | Tap Duplicate deck | A new slide deck is created |
| F43-02 | Compare slide count and contents | All slides and bodies are copied |
| F43-03 | Check first-slide title | “(copy)” suffix is appended |
| F43-04 | Edit the copy and save | Original remains unchanged |
| F43-05 | Delete the copy | Original remains available |
| F43-06 | Duplicate a deck with empty title | Copy receives “Untitled presentation (copy)” |
| F43-07 | Duplicate a deck with Hindi/emoji | Text is preserved |
| F43-08 | Duplicate while searching | List refreshes and new copy is available |
| F43-09 | Storage write fails | Clear error appears; app remains usable |
| F43-10 | Use airplane mode | Duplication works offline |

## Current limitations

- Duplicates only MyOffice's local text-deck format; it does not import or export PPTX.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F43-01 through F43-10 on Android before marking device-verified.
