# F78 — Export all PDF pages as PNG ZIP

## Acceptance checks
1. Open a readable PDF of 1–10 pages and choose **Export all pages as PNG ZIP**.
2. Save the ZIP from Android's document picker and extract it with the phone's file manager.
3. Confirm the archive contains one sequentially named PNG per PDF page, with no missing pages.
4. Compare each image against its source page for legibility and orientation.
5. Rotate the viewer to 90° and export again; confirm every image respects the rotation.
6. Cancel the save picker and confirm MyOffice remains usable.
7. Try a PDF with more than 100 pages; expect a clear safety-limit message and no incomplete ZIP.
8. Reopen the source PDF and confirm its content remains unchanged.
9. Confirm page rendering happens off the UI thread and the app remains responsive.

## CI/device verification
- CI must compile the debug APK and verify its package identity.
- If signing secrets are configured, CI must also verify and publish the signed production update APK.
- Physical-device checks are still required to validate document-picker behavior and output image quality.
