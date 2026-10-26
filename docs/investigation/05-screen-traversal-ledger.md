# MyOffice Investigation 05 — Screen Traversal Plan and Evidence Ledger

**Target:** WPS Office for Android, user-reported version 26.10.0.  
**Purpose:** Turn the broad feature inventory into a repeatable screen-by-screen audit.  
**Current reality:** The available research environment can inspect public web pages and edit the MyOffice GitHub repository, but it cannot remotely tap the user's installed WPS app. No emulator/device session or REA runtime is currently connected. Therefore, rows marked below are planned or documented, not directly exercised.

## Why “every screen” needs a defined boundary

WPS is a large multi-platform suite. Screens and options can change based on Android version, device size, region, login state, subscription, permissions, cloud provider, network, file format, and the file currently open. Some menus only appear after selecting an object or opening a particular kind of file. A defensible claim of complete traversal requires a defined build/device/account state and captured evidence for each screen and branch.

This project will use the following evidence labels:
- **DOC** — listed or described in public documentation.
- **VIDEO** — visible in a specific accessible video, with video URL and timestamp.
- **DEVICE** — personally exercised in WPS Android 26.10.0, with screenshot/recording and test result.
- **INFERRED** — plausible workflow, not verified.
- **BLOCKED** — needs login, subscription, network, permissions, special file, or device access.
- **N/A** — not applicable to this target/build, with reason.

## Public documentation coverage discovered

The official WPS Docs Center organizes help topics under WPS Docs, WPS Sheets, WPS Slides, WPS PDF, and WPS Cloud Documents. It lists feature families well beyond a basic office editor, including document formatting/layout/references/review/security; spreadsheet formulas/data analysis/charts/pivots/printing; slide design/animations/presenter mode; and PDF editing/pages/annotations/security. These help topics are a discovery index, not proof that every item is present on Android 26.10.0.

Reference: https://www.wps.ai/en-us/docs/

The official WPS Help Center has separate Basics, Writer, Spreadsheet, and Presentation areas, plus account, recovery, and subscription help:
https://help.wps.com/

The official Android product page advertises document editing, PDF conversion, cloud sync with third-party platforms, and many languages:
https://www.wps.com/office/android/

## Screen-by-screen traversal ledger

### A. Launch, onboarding, home and account
- [ ] Cold launch, splash/loading, first-run state
- [ ] Permissions prompts and denied-permission branches
- [ ] Login, registration, third-party login, account recovery, logout
- [ ] Home navigation, create menu, search, recent files, templates, tools
- [ ] Local files, cloud files, external storage providers
- [ ] Settings, language/theme, cache/storage, notifications, privacy, help/about
- [ ] Subscription page, restore purchase, locked-feature explanation
- [ ] Offline launch, no-account state, network failure, sign-in failure

### B. Shared file workflows
- [ ] Create blank DOC, XLS, PPT, PDF where available
- [ ] Open via recent list, in-app browser, Android file picker, external “Open with”
- [ ] Save, Save As, rename, duplicate/copy, move, delete, multi-select
- [ ] Remove from recents versus actual delete
- [ ] Share file versus share cloud link; export; print
- [ ] Unsupported/corrupt/locked file; duplicate filename; read-only file
- [ ] Autosave, unsaved-changes prompt, crash/relaunch recovery, backup/version restore
- [ ] Large file, low storage, storage permission denied, external provider unavailable

### C. Writer
- [ ] Editing mode, keyboard, cursor, selection handles, floating toolbar
- [ ] Undo/redo, select all, clipboard
- [ ] Font, size, bold, italic, underline, strike, color, highlight, clear formatting
- [ ] Paragraph alignment, indentation, spacing, lists, borders, shading
- [ ] Page size, orientation, margins, columns, page breaks, background, watermark
- [ ] Insert table, image, shape, hyperlink, symbol, date, header/footer, page number
- [ ] Find/replace, word count, spelling, translation, read aloud
- [ ] Outline/navigation, TOC, captions, footnotes/endnotes, index, cross-reference
- [ ] Comments, track changes, accept/reject, compare documents, restrict editing
- [ ] Password/encryption, export formats, print/preview, round-trip DOCX fidelity

### D. Spreadsheet
- [ ] Workbook/sheet creation, sheet tabs, rename/copy/move/delete/reorder
- [ ] Cell/range selection, formula bar, edit/cancel, fill handle
- [ ] Row/column insertion, deletion, resize, hide/unhide, freeze panes
- [ ] Font, alignment, borders, fill, wrap, merge/split, cell styles
- [ ] Number/date/currency formats, convert text to number, duplicate detection
- [ ] Formula entry, references, common functions, error states, recalculation
- [ ] Sort, filter, find/replace, data validation, conditional formatting
- [ ] Charts and chart editing; pivot tables if available
- [ ] Import CSV (delimiter/encoding), XLS/XLSX export, PDF/image export
- [ ] Print area, margins, orientation, scaling, fit-to-mobile, reading mode
- [ ] Sheet protection, comments, collaboration history, shared editing

### E. Presentation
- [ ] Deck creation/open/save, slide sorter, add/delete/duplicate/reorder
- [ ] Layout, theme, slide size/background/master
- [ ] Text box, images, shapes, tables, charts, object alignment/layers/grouping
- [ ] Audio/video insertion and playback compatibility
- [ ] Fonts, paragraph formatting, drawing/ink tools
- [ ] Transitions, animations, timing, slide show options
- [ ] Presenter view, speaker notes, rehearsal timer, pointer/focus tools
- [ ] Comments, spelling, review, encryption
- [ ] Export PPTX/PDF/images/video, print, round-trip fidelity

### F. PDF
- [ ] Open, page thumbnails, zoom, search, bookmarks, navigation
- [ ] Highlight, underline, strikeout, free draw, notes, shapes, attachments
- [ ] Insert/delete/reorder/rotate/extract/replace pages; split/merge
- [ ] Crop page/content, background, header/footer, page number, watermark
- [ ] Fill forms, signature/stamp, save/flatten behaviour
- [ ] Edit text/image, OCR/scanning, scanned-PDF conversion
- [ ] Convert PDF to DOCX/XLSX/PPTX/image and Office to PDF
- [ ] Compress/optimize, export/share/print
- [ ] Password/encryption, permissions, redaction, metadata
- [ ] AI PDF: summarize, ask questions, translate, extract information
- [ ] Premium/network gates, error handling, output fidelity

### G. Scan, templates, images and additional utilities
- [ ] Camera scan, multi-page capture, crop/deskew/rotate, OCR
- [ ] Template search, preview, download, use, save copy
- [ ] Image/photo edit tools shown in target app
- [ ] Archive/compress/extract or other utilities if actually present
- [ ] Screen capture, annotate screenshot, convert screenshot to document
- [ ] Cross-app intents, QR/file transfer, clipboard integration

### H. Cloud, collaboration and AI
- [ ] Connect/disconnect external cloud providers and permission flows
- [ ] Upload/download/sync status, offline edits, conflict resolution
- [ ] Link sharing, access roles, expiry/revocation where supported
- [ ] Co-edit, cursors, comments, collaboration history, version history
- [ ] AI entry points in Writer, Sheets, Slides, PDF, image tools
- [ ] Input selection, consent/privacy disclosure, generation, insertion, undo
- [ ] Limits, premium gates, quota, cancellation, offline/network failure

### I. Accessibility, localization and resilience
- [ ] Hindi UI/content and mixed Hindi-English document handling
- [ ] TalkBack, font scaling, contrast, focus order, touch target sizes
- [ ] Portrait/landscape, split-screen if supported, external keyboard
- [ ] Background/resume, process kill, low memory, battery saver
- [ ] Very large, malformed, password-protected and mixed-format documents
- [ ] File integrity before/after each edit, export and app upgrade

## Evidence row format

Use one record per screen or branch:

| Field | Required value |
|---|---|
| ID | Stable ID, e.g. WRI-FMT-001 |
| Screen/entry path | Exact path from launch |
| Build/device | App version, Android version, phone/tablet dimensions |
| Preconditions | Login, membership, network, permissions, file type |
| Controls/options | Every visible action, toggle, field and menu item |
| Action/result | Tap sequence and resulting screen/state |
| File/state effect | Saved output, changed setting, remote operation, or none |
| Negative branch | Back/cancel, invalid input, offline, denied permission |
| Evidence | Screenshot/video URL and timestamp, or official doc URL |
| Evidence label | DOC, VIDEO, DEVICE, INFERRED, BLOCKED, N/A |
| Confidence/priority | High/medium/low and P0/P1/P2/P3 |
| MyOffice acceptance test | Independent behaviour to implement and verify |

## Completion definition

A module is **screen-traversed** only after every visible top-level control has been checked and relevant conditional branches have been tested for the defined target state. A module is **feature-verified** only after its output/state change is tested. Public documentation alone cannot mark a screen or feature as device-verified.

## Current progress and blocker

- Public feature inventory: documented in Investigation 04.
- Public baseline and target version: documented in investigations 01–03.
- Direct WPS device interaction: not yet performed.
- REA/APK analysis: not yet performed.
- Missing capability: a live authorized Android device/emulator session or user-supplied captures/video for target-build evidence, and a supported REA environment for APK-level work.

## Next execution sequence

1. Extract and index official help topics by module.
2. Review individual official tutorial videos, logging only what is actually visible.
3. Capture target-build evidence from the installed app when a device session is available.
4. Update the ledger and convert verified behaviour into MyOffice acceptance tests.
5. Implement and test original MyOffice functionality; do not copy proprietary assets, source code, or branding.
