# WPS-to-MyOffice Feature Evidence Matrix (working draft)

Date: 2026-10-26  
Target requested for observation: WPS Office Android 26.10.0; verify exact installed build before version-specific conclusions.  
Status: public-documentation inventory + hypotheses; **not a completed hands-on reverse-engineering report**.

## Evidence rules

- `DOC`: official product/help page confirms that a capability is advertised, not the exact Android screen or every branch.
- `VIDEO-INDEX`: official-channel video title, date, and description are discoverable; the video has not yet been reviewed frame-by-frame, so do not infer its full UI sequence.
- `DEVICE`: reserved for a user-authorized direct observation on the target device, with version/build, Android version, network/account/subscription state and test-file details.
- `INFERRED`: candidate design or acceptance criteria to validate.
- `BLOCKED`: cannot verify from available evidence.

## Public source inventory

| Area | Public source | What it supports | What it does not prove |
|---|---|---|---|
| Android suite | https://www.wps.com/office/android/ | WPS advertises mobile document viewing/editing, PDF tools, cloud integrations and language support including Hindi. | Exact UI for version 26.10.0, offline behavior of each tool, or which functions require a plan. |
| Writer | https://www.wps.com/office/writer/ | Writer advertises document creation/editing, templates, conversion, and optional AI writing/summary/translation. | Android menu hierarchy, full compatibility, or exact entitlement gates. |
| Spreadsheet | https://www.wps.com/office/spreadsheet/ | Spreadsheet advertises data analysis, formulas, charts, and online conversion tools. | Android cell-editing gestures, formula coverage, workbook fidelity or offline branch behavior. |
| PDF Android | https://www.wps.com/feature/pdf-editor-for-android/ | Advertised PDF reading, annotation, signing, page organization and conversion; advanced features may be plan-dependent. | Exact build's paywall triggers or whether each feature is locally processed. |
| Scan to PDF | https://www.wps.com/feature/scan-to-pdf/ | Advertised camera/image import, crop/enhancement, multi-page capture, OCR and PDF export. | Actual permission flow, device compatibility, language coverage or whether every OCR path is offline. |
| Suite overview video | https://www.youtube.com/watch?v=iuV-I4iPgoM | Official WPS Office video published 2022-03-08; description names Writer, Spreadsheet, Presentation and PDF. | Current Android UI or the complete set of screens/features. |
| Worksheet video | https://www.youtube.com/watch?v=-BnUSCFh0nA | Official WPS Office video published 2021-08-30 titled “How to set a worksheet in WPS Spreadsheet.” | Current Android screen sequence; title/description alone is insufficient. |
| Slideshow video | https://www.youtube.com/watch?v=A1DFjJlbt44 | Official WPS Office video published 2021-08-30 titled “How to set up a slideshow in WPS Presentation.” | Current Android screen sequence; title/description alone is insufficient. |
| PDF conversion video | https://www.youtube.com/watch?v=2LCK9UlAZRc | Previously indexed official WPS video titled “How to convert PDF files into documents in WPS PDF”; description indicates conversion may be premium. | Exact current paywall/entitlement behavior or all conversion branches. |
| Writer numbering video | https://www.youtube.com/watch?v=-vFl0BFwoow | Previously indexed official WPS video titled “Automatic numbering in WPS Writer.” | Current Android UI or exact numbering behavior. |
| Presentation video insertion | https://www.youtube.com/watch?v=RA7HP9Kq_T0 | Previously indexed official WPS video titled “How to insert video in WPS Presentation.” | Android support, codecs, export behavior or current UI. |

## Feature-by-feature investigation order

This order is intentionally not a claim about WPS's own prioritization. It optimizes for a low-cost, offline-first education app and builds confidence in file safety before adding editors.

### F0 — App/build and local file access (prototype exists; not WPS-validated)
Cases:
- F0-01 launch with no recent files.
- F0-02 pick a local supported text file through Android Storage Access Framework.
- F0-03 cancel picker; deny/revoke access; reopen after process restart.
- F0-04 recent-file entry stale/unavailable; unsupported or large file; empty file.
- F0-05 open-with chooser and verify no write occurs unless user chooses an editor.
Acceptance: no crash; clear error; no broad storage permission; recents are robust; file bytes remain unchanged during preview.
Status: implementation exists in `MainActivity.java`; build passed on GitHub Actions, but phone tests and WPS comparison are pending.

### F1 — Home / file hub (must compare against WPS home before treating as complete)
Record visible tabs/cards, create (+) actions, sort/search/filter, file categories, recents/favourites, local/cloud boundaries, empty/loading/error states, back behavior and any ads/premium prompts.
Acceptance: reproduce only independently designed behavior that is supported by observed evidence; do not copy WPS branding/layout assets.

### F2 — Create and edit plain text / notes (education-first)
Probe new-file entry point, name/extension defaults, editor opening, text input, cursor/selection, undo/redo, save/save-as, back with unsaved changes, reopen and Unicode/Hindi text.
Acceptance: create/edit/save/reopen a local note without network; handle cancellation and duplicate names; verify persisted bytes and encoding.

### F3 — PDF reader and annotation
Probe open/share-in, page navigation, zoom, search, text selection/copy, bookmarks if available, highlight/pen/eraser, undo, save/copy, share, rotate/reorder/merge only after baseline reading is reliable.
Branches: encrypted PDF, malformed file, image-only PDF, no space, read-only provider, cancel save, paid prompt, offline.
Acceptance: never silently overwrite the source; label unsupported editing honestly; preserve original PDF on cancel/error.

### F4 — Writer compatibility
Probe DOC/DOCX/RTF/TXT import, formatting preservation, paragraphs, headings, bullets/numbering, tables, images, page setup, find/replace, save/export PDF, and reopen in another office app.
Acceptance: only claim format support after round-trip tests with a documented fixture set; unsupported formatting must be surfaced rather than silently dropped.

### F5 — Spreadsheet
Probe XLS/XLSX/CSV import; cell editing; formulas; fill; row/column insert/delete; sheet rename/add; sort/filter; freeze; charts; formatting; export; formula errors; date/locale/Hindi text.
Acceptance: calculation correctness and no data loss take priority over visual polish; compare actual output file contents with fixtures.

### F6 — Presentation
Probe PPT/PPTX import; create slide; layouts/themes; text/images; slide ordering; slideshow; speaker/presentation controls; export PDF; embedded media and unsupported fonts.
Acceptance: preserve original and disclose unsupported media/layouts; don't claim PowerPoint fidelity without fixture-based testing.

### F7 — Scanner / OCR
Probe permission prompt, camera capture, retake, crop/perspective, page reorder/delete, multi-page PDF, image import, OCR languages, searchable output, offline mode and sensitive-document privacy.
Acceptance: permission denial is recoverable; OCR accuracy is measured by language; never claim on-device processing unless verified.

### F8 — Learning tools for children
Independent design goal, not a WPS clone: create/edit notes, worksheets, flashcards, practice quizzes, reading PDFs and offline study packs. Keep a low-storage mode, Hindi-friendly text and no required login for core local work.
Acceptance: usable airplane-mode after installation; all core learning content stored locally; clear export/backup controls.

### F9 — Optional integrations
Cloud, account sync, AI, templates catalogue, collaboration and premium features are deferred until core offline editing is stable. No paid access bypass, private API imitation or silent upload of user documents.

## Standard evidence record (copy for each case)

- Case ID:
- Feature/version:
- Evidence label and source:
- Device / Android / WPS build:
- Account / subscription / network state:
- Test file (format, size, checksum if relevant):
- Entry path:
- Preconditions:
- Action steps:
- Expected result:
- Observed result:
- Cancel/back/permission-denied/offline/error branch:
- File/system side effect:
- Screenshot/video timestamp:
- Confidence / unknowns:
- MyOffice implementation status:
- Acceptance-test result:

## Next concrete step

Use the authorized WPS installation to capture the Home → local file open → back → create/new-file path with screenshots or a short screen recording. Until that evidence exists, mark exact screen details as `BLOCKED` or `INFERRED`. Public pages/videos are a starting map, not proof of the installed app's exact behavior.
