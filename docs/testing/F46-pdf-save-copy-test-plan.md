# F46 acceptance tests: PDF Save a Copy

Run on Android with a readable PDF opened from a document provider.

| ID | Scenario | Expected result |
|---|---|---|
| F46-01 | Tap Save a copy | Android Save document picker opens with a PDF filename |
| F46-02 | Save to a writable local destination | PDF bytes are copied and success is shown |
| F46-03 | Compare source and copy | PDF content is unchanged |
| F46-04 | Cancel the save picker | Viewer remains usable; no success toast |
| F46-05 | Destination stream is unavailable | Clear error appears |
| F46-06 | Save from a provider URI | Source remains unchanged |
| F46-07 | Save to a cloud provider | Android/provider handles destination; MyOffice does not directly upload |
| F46-08 | Use airplane mode with local source/destination | Copy works offline |
| F46-09 | Reopen the saved copy | PDF renders in MyOffice |

## Current limitations

- This copies the PDF as-is; it does not compress, annotate or convert it.
- Cloud destinations may require connectivity in the system provider.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F46-01 through F46-09 on Android before marking device-verified.
