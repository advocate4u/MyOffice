# F47 acceptance tests: Share entire presentation

Run on Android with a saved or unsaved multi-slide text deck and a compatible share target.

| ID | Scenario | Expected result |
|---|---|---|
| F47-01 | Tap Share entire presentation | Android share chooser opens |
| F47-02 | Inspect shared text | All slides appear in order with slide numbers |
| F47-03 | Include slide titles and bodies | Both are included for every slide |
| F47-04 | Share unsaved edits | Current editor text is included |
| F47-05 | Select a messaging/email app | Full deck text is passed to recipient |
| F47-06 | Cancel chooser | Return to editor without error |
| F47-07 | Share Hindi/emoji text | Text is preserved |
| F47-08 | Use airplane mode | Share intent can be prepared offline |

## Current limitations

- Shares plain text, not a PPTX file or rendered slides.
- Actual delivery depends on Android and the selected receiving app.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F47-01 through F47-08 on Android before marking device-verified.
