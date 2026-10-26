# F53 acceptance tests: Recent-file favorites

| ID | Scenario | Expected result |
|---|---|---|
| F53-01 | Tap Add to favorites on a recent document | A star action changes to Remove favorite |
| F53-02 | Tap Favorites only | The recent list shows only favorited entries |
| F53-03 | Exit and reopen MyOffice | Favorite state is restored locally |
| F53-04 | Remove a favorite | The document remains in recents but is no longer marked favorite |
| F53-05 | Remove a recent entry that is favorited | It is removed from recents and its favorite entry is cleared |
| F53-06 | Clear Favorites only | All recent items matching the current search/type filter return |
| F53-07 | Search while Favorites only is enabled | Search and favorites filters work together |
| F53-08 | Sort while Favorites only is enabled | Favorite results follow the selected sort order |
| F53-09 | Use in airplane mode | Favorite changes and filtering work offline |
| F53-10 | Inspect original document | Favorite changes do not edit or delete source file contents |

## Current limitations

- Favorites are local to this installation and are keyed by the selected document URI.
- If a document provider changes a file's URI, it may need to be favorited again.
- Device acceptance tests remain pending.
