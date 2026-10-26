# F30 acceptance tests: Offline PDF page notes

Run on Android with a readable local PDF.

| ID | Scenario | Expected result |
|---|---|---|
| F30-01 | Open a PDF page and tap Page note | Multiline note editor opens for the current page |
| F30-02 | Save a non-empty note | Note is stored locally and a confirmation appears |
| F30-03 | Reopen Page note on the same page | Previously saved note text appears |
| F30-04 | Navigate to another page and open Page note | Notes are independent per page |
| F30-05 | Reopen the same PDF URI later | Saved page notes remain available |
| F30-06 | Save an empty note | Existing note is removed |
| F30-07 | Tap Delete note | Note is removed |
| F30-08 | Tap Cancel | Changes in the dialog are not saved |
| F30-09 | Enter Hindi and emoji | Text is preserved |
| F30-10 | Use airplane mode | Notes save and load offline |

## Current limitations

- Notes are stored locally and keyed to the selected PDF URI plus page index.
- Notes are not embedded into the PDF and do not appear in other PDF applications.
- No cloud sync or note export is included yet.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F30-01 through F30-10 on Android before marking device-verified.
