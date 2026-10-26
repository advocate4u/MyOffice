# F14 acceptance tests: Import text into Writer as a copy

Run on Android with small UTF-8 text, Markdown or log files.

| ID | Scenario | Expected result |
|---|---|---|
| F14-01 | Open a small TXT file | Text preview appears |
| F14-02 | Tap Edit a copy in Writer | Writer opens with the text content |
| F14-03 | Edit and save the copy | New local Writer document is created |
| F14-04 | Recheck original source file | Original bytes remain unchanged |
| F14-05 | Import Hindi/emoji text | Unicode content remains intact |
| F14-06 | Open a file over 1 MiB | Preview is limited; Writer import action reports the limit |
| F14-07 | Cancel file picker | App remains usable |
| F14-08 | Use airplane mode with local file | Preview and local-copy editing work offline |

## Current limitations

- Import creates a local copy; it does not edit or overwrite the selected source.
- Writer still supports plain text only, not rich-text formatting or DOCX.
- Large files are not imported into Writer in this milestone.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F14-01 through F14-08 on Android before marking device-verified.
