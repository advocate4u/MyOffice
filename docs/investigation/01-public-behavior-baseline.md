# Investigation 01 — Publicly Documented WPS Android Workflows

Date: 2026-10-26

## Scope and evidence quality

This is a public-documentation baseline, not a decompilation or runtime trace of the WPS APK. The source material is WPS's own public product/help pages. No APK has been supplied to or analyzed by REA in this investigation.

## Document workflow candidate

Public WPS Writer documentation describes this workflow:

1. Launch WPS Office.
2. Choose Writer / Docs.
3. Create a blank document or open an existing DOC/DOCX file.
4. Edit text and formatting.
5. Save in a supported document format or export to PDF.
6. Share or sync the document where supported.

Source: https://www.wps.com/feature/the-free-docx-editor-for-word-files/

## Office-suite scope

WPS publicly describes an integrated suite containing Writer, Spreadsheet, Presentation, and PDF functionality, including opening common DOCX, XLSX, PPTX, and PDF files.

Sources:
- https://www.wps.com/office/android/
- https://www.wps.com/feature/productivity-suite/

## PDF workflow candidate

WPS's public Android PDF page describes opening a PDF, editing or annotating it, signing/filling forms, organizing pages, converting formats, and saving/sharing the result. Availability may depend on app version, account, plan, file type, and region.

Source: https://www.wps.com/feature/pdf-editor-for-android/

## Initial MyOffice acceptance criteria (proposed, not yet implemented)

- User can choose a local document through Android's system file picker.
- App identifies supported file types and gives a clear error for unsupported types.
- User can create a local plain-text document and save it.
- Saved documents remain after app restart and can be reopened.
- Changes are not silently discarded; unsaved state is visible.
- Original implementation and assets are used.
- DOCX/XLSX/PPTX compatibility is not claimed until tested with a documented test corpus.

## What remains unknown

- Actual screen-by-screen behavior in the user's installed WPS version.
- Internal code paths, classes, libraries, and data flow.
- Exact file-access, autosave, recovery, and permission behavior.
- Which features are available without sign-in or subscription on the user's device.
- Whether any APK-specific analysis can be performed with the user's authorization and a supported REA environment.

## Next evidence collection step

On the user's phone, record the installed WPS version and manually test one small, non-sensitive document: open it, edit a sentence, save, close, reopen, and observe the result. Capture screenshots of relevant screens if comfortable, with private content hidden. These are user-visible observations; they do not reveal internal implementation. For internal static analysis, use only an APK and analysis scope the user is authorized to examine.
