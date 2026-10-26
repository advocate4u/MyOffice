# F54 acceptance tests: Dedicated favorites screen

| ID | Scenario | Expected result |
|---|---|---|
| F54-01 | Tap Favorite files on Home with no favorites | Empty-state message explains how to add favorites |
| F54-02 | Star a recent document and open Favorite files | Document appears in the favorites list |
| F54-03 | Search favorites by partial filename | Only matching favorites are shown |
| F54-04 | Clear favorite search | Full favorite list returns |
| F54-05 | Tap Open favorite | Selected document opens and is added to recent history |
| F54-06 | Tap Remove favorite | Star is removed; source document is unchanged |
| F54-07 | Close and reopen MyOffice | Favorites remain saved locally |
| F54-08 | Remove a favorite whose provider URI is no longer readable | App does not crash; show a recoverable error or omit unavailable metadata |
| F54-09 | Use in airplane mode with local documents | Favorites list and search work offline |
| F54-10 | Press Android Back from favorites | Returns to the previous screen |

## Current limitations

- Favorites are stored per app installation and identified by URI.
- Favorites are not synchronized to cloud storage.
- Device acceptance tests remain pending.
