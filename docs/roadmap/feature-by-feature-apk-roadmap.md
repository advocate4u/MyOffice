# MyOffice: Feature-by-feature APK roadmap

## Product goal
Build an original Android office and offline-learning app. Use public documentation and videos to understand observable workflows, but do not copy WPS source code, protected assets, branding, or proprietary implementation.

## Delivery rule
For each feature: define acceptance criteria -> implement -> build APK -> inspect build logs -> run automated checks -> test on an Android device when available -> record limitations -> only then start the next feature. Do not call a feature complete merely because it compiles.

## Release train

### F1 — Local file hub (current)
- Open Android system document picker with ACTION_OPEN_DOCUMENT.
- Avoid broad storage permissions; only access files explicitly selected by the user.
- Show recent files with name, MIME type and size.
- Persist URI grants where the provider supports them and retain recent list locally.
- Preview text, CSV, Markdown, JSON, XML and log files, limited to 1 MiB.
- For unsupported formats, show metadata and offer Android's open-with chooser.
- No login, network, analytics or cloud dependency.
- Acceptance: launch offline; select a text file; preview it; return home; file appears in recents; relaunch and verify recents; revoke/delete source and verify graceful handling; select PDF/DOCX and confirm no false claim of internal rendering.

### F2 — Create and save plain-text notes
- Create note and edit/save to user-chosen location via ACTION_CREATE_DOCUMENT.
- UTF-8, safe overwrite behavior and unsaved-change prompt.
- Tests: empty note, Hindi, emoji, long text, cancel save, repeated save.

### F3 — Local library quality
- Search and sort recents; remove item from history without deleting source file.
- Recover from revoked/missing source permissions.
- Accessibility labels, responsive layout and better metadata handling.

### F4 — PDF reading
- Render pages locally using a legally usable library or platform capability.
- Page navigation, zoom, rotation, progress and error states.
- Test large files, malformed PDFs, password-protected PDFs, low memory and airplane mode.
- Verify dependency license before adoption.

### F5 — PDF annotations and study tools
- Highlights, bookmarks, user notes and search where technically supported.
- Keep annotations local by default; export/backup is opt-in.
- Never claim redaction unless content is securely removed, not merely covered visually.

### F6 — Offline education packs
- Import/export lesson packs; subject, grade and language metadata; notes and quizzes.
- Download only when explicitly requested; packs usable offline afterwards.
- Child privacy, accessible Hindi/English UI and storage controls.

### F7 — DOCX reading and editing
- First achieve robust read-only compatibility, then edit/save.
- Validate tables, lists, images, page breaks, fonts, Hindi shaping, headers and footers.
- Use standards-based/open-source libraries only after license and format-fidelity review.

### F8 — Spreadsheet
- CSV first; then XLSX reading/editing with formulas and formatting clearly scoped.
- Formula compatibility matrix and recalculation tests.

### F9 — Presentation
- PPTX reading/rendering first; then slide editing only when reliable.
- Test media, fonts, transitions, presenter mode and offline behavior.

### F10 — Release hardening
- Automated unit/instrumentation tests, accessibility, crash handling, backup/restore, app size and low-RAM checks.
- Generate reproducible debug APK and a separately signed release APK only after signing configuration is securely arranged.

## Architecture choices
- Native Android Java, Android SDK, minSdk 26, target/compile SDK 35 for the prototype.
- Android Storage Access Framework for user-selected files; no broad storage permission.
- Local-first; no account/network dependency for core reading.
- Separate UI, repository, document-format adapter and education-pack layers as features grow.
- Keep parsing bounded; do not load arbitrary large documents fully into memory.

## Current implementation status
F1 source and a GitHub Actions debug-APK workflow have been added. F1 is not complete until the workflow succeeds and device tests pass. Build artifact availability depends on a successful workflow run.
