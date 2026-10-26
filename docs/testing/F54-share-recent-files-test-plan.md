# F54 acceptance tests: Share recent files

| ID | Scenario | Expected result |
|---|---|---|
| F54-01 | Tap Share file on a recent PDF | Android share chooser opens with PDF MIME type |
| F54-02 | Tap Share file on a text file | Android share chooser opens with the detected MIME type |
| F54-03 | Select a compatible target app | Target receives the selected URI with temporary read permission |
| F54-04 | Cancel the share chooser | MyOffice returns without changing the source file |
| F54-05 | Share a file from a document provider | Receiving app can read the URI while permission is granted |
| F54-06 | Share a file with unknown MIME type | Generic file share is attempted |
| F54-07 | No compatible share app exists | A clear message is shown; app remains usable |
| F54-08 | Use in airplane mode | Share intent creation works without MyOffice network access |

## Current limitations

- Delivery depends on the selected receiving app.
- MyOffice does not upload or copy the source document itself.
- Device acceptance tests remain pending.
