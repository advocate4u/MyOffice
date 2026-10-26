# F55 acceptance tests: Favorites-first recent sorting

| ID | Scenario | Expected result |
|---|---|---|
| F55-01 | Open Home with both favorites and non-favorites | Sort control is available |
| F55-02 | Cycle sort modes to Favorites first | Favorites appear before all non-favorites |
| F55-03 | Compare items within favorites | Newer recent entries appear first |
| F55-04 | Compare items within non-favorites | Newer recent entries appear first |
| F55-05 | Change favorite status while Favorites first is active | List rerenders with the item in the correct group |
| F55-06 | Search while Favorites first is active | Search results remain sorted with favorites first |
| F55-07 | Apply a file-type filter while Favorites first is active | Filtered results retain the favorites-first ordering |
| F55-08 | Restart MyOffice | Favorite membership persists; selecting Favorites first works again |
| F55-09 | Remove a file from recent history | It is absent from the sorted list |
| F55-10 | Use offline | Sorting works without network access |

## Limitations

Sort mode is currently an in-session preference; the favorite list persists locally. Verify on a real device before marking complete.
