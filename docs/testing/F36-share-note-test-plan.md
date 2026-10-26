# F36 acceptance tests: Share note

Run on Android with a note and at least one compatible share target.

| ID | Scenario | Expected result |
|---|---|---|
| F36-01 | Tap Share note | Android share chooser opens |
| F36-02 | Select a messaging/email app | Title is provided as subject and title/body as shared text |
| F36-03 | Share unsaved edits | Current editor contents are shared |
| F36-04 | Cancel the chooser | Return to the note editor without error |
| F36-05 | Share Hindi and emoji text | Text is preserved |
| F36-06 | No compatible share target exists | Clear error appears; no crash |
| F36-07 | Use airplane mode | Share intent can be prepared without MyOffice network access |

## Current limitations

- Android and the selected receiving app control actual delivery.
- Sharing text does not embed attachments or automatically save the note.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F36-01 through F36-07 on Android before marking device-verified.
