# F21 acceptance tests: Remove from recents

Run on Android using files that can safely be reopened.

| ID | Scenario | Expected result |
|---|---|---|
| F21-01 | Tap Remove from recent on a recent item | Item disappears from the list |
| F21-02 | Check the original local file after removal | Original file still exists and remains unchanged |
| F21-03 | Reopen MyOffice | Removed history entry stays removed |
| F21-04 | Open the same file again through the picker | It returns to recents |
| F21-05 | Remove a filtered search result | It disappears without clearing the search query |
| F21-06 | Remove the final recent item | Empty-state message appears |
| F21-07 | Tap the file card itself | File still opens normally |
| F21-08 | Use in airplane mode | History management works offline |

## Current limitations

- Recent history is local to this installation; it is not synchronized across devices.
- Removal affects history only and does not delete the source document.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F21-01 through F21-08 on Android before marking device-verified.
