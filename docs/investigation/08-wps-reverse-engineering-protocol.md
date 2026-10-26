# WPS Android Reverse-Engineering Protocol for MyOffice

## Purpose
Build MyOffice from an evidence-backed model of WPS Office Android behavior, not from guesses. This is a behavioral and compatibility study, not a claim to have reproduced WPS internals.

## Legal and technical boundary
- Use public official videos, public help pages, visible app behavior, and user-authorized testing.
- Do not copy WPS source code, protected assets, branding, private APIs, credentials, or proprietary document-engine internals.
- Before APK-level decompilation or instrumentation, confirm authorization and the applicable licence/terms. Do not bypass paid entitlements, access controls, encryption, or account restrictions.
- Every observation must be labelled by evidence type; never report a feature as device-verified if it was inferred from a web page or video.

## Target baseline
- Product: WPS Office for Android
- Target version requested for the study: 26.10.0
- Package identifier reported by public listings: cn.wps.moffice_eng
- Target device for eventual hands-on validation: Samsung Galaxy S21 FE / Android
- Version caveat: public listings and official sites can expose different variants, release channels, Android minimum versions and feature rollouts. Record the installed app's About/version screen before making version-specific claims.

## Evidence labels
- DOC: official documentation / product page.
- VIDEO: official video; record title, URL, publication date and timestamps actually reviewed.
- DEVICE: directly observed on the authorized installed app; record OS, app build, account state, network state and test file.
- APK-META: metadata from a lawfully obtained package (manifest, package/version/signature); does not establish screen behavior.
- INFERRED: hypothesis based on other evidence; must not be presented as verified.
- BLOCKED: unable to inspect, account/paywall/region/device prevents validation.
- N/A: tested and shown not applicable for this target/version.

## Required per-workflow record
1. Stable case ID and feature area.
2. Exact WPS app version/build and device/OS (or VIDEO/DOC evidence source).
3. Entry path: home -> menu -> submenu -> dialog.
4. Preconditions: signed-in/out, file type/size, storage permission, network, subscription state.
5. Visible controls and default selections.
6. Action sequence and expected/observed result.
7. Conditional branches: cancel, back, denied permission, offline, file unsupported, no storage, error, paywall.
8. Persistent effects: file bytes, metadata, recent history, setting, account/cloud change.
9. Screenshot/video timestamp and evidence label.
10. MyOffice acceptance test and current implementation status.

## Coverage matrix
- Launch, onboarding, permission prompts, home tabs and navigation.
- Local files: open/import/create, recents, search, sort, rename, move, copy, delete, favourites, folders, unsupported files.
- Writer: create/open/save, text selection, clipboard, undo/redo, fonts, paragraphs, lists, tables, images, page layout, find/replace, comments/review, export/print.
- Spreadsheet: workbook/sheet navigation, cells/ranges, keyboard/formula input, formula evaluation, formatting, fill, sort/filter, charts, freeze panes, print/export.
- Presentation: deck/slide creation, layout/themes, text/images/audio/video, transitions/animations, slide show, presenter/navigation, export.
- PDF: viewer/navigation, search, annotations, page reorder/rotate/delete, scan/OCR, signature/stamp, conversion, compression, password/security, print/share.
- Scanner/camera: capture, crop, enhance, multi-page order, output format, signatures/stamps, storage permission, offline and failure paths.
- Templates, learning/study tools, flashcards/quizzes, language/accessibility.
- Cloud/account: sign-in, sync, shared files, comments, collaboration, history, conflict resolution, sign-out.
- AI: entry points, eligibility, network requirements, privacy notice, quota/paywall, failures.
- Settings: theme, language, default open behavior, autosave/recovery, storage/cache, notifications, privacy.
- Reliability/security: large files, corrupted files, no network, low storage, low memory, interrupted save, process death, permission revocation.

## Current evidence-backed findings (public-source level only)
- Official WPS Android marketing describes viewing/editing documents and a PDF reader/editor/converter, cloud-platform integrations and language support including Hindi. Source: https://www.wps.com/office/android/
- The official WPS site describes DOCX/XLSX/PPTX/PDF/OFD support, cloud documents, collaboration, and AI features; availability may differ by version/region/account. Source: https://www.wps.cn/android
- An official WPS Office introductory video describes the suite as Writer, Spreadsheet, Presentation and PDF. Source: https://www.youtube.com/watch?v=iuV-I4iPgoM
- A public third-party listing for version 26.10.0 reports package cn.wps.moffice_eng and scanner updates (All Features entry; signature/stamp insertion). This is secondary evidence only and does not verify actual UI behavior. Source: https://www.apkmirror.com/apk/wps-software-pte-ltd/wps-office-pdf/wps-office-pdf-word-sheet-26-10-0-release/wps-office-pdf-word-sheet-26-10-0-android-apk-download/

## Current limitations
- This document does not claim that WPS 26.10.0 has been installed or manually traversed in the current environment.
- No WPS screen has yet been marked DEVICE-verified here.
- Official video index is incomplete; the first-pass inventory is a research queue, not exhaustive coverage and not equivalent to watching every video.
- MyOffice's first Local File Hub prototype is not yet validated against WPS behavior. Keep it as a prototype until the relevant workflows are observed and tested.

## Gate before implementing each major feature
Do not start implementation of a WPS-derived workflow until:
1. its entry path and visible controls have evidence,
2. at least one normal workflow and relevant error/cancel branches are documented,
3. mobile-vs-desktop distinction is clear,
4. a MyOffice acceptance test exists,
5. implementation uses original code and appropriately licensed dependencies.

## Research sequence
1. Home/navigation/file manager.
2. Writer basic document lifecycle and formatting.
3. PDF reader and scanner.
4. Spreadsheet basic editing.
5. Presentation viewing/slideshow.
6. Import/export and conversion.
7. Offline education workflows and accessibility.
8. Cloud, collaboration, templates and AI (only after documenting online/account dependencies).
