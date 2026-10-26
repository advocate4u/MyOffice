# F38 acceptance tests: Notes sorting

Run on Android with several saved notes having different titles and modified dates.

| ID | Scenario | Expected result |
|---|---|---|
| F38-01 | Open My notes | Default order is most recently modified first |
| F38-02 | Tap Sort | Notes sort by title A–Z |
| F38-03 | Tap Sort again | Modified-newest order returns |
| F38-04 | Search while sorted by title | Search remains active and filtered results keep title order |
| F38-05 | Duplicate a note while title-sorted | List refreshes and preserves selected sort |
| F38-06 | Delete a note while sorted | Only selected note is deleted after confirmation |
| F38-07 | Use Hindi titles | Alphabetic sort remains stable and does not crash |
| F38-08 | Use airplane mode | Sorting works offline |

## Current limitations

- Sort preference is session-only and resets to modified-newest after leaving/reopening the Notes screen.
- Title sorting reads each local note's title to compare items.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F38-01 through F38-08 on Android before marking device-verified.
