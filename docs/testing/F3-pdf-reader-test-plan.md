# F3 acceptance tests: Offline PDF reader

Run on the debug APK with airplane mode enabled. Use legally obtained test PDFs and do not mark device-verified until tested on a real Android device.

| ID | Scenario | Expected result |
|---|---|---|
| F3-01 | Open a valid one-page PDF from local storage | First page renders and label says Page 1 of 1 |
| F3-02 | Open a multi-page PDF | First page renders; Next page advances and label updates |
| F3-03 | Navigate to last page | Next page is disabled; Previous page remains enabled |
| F3-04 | Navigate back to first page | Previous page is disabled |
| F3-05 | Open PDF from a document provider in airplane mode | Works if provider grants a readable local file descriptor |
| F3-06 | Open encrypted/password-protected PDF | Clear unsupported/error message; app remains usable |
| F3-07 | Open malformed/empty PDF | Error message; no crash |
| F3-08 | Cancel file picker | Home remains usable |
| F3-09 | Press Back to MyOffice from reader | Returns to home |
| F3-10 | Open a large or unusually tall page | Rendering is capped to a bounded raster size; app should not crash |
| F3-11 | Inspect app permissions/network use | Core reader does not require internet or upload the PDF |
| F3-12 | Open unsupported provider URI | Reader displays a recoverable error |

## Build verification

- GitHub Actions run completes successfully.
- Artifact `MyOffice-debug-apk` contains `app-debug.apk`.
- Install and run F3-01 through F3-12 on Android before marking the reader device-verified.

## Current limitation

This is a basic page renderer only. PDF text search, selection/copy, zoom controls, annotation, signing, OCR, page manipulation and conversion are not implemented.
