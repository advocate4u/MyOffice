# F22 acceptance tests: Recent file sorting

Run on Android with at least three local files of different names and sizes.

| ID | Scenario | Expected result |
|---|---|---|
| F22-01 | Open home with recent items | Default order is newest opened first |
| F22-02 | Tap Sort once | Items sort by filename A–Z |
| F22-03 | Tap Sort again | Items sort by largest file first |
| F22-04 | Tap Sort again | Newest-first order returns |
| F22-05 | Search while sorted by name | Search filters the sorted results without resetting order |
| F22-06 | Remove an item while sorted | Only that history entry disappears |
| F22-07 | Include a provider with unknown file sizes | Sorting remains stable and does not crash |
| F22-08 | Relaunch app | App returns to default newest-first order; sort preference is session-only |
| F22-09 | Use airplane mode | Sorting works offline |

## Current limitations

- File size may be unknown for some document providers; such items are ordered using the provider's reported size value.
- Sort choice currently resets to newest-first after app restart.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F22-01 through F22-09 on Android before marking device-verified.
