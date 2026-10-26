# F77 — Export current PDF page as PNG test plan

## Scope
Export the currently displayed PDF page as a PNG image, respecting the current per-document viewer rotation. The original PDF must remain unchanged.

## Manual acceptance checks
1. Open a readable local PDF with at least three pages.
2. Navigate to page 2 and choose **Export current page as PNG**.
3. In the system Save document picker, choose a writable destination and save.
4. Open the resulting PNG in Gallery or another image viewer. Confirm it shows page 2, not page 1, and is legible.
5. Rotate the PDF view by 90°, export the current page again, and confirm the saved PNG uses the same orientation as the viewer.
6. Cancel the Save document picker; confirm the app returns without a crash.
7. Try a missing/unreadable or encrypted PDF; confirm a clear error appears and the app remains usable.
8. Reopen the source PDF and verify its bytes/content were not changed by export.
9. Repeat with a long page and a small phone display; confirm the UI remains responsive while the PNG is prepared.

## Build/verification
- CI must compile the Android app and upload the debug test APK.
- When configured, CI must separately sign and verify the production release APK.
- Device-level acceptance on a physical Android device remains required; a successful CI build alone does not establish runtime behavior.
