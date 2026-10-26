# F87 — WPS-inspired Home UI polish test plan

Version target: 0.4.9 (versionCode 13)

## Build checks
- [ ] GitHub Actions debug build completes.
- [ ] APK package identity is `org.myoffice.app.debug` and version is `0.4.9-debug`.
- [ ] Artifact is clearly labelled DEBUG-TEST-ONLY; it is not a production update APK.
- [ ] Release artifact is published only if persistent MyAdv signing secrets are configured.

## Device checks (Android 8+)
- [ ] Home canvas uses a light neutral background and controls remain readable.
- [ ] Open a file remains the most visually prominent primary action.
- [ ] Shortcut tiles have rounded white cards and readable title/subtitle hierarchy.
- [ ] Action buttons use consistent blue-tinted styling and remain comfortably tappable.
- [ ] Tap Create note, Writer, My notes, Spreadsheet, Presentation, Images to PDF and Favorite files; each opens the correct existing workflow.
- [ ] Recent search, sorting, filters, favorites, selection and sharing still work.
- [ ] Verify in light/dark system settings and with large font/accessibility settings; note that this iteration primarily styles the app's existing light UI.
- [ ] Compare on-device with WPS Office's general workflow; do not claim pixel-perfect equivalence without device screenshots and review.
