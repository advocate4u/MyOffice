# F8 acceptance tests: Export notes and Writer text

Run on the debug APK on Android.

| ID | Scenario | Expected result |
|---|---|---|
| F8-01 | Tap Export as .txt in a note | Android Save document picker opens with a note-based filename |
| F8-02 | Save exported note to local storage | UTF-8 text file contains note title, blank line and body |
| F8-03 | Export Writer document | Save picker opens; exported file contains current editor text |
| F8-04 | Cancel the Save picker | Editor remains usable; no false success message |
| F8-05 | Export Hindi text and emoji | Exported UTF-8 text preserves characters |
| F8-06 | Use export in airplane mode | Local destination works without internet |
| F8-07 | Choose a destination that cannot be written | Clear error appears |
| F8-08 | Use a title containing filesystem-special characters | Suggested filename replaces invalid characters safely |

## Build verification

- GitHub Actions run completes successfully.
- Artifact `MyOffice-debug-apk` contains `app-debug.apk`.
- Execute F8-01 through F8-08 on Android before marking device-verified.
