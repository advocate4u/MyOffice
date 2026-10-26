# F53 acceptance tests: Clear recent history

| ID | Scenario | Expected result |
|---|---|---|
| F53-01 | Tap Clear recent history | Confirmation dialog appears |
| F53-02 | Cancel confirmation | Recent list remains unchanged |
| F53-03 | Confirm with multiple recent items | All recent entries are removed |
| F53-04 | Inspect source documents after clearing | Original documents still exist |
| F53-05 | Reopen MyOffice | Cleared entries do not return |
| F53-06 | Open a file again through the picker | That file is added back to recent history |
| F53-07 | Clear history while a search query is active | Empty state is shown and the app remains usable |
| F53-08 | Use in airplane mode | History clearing works offline |

## Current limitations

- Clearing affects only MyOffice's local recent-file list. It does not delete files, notes, Writer documents, sheets or presentations.
- Device acceptance tests remain pending.
