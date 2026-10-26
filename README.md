# MyOffice

An original Android office and learning app focused on free, offline-first use, including Hindi/English writing and low-resource devices.

## Development rules

- Implement one feature at a time.
- Build a debug APK after each feature, verify the build and publish the artifact.
- Then proceed to the next feature and repeat until the planned application is complete.
- Base WPS-inspired compatibility work on public documentation and authorized observations of user-visible behavior; never copy proprietary source, protected assets, branding or private internals.
- Label evidence honestly: public documentation is not the same as direct device verification.
- Prioritize local file safety, offline behavior, Hindi text, accessibility and low storage use.

## Current implementation

- Android app module: `app/`, Java 17, minSdk 26.
- MyAdv release signing: CI always builds and uploads a clearly labeled debug test APK. A production update APK is built only when the persistent keystore secrets `MYADV_KEYSTORE_BASE64`, `MYADV_STORE_PASSWORD`, `MYADV_KEY_ALIAS`, and `MYADV_KEY_PASSWORD` are configured. If those secrets are missing, the workflow still completes the debug build and explains why no production update APK was published. Never use the debug APK to update the production app.
- APK verification: CI verifies the APK signature, confirms the package ID remains `org.myoffice.app`, and emits a SHA-256 checksum. Android's package installer independently validates signatures at install/update time.
- Back navigation: the Android Back button returns through previous screens; from Home, press Back twice within two seconds to close the app. Spreadsheet/presentation editors retain their unsaved-change prompts.
- Local file hub: system document picker, recent URI list, bounded text previews, file metadata and open-with chooser.
- Offline notes (feature F2): create, edit, save and reopen notes in app-private storage; Hindi/English text; unsaved-change confirmation.
- Basic PDF reader (feature F3): render local PDF pages with Android PdfRenderer and navigate previous/next pages; rendering stays on-device and unsupported/encrypted files show an error.
- PDF rotated-copy export (feature F62): export a new PDF with the selected 0°/90°/180°/270° viewer rotation baked into every page; rendering runs off the UI thread and leaves the source untouched. This rasterized output may lose searchable/selectable text and is limited to 500 pages.
- PDF watermark/stamp export (feature F63): add user-supplied text such as DRAFT, COPY or APPROVED to every page of a new flattened PDF copy, while preserving the chosen viewer rotation and leaving the original untouched.
- PDF page-range extraction (feature F69): create a new flattened PDF from an inclusive user-selected page range, optionally applying the current viewer rotation, while preserving the original source.
- PDF merge (feature F70): combine the currently opened PDF with additional selected local PDFs into a new flattened PDF, rendered offline on a background thread; originals remain unchanged and merged output is limited to 500 pages.
- PDF all-pages image export (feature F78): export up to 100 PDF pages as individual PNG images inside one ZIP archive, applying the current viewer rotation; render locally on a background thread and leave the source PDF unchanged.
- PDF current-page image export (feature F77): render the currently viewed page to a PNG through Android's built-in PdfRenderer, honor the saved viewer rotation, and save through Android's document picker. Rendering is local, runs on a background thread, and does not modify the source PDF.
- PDF page text annotation (feature F71): add user-entered text to the currently viewed page and export a new flattened PDF copy; the annotation is baked into that page only, with the source left unchanged.
- Writer first milestone (feature F4): create, edit, save and reopen local UTF-8 plain-text documents; share text through Android's chooser; unsaved-change confirmation.
- Writer rich formatting (feature F57): apply bold, italic, underline, strikethrough, relative font size, red/blue text, yellow highlight, and left/center/right editor alignment. Formatted copies persist as HTML and can be reopened offline; existing TXT files remain supported.
- Writer rich-format save safety (feature F60): detect formatting spans when saving or leaving Writer, persist formatted content as HTML instead of silently flattening it to plain text, and compare the editor's actual initial text when checking for unsaved changes.
- Writer bullet and numbered lists (feature F61): prefix selected lines with bullets or sequential numbers, toggle an existing list off, and persist list markers as editable text in TXT/HTML documents.
- Spreadsheet first milestone (feature F5): editable local CSV grid with add-row/add-column controls, quoted-field parsing/escaping, and local save/reopen.
- Presentation first milestone (feature F6): create/edit/save/reopen local text-only slide decks, navigate slides, add slides, and share current slide text.
- Image-to-PDF first milestone (feature F7): select up to 20 local images, create a multi-page PDF on-device, and choose a destination through Android's Save document picker.
- Text export (feature F8): export local notes and Writer content to a user-selected UTF-8 `.txt` file using Android's Save document picker.
- CSV import (feature F9): open an existing local CSV into the editable grid, with a 1 MiB import limit and save-as-new behavior that leaves the original file unchanged.
- CSV export (feature F10): export the current editable grid to a user-selected UTF-8 `.csv` file, with standard comma/quote escaping.
- PDF sharing (feature F11): share a PDF opened in MyOffice through Android's share chooser, with read-only URI access.
- Local deletion (feature F12): delete app-private notes, Writer documents, CSV sheets and slide decks after a confirmation dialog.
- Recent-file search (feature F13): filter the recent file list by filename, case-insensitively, without uploading file data.
- Remove from recents (feature F21): remove an entry from local recent history without deleting the original document.
- Recent sorting (feature F22): cycle through newest-first, filename A–Z and largest-first ordering; search and remove-from-recents continue to work with the selected order.
- Recent-file favorites (feature F53): mark recent documents as favorites, persist favorites locally across app restarts, filter the Home list to favorites, and remove a favorite without modifying the source file.
- Dedicated favorites screen (feature F54): browse and search saved favorite URIs separately from recent history, reopen a favorite, or remove its star without editing the document.
- Batch sharing from recents (feature F58): select multiple recent files and hand them to Android's share chooser using URI grants; mixed file types use a generic MIME type.
- Favorites-first recent sorting (feature F55): sort the Home recent-file list with favorites first, preserving newest-first order within favorites and non-favorites.
- Share from recent files (feature F56): share a recent document directly through Android's chooser without first opening it; source file is not modified.
- Home shortcuts (feature F23): group note, Writer, spreadsheet, presentation and image-to-PDF entry points into compact two-column tiles while keeping Open a file prominent.
- Spreadsheet back protection (feature F24): Android's system Back key uses the same save/discard/keep-editing prompt as the in-screen Back action when a CSV sheet has unsaved changes.
- Recent type filters (feature F25): filter recent items to All, PDF, Text, CSV or common Office formats while retaining filename search and sorting.
- PDF zoom (feature F26): increase/decrease local PDF render scale or reset to 100%, with horizontal scrolling for zoomed pages and bounded scale settings.
- PDF page jump (feature F27): enter a one-based page number and jump directly to that page while retaining the current zoom level.
- PDF reading position (feature F28): remember the last page viewed for each selected PDF URI locally and reopen that PDF at the saved page.
- PDF bookmarks (feature F29): save/remove page bookmarks per PDF and open a saved page from a local bookmark list.
- PDF page notes (feature F30): create, edit and delete offline study notes attached to a specific page of a selected PDF.
- Writer Find & Replace (feature F31): replace all exact, case-sensitive text matches in the current plain-text document and keep the result in the editor until saved.
- Writer statistics (feature F32): display a live word count and character count as the user types or replaces text.
- Writer Undo/Redo (feature F33): keep a bounded 50-step text snapshot history during the current editing session, with undo/redo controls and live statistics updates.
- Writer library search (feature F34): filter local Writer documents by title, preview text or filename, case-insensitively.
- Notes library search (feature F35): filter saved local notes by title, content preview or filename, case-insensitively.
- Share note (feature F36): share the current note title and text through Android's system chooser without forcing a save first.
- Duplicate note (feature F37): create a separate copy of a saved note with a “(copy)” title suffix while preserving the original.
- Notes sorting (feature F38): switch between modified-date newest-first and title A–Z ordering while retaining the active search.
- Writer sorting (feature F39): switch between modified-date newest-first and first-line title A–Z ordering while retaining the active search.
- Spreadsheet library search/sort (feature F40): filter saved CSV sheets by filename or first-row preview and sort by modified date or filename.
- Presentation library search/sort (feature F41): filter saved slide decks by title, first-slide text or filename and sort by modified date or title.
- Presentation Back protection (feature F42): Android system Back from the slide editor uses the save/discard/keep-editing prompt; system Back from slideshow returns to the editor.
- Duplicate presentation (feature F43): create a separate copy of a local text slide deck and append “(copy)” to its first-slide title.
- Duplicate CSV sheet (feature F44): make a byte-for-byte local copy of a saved CSV sheet so edits can be made independently.
- Duplicate Writer document (feature F45): create a separate byte-for-byte copy of a saved plain-text Writer file.
- PDF Save a Copy (feature F46): copy an opened PDF stream to a user-selected destination through Android's Save document picker.
- Share entire presentation (feature F47): share all text slides in order through Android's system chooser, including slide numbers and titles.
- PDF page rotation (feature F48): rotate the currently viewed PDF page in 90° steps for easier reading of sideways scans; rotation is a viewer-only transform and does not modify the source PDF.
- Per-document PDF rotation memory (feature F49): remember each PDF's viewer rotation locally by document URI, so reopening it restores its chosen orientation without changing the file.
- PDF rotation reset (feature F50): reset the current PDF's viewing orientation to 0° in one tap and persist that choice for that document.
- MyAdv release signing support (feature F51): optional release builds use the same MYADV_* keystore secrets as MyAdv, then CI verifies the APK signature, package ID and alignment. Debug APK remains available without signing secrets.
- Back navigation (feature F52): preserve the previous screen, return to it on Android Back, and require a second Back press within two seconds to close from Home.
- Text import (feature F14): open a supported text file in preview and optionally edit a local copy in Writer, preserving the original source.
- Find-in-text (feature F15): search a text preview case-insensitively and highlight matching phrases locally.
- Formula preview (feature F16): calculate read-only SUM, AVERAGE, MIN, MAX and COUNT over simple cell ranges in the CSV grid, without changing stored formula text.
- Slideshow (feature F17): present text-only slide decks in a read-only slide view with previous/next navigation.
- DOCX text import (feature F18): extract paragraph text from a local DOCX package into a new Writer copy; original is unchanged.
- DOCX basic-format import (feature F65): import supported bold/italic/underline/strikethrough, text color, highlight and font-size runs into Writer spans. Tables, images, advanced styles and full round-trip fidelity are not preserved.
- Basic XLSX worksheet import (feature F66): import shared-string, inline-string, numeric, boolean and cached formula-result values from the first worksheet into a new local editable CSV sheet. The source XLSX remains unchanged; cell formatting and multiple worksheets are not yet supported.
- Basic XLSX workbook export (feature F68): export the current editable grid as a new single-sheet OOXML workbook with numeric values and inline text, leaving the internal CSV sheet unchanged. Cell styling, formulas and multiple-sheet export are not yet supported.
- DOCX paragraph alignment (feature F67): import left/center/right/justified paragraph alignment spans and write supported paragraph alignment back during DOCX export.
- DOCX list import (feature F72): read common bullet and decimal numbering definitions and prefix imported paragraphs with editable bullet/number text. Advanced list restarts, multilevel formats and true Word list semantics are not preserved.
- DOCX table text import (feature F73): preserve table cell text as tab-separated rows when importing DOCX into Writer, so simple table content remains readable/editable. Tables are flattened to text, not editable grid objects.
- RTF import/export (feature F74): import basic text, paragraph breaks, tabs, common symbols and Unicode escapes from RTF into Writer; export Writer text as a valid basic RTF document.
- RTF character formatting import (feature F75): preserve common bold, italic, underline, strikethrough and font-size controls from RTF as Writer spans. Color tables, paragraph styles, images and tables remain unsupported.
- RTF formatting export (feature F76): write supported Writer bold/italic/underline/strikethrough and relative font-size spans into RTF character groups; color tables, paragraph formatting and full RTF fidelity are not yet supported.
- DOCX export (feature F19): export Writer plain text as a minimal WordprocessingML DOCX package through Android's Save document picker.
- DOCX formatted export (feature F64): export Writer rich-text runs with bold, italic, underline, strikethrough, text color, highlight and relative font size, plus the current left/center/right paragraph alignment. This is a focused compatibility subset, not full Word fidelity.
- Arithmetic formula preview (feature F20): preview `=A1+B1`, subtraction, multiplication and division using numeric cell references, alongside the aggregate formulas from F16.
- No login or network permission is required for these core features.
- Advanced Word compatibility (preserving DOCX formatting, tables and images), XLSX/PPTX import/export, slide themes/images/animations, camera scanning/OCR, PDF text search/native annotations and other advanced office functionality are still in development.

## Build and APK workflow

GitHub Actions always builds a clearly labeled debug test APK. It builds a production update APK only when all four `MYADV_*` signing secrets are configured; if they are missing, the workflow fails the release-signing stage and does not publish a production update APK. Workflow:
https://github.com/advocate4u/MyOffice/actions/workflows/android-debug-apk.yml

Check the latest run before claiming that a feature build succeeded. A successful CI build is not a substitute for installation and device-level acceptance testing.

## WPS reverse engineering

- [REA-led static preflight of uploaded WPS 18.20.2 APK](docs/reverse-engineering/REA-static-preflight-WPS-18.20.2.md)
- [PowerShell helper to run official REA probes](scripts/re/inspect-wps.ps1)
- Official REA project: https://github.com/morluto/rea

The uploaded APK has not yet been run through REA/JADX in this environment because the pinned engine could not be downloaded here. The preflight report records static package data and a signing-identity warning; it does not claim the APK is authentic or working.

## Investigation and testing

- [WPS reverse-engineering protocol](docs/investigation/08-wps-reverse-engineering-protocol.md)
- [WPS-to-MyOffice feature evidence matrix](docs/investigation/09-wps-to-myoffice-feature-evidence-matrix.md)
- [F1 local file hub test plan](docs/testing/F1-local-file-hub-test-plan.md)
- [F2 offline notes test plan](docs/testing/F2-offline-notes-test-plan.md)
- [F3 PDF reader test plan](docs/testing/F3-pdf-reader-test-plan.md)
- [F4 Writer test plan](docs/testing/F4-writer-test-plan.md)
- [F5 Spreadsheet test plan](docs/testing/F5-spreadsheet-test-plan.md)
- [F6 Presentation test plan](docs/testing/F6-presentation-test-plan.md)
- [F7 Image-to-PDF test plan](docs/testing/F7-image-to-pdf-test-plan.md)
- [F8 Text export test plan](docs/testing/F8-text-export-test-plan.md)
- [F9 CSV import test plan](docs/testing/F9-csv-import-test-plan.md)
- [F10 CSV export test plan](docs/testing/F10-csv-export-test-plan.md)
- [F11 PDF sharing test plan](docs/testing/F11-pdf-sharing-test-plan.md)
- [F12 Local deletion test plan](docs/testing/F12-local-deletion-test-plan.md)
- [F13 Recent-file search test plan](docs/testing/F13-recent-search-test-plan.md)
- [F21 Remove from recents test plan](docs/testing/F21-remove-from-recents-test-plan.md)
- [F22 Recent sorting test plan](docs/testing/F22-recent-sorting-test-plan.md)
- [F23 Home shortcut tiles test plan](docs/testing/F23-home-shortcuts-test-plan.md)
- [F24 Spreadsheet system Back test plan](docs/testing/F24-spreadsheet-system-back-test-plan.md)
- [F25 Recent file-type filters test plan](docs/testing/F25-recent-file-filters-test-plan.md)
- [F26 PDF zoom test plan](docs/testing/F26-pdf-zoom-test-plan.md)
- [F27 PDF page jump test plan](docs/testing/F27-pdf-page-jump-test-plan.md)
- [F28 PDF reading position test plan](docs/testing/F28-pdf-reading-position-test-plan.md)
- [F29 PDF bookmarks test plan](docs/testing/F29-pdf-bookmarks-test-plan.md)
- [F30 PDF page notes test plan](docs/testing/F30-pdf-page-notes-test-plan.md)
- [F31 Writer Find & Replace test plan](docs/testing/F31-writer-find-replace-test-plan.md)
- [F32 Writer word/character count test plan](docs/testing/F32-writer-stats-test-plan.md)
- [F33 Writer Undo/Redo test plan](docs/testing/F33-writer-undo-redo-test-plan.md)
- [F34 Writer library search test plan](docs/testing/F34-writer-search-test-plan.md)
- [F35 Notes library search test plan](docs/testing/F35-notes-search-test-plan.md)
- [F36 Share note test plan](docs/testing/F36-share-note-test-plan.md)
- [F37 Duplicate note test plan](docs/testing/F37-duplicate-note-test-plan.md)
- [F38 Notes sorting test plan](docs/testing/F38-notes-sorting-test-plan.md)
- [F39 Writer sorting test plan](docs/testing/F39-writer-sorting-test-plan.md)
- [F40 Spreadsheet library test plan](docs/testing/F40-spreadsheet-library-test-plan.md)
- [F41 Presentation library test plan](docs/testing/F41-presentation-library-test-plan.md)
- [F42 Presentation system Back test plan](docs/testing/F42-presentation-system-back-test-plan.md)
- [F43 Duplicate presentation test plan](docs/testing/F43-duplicate-presentation-test-plan.md)
- [F44 Duplicate CSV sheet test plan](docs/testing/F44-duplicate-csv-sheet-test-plan.md)
- [F45 Duplicate Writer document test plan](docs/testing/F45-duplicate-writer-test-plan.md)
- [F46 PDF Save a Copy test plan](docs/testing/F46-pdf-save-copy-test-plan.md)
- [F47 Share entire presentation test plan](docs/testing/F47-share-entire-presentation-test-plan.md)
- [F48 PDF page rotation test plan](docs/testing/F48-pdf-rotation-test-plan.md)
- [F49 PDF rotation memory test plan](docs/testing/F49-pdf-rotation-memory-test-plan.md)
- [F50 PDF rotation reset test plan](docs/testing/F50-pdf-rotation-reset-test-plan.md)
- [F51 MyAdv signing test plan](docs/testing/F51-myadv-signing-test-plan.md)
- [F52 Back navigation test plan](docs/testing/F52-back-navigation-test-plan.md)
- [F58 Batch share recent files test plan](docs/testing/F58-batch-share-recent-files-test-plan.md)
- [F59 Select visible recent files test plan](docs/testing/F59-select-visible-recent-files-test-plan.md)
- [F62 PDF rotated-copy export test plan](docs/testing/F62-pdf-rotated-copy-export-test-plan.md)
- [F63 PDF watermark/stamp test plan](docs/testing/F63-pdf-watermark-stamp-test-plan.md)
- [F64 DOCX formatted export test plan](docs/testing/F64-docx-formatted-export-test-plan.md)
- [F65 DOCX basic-format import test plan](docs/testing/F65-docx-basic-format-import-test-plan.md)
- [F67 DOCX paragraph alignment test plan](docs/testing/F67-docx-paragraph-alignment-test-plan.md)
- [F68 XLSX export test plan](docs/testing/F68-xlsx-export-test-plan.md)
- [F69 PDF page-range extraction test plan](docs/testing/F69-pdf-page-range-extraction-test-plan.md)
- [F70 PDF merge test plan](docs/testing/F70-pdf-merge-test-plan.md)
- [F71 PDF page text annotation test plan](docs/testing/F71-pdf-page-text-annotation-test-plan.md)
- [F72 DOCX list import test plan](docs/testing/F72-docx-list-import-test-plan.md)
- [F73 DOCX table text import test plan](docs/testing/F73-docx-table-text-import-test-plan.md)
- [F74 RTF import/export test plan](docs/testing/F74-rtf-import-export-test-plan.md)
- [F75 RTF formatting import test plan](docs/testing/F75-rtf-formatting-import-test-plan.md)
- [F78 PDF pages PNG ZIP export test plan](docs/testing/F78-pdf-pages-png-zip-test-plan.md)
- [F77 PDF page PNG export test plan](docs/testing/F77-pdf-page-png-export-test-plan.md)
- [F76 RTF formatting export test plan](docs/testing/F76-rtf-formatting-export-test-plan.md)
- [F14 Text import test plan](docs/testing/F14-text-import-test-plan.md)
- [F15 Find-in-text test plan](docs/testing/F15-find-in-text-test-plan.md)
- [F16 Formula preview test plan](docs/testing/F16-formula-preview-test-plan.md)
- [F17 Slideshow test plan](docs/testing/F17-slideshow-test-plan.md)
- [F18 DOCX text import test plan](docs/testing/F18-docx-import-test-plan.md)
- [F19 DOCX export test plan](docs/testing/F19-docx-export-test-plan.md)
- [F20 Arithmetic formula test plan](docs/testing/F20-arithmetic-formula-test-plan.md)

- PDF text note overlay (feature F57): add a readable multi-line note on the current page when exporting a new flattened PDF copy; original PDF remains unchanged.

- PDF freehand annotation (feature F58): draw with a finger in an annotation pad and export the drawing onto the current page in a new flattened PDF copy; original remains unchanged.

- Reusable PDF typed signature: optionally store the last typed signature in app-private local preferences for reuse on this device; users can leave the remember option unchecked.

- PDF page reorder / custom selection: enter a comma-separated order such as `3,1,2` to export selected pages in that order as a new flattened PDF; duplicate or out-of-range pages are rejected and the source remains unchanged.

- DOCX font-family import: read common `w:rFonts` ascii/hAnsi/eastAsia run properties into Writer font-family spans; the chosen font may be substituted if unavailable on the device.

## Current status

F0/F1 file hub prototype, F2 offline notes, F3 basic PDF reader, F4 plain-text Writer, F5 CSV spreadsheet grid, F6 text-only presentations, F7 image-to-PDF conversion, F8 text export and F9 CSV import are in source. F2–F50 features are implemented to varying prototype levels. F51 adds optional MyAdv-key release signing and F52 documents Back navigation. A signed release APK is created only when the same signing secrets are configured for this repository. Each feature requires successful CI and device acceptance tests before it can be considered verified. Device acceptance tests remain pending until the APK is installed and tested on Android. Work proceeds one feature at a time, with a build after each feature.
