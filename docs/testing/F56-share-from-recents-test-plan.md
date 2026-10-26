# F56 acceptance tests: Share from recent files

| ID | Scenario | Expected result |
|---|---|---|
| F56-01 | Tap Share file on a recent item | Android share chooser opens |
| F56-02 | Select a compatible app | The selected file is offered to that app with read permission |
| F56-03 | Cancel the chooser | MyOffice returns without error |
| F56-04 | Share a provider-backed file | Recipient can read the URI while access is granted |
| F56-05 | Share a text document | Text-sharing behavior uses a text payload when supported |
| F56-06 | Share a PDF or binary document | URI-sharing behavior is used where supported |
| F56-07 | Inspect original document after sharing | MyOffice does not modify the source |
| F56-08 | Share while filtered/searched | The selected recent item is the one shared |
| F56-09 | Use airplane mode | MyOffice can create the share intent offline; delivery depends on the recipient app |
| F56-10 | Source permission has been revoked | A clear error appears and the app remains usable |

## Limitations

Delivery depends on Android and the receiving app. Device acceptance testing remains pending.
