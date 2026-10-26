# Official WPS Office YouTube: first-pass video inventory and feature extraction

Review started: 2026-10-26  
Channel supplied by user: https://www.youtube.com/@WPSOfficeOfficial  
Target product for our own implementation: MyOffice  
Purpose: derive an evidence-based feature/use-case specification from public demonstrations, then implement original features that support free/offline learning.

## Important status and limits

This is a **first-pass search-index inventory**, not a completed review of every channel video. YouTube's channel pages did not expose a complete, paginated video list through the current research interface, so the list below must not be described as exhaustive. A full pass requires a reliable complete channel/playlist inventory and, for videos whose visual behavior matters, watching the videos and recording timestamped observations. Public video observations are not APK reverse engineering and do not establish that a feature works on WPS Android 26.10.0 or offline.

## First-pass video register

| ID | Video | Date | What the available source supports | Platform / confidence |
|---|---|---|---|---|
| WPS-YT-001 | [Introduction of WPS Office](https://www.youtube.com/watch?v=iuV-I4iPgoM) | 2022-03-08 | Official description introduces Writer, Spreadsheet, Presentation and PDF, and says WPS Academy provides beginner and advanced tutorials. Use this as suite-level discovery, not a detailed mobile screen specification. | Platform unclear from metadata; discovery only |
| WPS-YT-002 | [How to use automatic numbering in WPS Writer](https://www.youtube.com/watch?v=-vFl0BFwoow) | 2021-09-05 | Official description identifies automatic numbering in Writer. MyOffice research tasks: numbered lists, restart/continue numbering, nested lists, undo/redo and save/reopen persistence. These are test hypotheses until visuals are reviewed. | Platform unclear; title/description supported |
| WPS-YT-003 | [How to set a worksheet in WPS Spreadsheet](https://www.youtube.com/watch?v=-BnUSCFh0nA) | 2021-08-30 | Official description identifies worksheet setup. MyOffice research tasks: sheet options, rename/add/delete/move/hide if shown, tab navigation, confirmation dialogs and persistence. Do not assume each option is in this video. | Platform unclear; title/description supported |
| WPS-YT-004 | [How to set up a slideshow in WPS Presentation](https://www.youtube.com/watch?v=A1DFjJlbt44) | 2021-08-30 | Official description identifies slideshow setup. MyOffice research tasks: start-from-beginning/current-slide, orientation/display options, presenter controls, exit behavior and whether these are desktop-only. | Platform unclear; title/description supported |
| WPS-YT-005 | [How to insert video in WPS Presentation](https://www.youtube.com/watch?v=RA7HP9Kq_T0) | 2021-09-21 | Official description identifies inserting video into a presentation. MyOffice research tasks: insertion source, file picker, playback/preview, unsupported format/error behavior, file size and whether the feature is available on Android. | Platform unclear; title/description supported |
| WPS-YT-006 | [How to convert PDF files into documents in WPS PDF](https://www.youtube.com/watch?v=2LCK9UlAZRc) | 2021-10-18 | Official description labels PDF-to-document conversion as a premium function. MyOffice research tasks: input selection, output formats, conversion result, layout fidelity, OCR need, paywall and failure handling. It is not evidence that conversion is free or offline. | Platform unclear; title/description supported |

### Source metadata

The search index returned these videos as published by “WPS Office” and their descriptions refer to WPS Academy:
- WPS-YT-001: https://www.youtube.com/watch?v=iuV-I4iPgoM
- WPS-YT-002: https://www.youtube.com/watch?v=-vFl0BFwoow
- WPS-YT-003: https://www.youtube.com/watch?v=-BnUSCFh0nA
- WPS-YT-004: https://www.youtube.com/watch?v=A1DFjJlbt44
- WPS-YT-005: https://www.youtube.com/watch?v=RA7HP9Kq_T0
- WPS-YT-006: https://www.youtube.com/watch?v=2LCK9UlAZRc

The title and description are evidence of topic only. The task-specific questions in the third column are our investigation checklist, not claims about what the video visibly demonstrates.

## Video review procedure for every accessible video

For each video:
1. Capture exact title, URL, publication date, description, playlist, and retrieval date.
2. Identify the demonstrated platform (Android phone, Android tablet, iOS, Windows, Mac, web) only when visual evidence supports it.
3. Review the video from start to finish. If captions/transcript exist, use them as an aid, not a substitute for visual verification.
4. Split it into discrete workflows; record timestamps for each relevant control, menu, dialog, error, and result.
5. Record prerequisites: sign-in, network, permission, subscription, region, file type, existing document state.
6. Record normal path, cancel/back path, invalid input, failure/offline path if the video shows it. Mark unshown branches “not demonstrated”; do not invent them.
7. Map each observation to a row in docs/investigation/05-screen-traversal-ledger.md.
8. Convert useful behavior into an original MyOffice acceptance test; do not copy WPS assets, source code, branding, or pixel-perfect proprietary UI.
9. Keep platform/version-specific uncertainty explicit.
10. Mark the video reviewed only when the entire accessible video has been reviewed; mark channel inventory complete only when all accessible playlists/videos have been reconciled.

## MyOffice acceptance-test examples derived from the video topics

### Writer: numbered lists
- Create a document offline and type a numbered list.
- Press Enter to create the next item; test Enter twice to end the list.
- Create a nested list and change list level if supported.
- Undo and redo numbering changes.
- Save, close, reopen and confirm numbering persists.
- Test with Hindi text, long wrapped lines, large font, and copy/paste.
- Record unsupported features and accessibility behavior.

### Spreadsheet: worksheet setup
- Create a workbook offline.
- Add and rename a sheet; test move/hide/delete only if implemented.
- If deleting a sheet, show a clear confirmation and ensure Cancel preserves the sheet.
- Enter sample data, save, close and reopen.
- Test one-sheet workbook edge cases and dirty/unsaved state.
- Ensure local file remains available in airplane mode.

### Presentation: slideshow setup
- Create a two-slide presentation offline.
- Start presentation and exit using available controls/back gesture.
- Check orientation, slide navigation, display sleep behavior and accessibility.
- Reopen and confirm slide content is intact.
- Separate mobile capabilities from desktop-only options.

### Presentation: video insertion
- Test a small locally stored supported video and an unsupported/corrupt file.
- Make the file dependency explicit: if media is linked rather than embedded, explain what happens when moved.
- Test preview and slideshow playback, if supported.
- Confirm offline playback if the media is stored locally.
- Do not implement video embedding until a reliable file-format/library approach is selected.

### PDF conversion
- Distinguish text-based PDF from scanned-image PDF.
- Compare text order, tables, page breaks, images and Hindi glyphs after conversion.
- Test no-network behavior, file size limits, cancellation, error messaging and premium gates.
- Never claim conversion is lossless without measuring output quality.
- If OCR is used, make language support, confidence and privacy visible.

## Education-first priorities

The public video review should help identify what a lightweight, original app can offer, but the main goal is student access:
- local document/PDF reading and local saving without mandatory login;
- offline lesson packs with subject, class, language, size and content licence shown;
- reading notes, highlights and simple practice activities;
- Hindi/English interface and accessible text sizing;
- low-storage/low-RAM behavior and clear recovery from crashes or interrupted writes;
- optional downloads and sync that never block already downloaded lessons;
- educational content only from public-domain, openly licensed, user-provided or permissioned sources;
- no unnecessary collection of children's personal data.

## Next required pass

1. Build a complete video/playlist index from the official channel, with pagination and retrieval date.
2. Review the videos by topic group: Writer, Spreadsheet, Presentation, PDF, home/file management, templates, scanning/OCR, sharing/cloud, AI, settings/privacy/accessibility.
3. Add timestamp-level rows rather than treating one video as one feature.
4. Verify relevant mobile behavior on the exact WPS Android build separately.
5. Update the MyOffice implementation backlog only after evidence and acceptance criteria are recorded.

## Completion statement

As of this commit, six official-channel videos have been identified by public search and logged by topic. This does not mean all six have been watched end-to-end, that their screen-level behavior has been verified, or that the channel-wide inventory is complete. Direct device interaction, APK analysis and REA execution remain unperformed.
