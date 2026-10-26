# F11 acceptance tests: Share PDF

Run on Android with at least one readable local PDF and a compatible share target.

| ID | Scenario | Expected result |
|---|---|---|
| F11-01 | Open a readable PDF and tap Share PDF | Android share chooser opens |
| F11-02 | Select a compatible recipient app | Recipient receives a readable PDF URI with temporary read permission |
| F11-03 | Cancel share chooser | MyOffice returns to the PDF reader without error |
| F11-04 | Share a PDF from a document provider | Recipient can read the document while permission is granted |
| F11-05 | Open a PDF that cannot be read | Clear preview error; no false sharing success |
| F11-06 | Use in airplane mode with local files | Share intent can be created without MyOffice network access |

## Current limitations

- MyOffice hands the URI to Android's share system; availability and delivery depend on the receiving app.
- This does not create a new copy or modify the source PDF.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F11-01 through F11-06 on Android before marking device-verified.
