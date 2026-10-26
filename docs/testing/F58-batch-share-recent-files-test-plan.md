# F58 acceptance tests: Batch sharing from recents

| ID | Scenario | Expected result |
|---|---|---|
| F58-01 | Tap Select files | Selection checkboxes appear on visible recent items |
| F58-02 | Select one file | Share selected count becomes 1 and action is enabled |
| F58-03 | Select multiple files | Count reflects the selected items |
| F58-04 | Tap Share selected | Android share chooser opens with all selected URIs |
| F58-05 | Share files with the same MIME type | Chooser receives the common MIME type |
| F58-06 | Share mixed file types | Chooser receives a generic MIME type and all selected URIs |
| F58-07 | Cancel selection mode | Checkboxes disappear and selected items are cleared |
| F58-08 | Change search/filter during selection | Selection remains associated with URI values; only visible files are shown |
| F58-09 | Cancel share chooser | MyOffice returns without modifying source files |
| F58-10 | Use in airplane mode | Share intent is created locally; delivery depends on the receiving app |
| F58-11 | A selected provider URI becomes unavailable | Error is shown and the app remains usable |

## Current limitations

- MyOffice hands URI references to Android; the selected recipient controls actual delivery.
- Selected items are kept in memory and are cleared when selection mode is cancelled or the Activity is recreated.
- Device acceptance tests remain pending.
