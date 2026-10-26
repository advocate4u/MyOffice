# F7 acceptance tests: Local image-to-PDF conversion

Run on the debug APK with airplane mode enabled. Use test images you own or are authorized to process.

| ID | Scenario | Expected result |
|---|---|---|
| F7-01 | Tap Images to PDF | Android image picker opens |
| F7-02 | Select one image | A PDF save picker opens after conversion |
| F7-03 | Select multiple images | Output PDF has one page per selected image in selection order |
| F7-04 | Select landscape and portrait images | Pages use corresponding landscape/portrait dimensions |
| F7-05 | Save PDF to a writable local destination | File is saved and MyOffice opens its PDF preview |
| F7-06 | Cancel image picker | App returns to home without error |
| F7-07 | Cancel save picker | Temporary PDF is discarded; app remains usable |
| F7-08 | Select an invalid/unreadable image | Clear conversion error; no crash |
| F7-09 | Select a very large image | Decode is sampled to reduce memory pressure |
| F7-10 | Select more than 20 images | App explains the limit and does not start conversion |
| F7-11 | Use in airplane mode | Conversion works without internet |
| F7-12 | Inspect app manifest | No camera or internet permission is needed for image-import conversion |

## Current limitations

- This imports existing images; it is not yet a live camera scanner.
- No crop/perspective correction, image enhancement, OCR, searchable text or compression settings.
- Output is saved through Android's Save document picker; cloud providers may show up in the system picker, but MyOffice does not upload directly itself.

## Build verification

- GitHub Actions run completes successfully.
- Artifact `MyOffice-debug-apk` contains `app-debug.apk`.
- Install and run F7-01 through F7-12 on Android before marking this feature device-verified.
