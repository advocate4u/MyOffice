# MyOffice Investigation 03 — Android Workflow Baseline

**Status:** Public-documentation analysis completed; direct video-frame analysis and on-device observation remain pending.  
**Reference product:** WPS Office for Android; user-specified target version 26.10.0.  
**Purpose:** Derive original MyOffice requirements from publicly observable workflows. This is not a claim of APK decompilation or internal-code analysis.

## Sources reviewed

- WPS Office Android product page: https://www.wps.com/office/android/
- WPS Office Android PDF editor overview: https://www.wps.com/feature/pdf-editor-for-android/
- WPS documentation — New Document on Android: https://www.wps.ai/en-us/docs/wps-docs/document-collaboration/new-document/android/how-to/
- WPS documentation — Copy, Paste, and Cut on Android: https://www.wps.ai/en-us/docs/wps-docs/document-collaboration/copy-paste-and-cut/android/introduction/
- User-provided official YouTube channel: https://youtube.com/@wpsofficeofficial
- Supplementary mobile walkthrough (third party, not official): https://www.youtube.com/watch?v=SyaJ01dbJc8

Public documentation can describe feature flows, but does not prove that each control or entitlement is identical in version 26.10.0. Membership gates and UI may change by region, build, account, and app version.

## Findings and evidence level

### A. Creating a document

Public WPS documentation describes this Android flow:

1. Open the WPS home page.
2. Tap the `+` action.
3. Choose Docs.
4. Choose Blank.
5. An editable document opens; the user can type and then save or share it.

Other documented paths include selecting a template, continuing from an imported file, and converting a scanned image into editable text. The documentation notes that some template/import/scan flows may require membership.

**Evidence:** Public product documentation, not direct observation of the user's installed build.

### B. Basic text editing

WPS's public Android documentation describes selecting text in edit mode, then using a floating selection toolbar to copy or cut it, and pasting at the cursor location.

**MyOffice requirement:** support native text selection, copy, cut, paste, undo/redo where available, and predictable cursor placement. Do not imitate proprietary visual assets; use original UI and platform conventions.

### C. PDF workflows

WPS publicly describes PDF viewing and workflows such as annotation, signing/filling, page operations, and conversion between PDF and Office formats. Availability may differ by feature, subscription, file type, or version.

**MyOffice requirement:** treat PDF reading and annotation as a separate milestone after a reliable document core. Build only features that can be implemented and tested safely; make conversion limits explicit rather than promising perfect layout preservation.

### D. Files and cloud

WPS publicly promotes local document access, sharing, and cloud synchronization/integration. These are product claims, not proof of internal architecture.

**MyOffice requirement:** first implement local file open/save, recent files, rename, export/share, and recovery from interrupted edits. Add cloud storage only after local data integrity and account/security design are settled.

## Proposed original MyOffice acceptance criteria

### Milestone 1 — local text document
- Create a blank document from a clear, accessible action.
- Type, select, copy, cut, and paste text.
- Save to a user-visible location with a chosen filename.
- Reopen the saved file and verify the content is retained.
- Handle back navigation and unsaved changes without silently losing work.
- Provide clear loading, empty, error, and permission states.

### Milestone 2 — document navigation and sharing
- List recent/local documents and open a selected file.
- Rename and share/export through Android's standard sharing mechanisms.
- Handle unsupported formats and permission errors clearly.
- Avoid collecting file content or telemetry unnecessarily.

### Milestone 3 — PDF foundation
- Open and render a local PDF.
- Add basic annotations only if supported by the chosen implementation library.
- Save a copy without overwriting the original unexpectedly.
- Clearly label unsupported editing/conversion operations.

### Milestone 4 — spreadsheets and presentations
- Scope these as separate modules; do not claim full Excel/PowerPoint compatibility until tested against real files.

## Manual validation protocol for WPS 26.10.0

On the user's own installed app, use a harmless test file and record screenshots or screen recording of:
1. Home screen and visible navigation.
2. New blank document flow.
3. Editor toolbar before and after entering edit mode.
4. Save/rename dialog and available storage destinations.
5. Close and reopen the same document.
6. Share/export menu and available formats.
7. Any membership prompt or permission dialog relevant to the workflow.

Hide personal documents, account identifiers, notifications, and other private information before sharing captures.

For each step, record: **precondition → action → visible result → file/state change → error or prompt → confidence**.

## What is not established yet

- No WPS APK has been acquired, inspected, decompiled, instrumented, or executed through REA.
- No claim is made about WPS internal classes, APIs, database schema, network endpoints, encryption, or source code.
- The YouTube channel link is a channel-level reference; individual videos should be selected and analyzed one at a time. If a video cannot be inspected directly, use its transcript or screenshots.
- Public documentation may be generic or updated after the target build; the user's installed version must be manually checked.

## Next action

Start with a user-provided video URL from the official channel, or screenshots of the WPS 26.10.0 home screen and blank-document editor. Use those observations to update this baseline before making detailed UI or implementation decisions.
