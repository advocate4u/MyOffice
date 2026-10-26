# F23 acceptance tests: Home shortcut tiles

Run on Android on a phone-sized display and a larger display if available.

| ID | Scenario | Expected result |
|---|---|---|
| F23-01 | Launch MyOffice | Home shows a prominent Open a file action and compact two-column shortcut tiles |
| F23-02 | Tap Create note | Note editor opens |
| F23-03 | Tap Writer | Writer list opens |
| F23-04 | Tap My notes | Saved note list opens |
| F23-05 | Tap Spreadsheet | CSV sheet list opens |
| F23-06 | Tap Presentation | Slide deck list opens |
| F23-07 | Tap Images to PDF | Android image picker opens |
| F23-08 | Tap Refresh recent files | Recents refresh and home re-renders |
| F23-09 | Use TalkBack / keyboard focus | Shortcut tiles are focusable and actionable |
| F23-10 | Use narrow screen / large font | Tiles remain readable without horizontal overflow |
| F23-11 | Use in airplane mode | Local shortcuts remain available offline |

## Current limitations

- This is an original, lightweight home layout inspired by familiar office-suite navigation; it does not reproduce WPS protected assets or exact proprietary UI.
- Recent items remain below the shortcut area.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F23-01 through F23-11 on Android before marking device-verified.
