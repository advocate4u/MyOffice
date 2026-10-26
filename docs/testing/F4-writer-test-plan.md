# F4 acceptance tests: Writer plain-text documents

Run on the debug APK, including airplane mode. This is the first Writer milestone, not rich-text or DOCX compatibility.

| ID | Scenario | Expected result |
|---|---|---|
| F4-01 | Tap Writer from home | Writer document list opens |
| F4-02 | Create a new text document | Empty editor opens without login/network |
| F4-03 | Enter Hindi, English, punctuation and emoji | Characters appear and remain intact |
| F4-04 | Save a new document | It appears in the Writer list |
| F4-05 | Reopen and edit existing document | Existing file updates on save rather than duplicating |
| F4-06 | Back out with unsaved changes | Confirmation offers keep editing, discard or save |
| F4-07 | Tap Share text | Android share chooser opens with current text |
| F4-08 | Force-stop and relaunch | Saved documents remain available |
| F4-09 | Use Writer in airplane mode | Create/read/edit/save works offline |
| F4-10 | Save blank document | Blank document is saved and listed as Untitled document |
| F4-11 | Simulate low storage/write failure if feasible | Clear error appears; no success message is shown |
| F4-12 | Inspect manifest permissions | Core Writer needs no internet permission |

## Current limitations

- Plain text only: no bold/italic/paragraph formatting, page layout, tables, images or spellcheck.
- No DOC/DOCX parsing or export; file storage is UTF-8 `.txt`.
- Share sends text through Android's share sheet; it does not create a formatted office document.

## Build verification

- GitHub Actions run completes successfully.
- Artifact `MyOffice-debug-apk` contains `app-debug.apk`.
- Install and run F4-01 through F4-12 on Android before marking Writer device-verified.
