# F59 acceptance tests: Select/deselect visible recent files

| ID | Scenario | Expected result |
|---|---|---|
| F59-01 | Enter file-selection mode | Select visible action becomes enabled when items are visible |
| F59-02 | Tap Select visible | All items currently visible in the recent list become selected |
| F59-03 | Tap Deselect visible | Only currently visible selected items are deselected |
| F59-04 | Apply filename search, then select visible | Only matching visible files are bulk-selected |
| F59-05 | Apply PDF/CSV/Office filter, then select visible | Only items in the current type filter are selected |
| F59-06 | Enable Favorites only, then select visible | Only visible favorites are selected |
| F59-07 | Change filters after selecting items | Selections hidden by the filter remain selected |
| F59-08 | Cancel selection mode | All selections clear and checkboxes disappear |
| F59-09 | Inspect Share selected count | Count reflects all selected URI entries, including entries hidden by current filters |
| F59-10 | Use in airplane mode | Selection and bulk-select work offline |

## Current limitations

- Selection state is in-memory and is not retained across Activity recreation.
- Device acceptance tests remain pending.
