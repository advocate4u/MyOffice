# MyOffice Investigation 04 — WPS Feature & Use-Case Inventory

**Scope:** Build a broad, testable inventory of WPS Office feature families to guide MyOffice.
**Target named by user:** WPS Office Android 26.10.0 (package cn.wps.moffice_eng).
**Evidence boundary:** Based on public WPS product pages, help/academy material, and the user's official-channel link. This is not a claim that every feature has been found in the Android build or directly exercised. WPS's Windows, web, Android, and paid-plan capabilities differ. Treat mobile availability as **verify on target build** until observed.

## Status vocabulary

- **Publicly documented** — a public WPS source advertises or explains it.
- **Observed** — directly seen in the specified Android build, with evidence capture.
- **Unknown** — no direct target-build evidence yet.
- **Out of initial scope** — record it, but do not implement until core file safety and compatibility are stable.

## 1. Cross-suite home, navigation, and file management

| Use case / option family | What to inspect | MyOffice decision / acceptance target | Status |
|---|---|---|---|
| Home/dashboard | Module shortcuts, create action, recent files, templates, account entry, promotional/AI cards | Clear original home screen; key actions accessible; distinguish local and cloud files | Unknown on 26.10.0 |
| Create new | Blank DOC, XLS, PPT, PDF/scan where offered; templates | Create only supported types and show format clearly | Publicly documented in suite; verify Android |
| Open/import | Recent, local storage, Android document picker, third-party cloud | Open user-selected files through Android Storage Access Framework where possible | Publicly advertised; verify |
| Recent files | Sort/order, pin/favourite, remove from recent vs delete file | Never make “remove from recent” silently delete source file | Unknown |
| File operations | Rename, copy, move, delete, multi-select, sort, folder browsing | Confirm destructive operations; handle permissions and duplicates | Verify |
| Search | Filename search, recent/local/cloud scope | Show scope and useful empty/error states | Verify |
| File types | DOC/DOCX, XLS/XLSX, PPT/PPTX, PDF, TXT/CSV and image types | Explicit support matrix and graceful unsupported-file errors | DOCX/XLSX/PPTX/PDF publicly advertised; rest verify |
| Share/export | Android share sheet, send copy/link, export format | Clearly distinguish a file copy from a cloud link | Verify |
| Print | Android print service, page settings | Offer only where platform and file renderer support it | Verify |
| Recovery | Auto-recovery, drafts, backup, version history | Save safely and communicate recovery choices | Public WPS ecosystem mentions backup/history; Android behaviour unknown |
| Settings | Language, theme, default format, cache/storage, notifications, privacy, account | Privacy-respecting settings with explicit data retention | Verify |
| Ads/subscription | Premium locks, trial prompts, ads, restore purchases | MyOffice must have its own transparent plan; do not reproduce WPS prompts | Verify |
| Help/feedback | FAQ, contact support, issue reporting, version/build info | In-app troubleshooting and version information | Verify |

## 2. Writer / text documents

| Use case / option family | Inspect / test | MyOffice target | Status |
|---|---|---|---|
| Create/open/save | Blank document, existing DOC/DOCX, Save/Save As, reopen | No silent data loss; verify content after reopening | Publicly documented family; exact Android flow unknown |
| Editing | Cursor, selection handles, cut/copy/paste, undo/redo, select all | Reliable touch editing and predictable cursor placement | Public Android docs describe selection/clipboard; verify target build |
| Text formatting | Font family/size, bold, italic, underline, color, highlight, case | Implement core formatting first | Exact options unknown |
| Paragraph/layout | Alignment, indent, spacing, bullets, numbering, borders/shading | Preserve semantic formatting in DOCX | Unknown |
| Page layout | Margins, orientation, size, columns, page/section breaks | Expose only options supported by renderer | Unknown |
| Insert | Tables, images, shapes, links, headers/footers, page numbers, symbols, date, text boxes | Test import/export fidelity for each object type | Unknown |
| Review | Find/replace, word count, spelling, comments, tracked changes | Phase after editing core; compatibility tests required | WPS suite documents comments/track changes; Android availability unknown |
| Document navigation | Outline/headings, page thumbnails, go to page | Useful for long legal documents | Unknown |
| Templates | Resume, report, letter, invoice, templates | MyOffice-owned templates only; original design and content | Public suite advertises templates; Android catalogue varies |
| Protection | Password encryption, read/edit permission | Do not claim encryption until implementation is reviewed and tested | Public suite help mentions encryption/permissions; Android unknown |
| Export | DOC/DOCX, PDF, TXT and print | Round-trip tests and explicit unsupported-feature warnings | Common formats advertised; exact export choices unknown |

## 3. Spreadsheet

| Use case / option family | Inspect / test | MyOffice target | Status |
|---|---|---|---|
| Workbook/sheets | Create/open XLS/XLSX/CSV, add/rename/delete/reorder sheets | Workbook model and sheet navigation | Spreadsheet publicly documented; Android details unknown |
| Cells | Select ranges, edit values, fill, copy/paste, row/column insert/delete/resize | Touch-friendly cell selection and edit bar | Unknown |
| Formatting | Number/date/currency, font, alignment, borders, fill, wrap, merge | Preserve formats through save/reopen | Unknown |
| Formulas | Formula bar, references, common functions, recalculation/errors | Define supported formula set; never silently corrupt formulas | Unknown |
| Data operations | Sort, filter, freeze panes, find, validation, remove duplicates | Implement incrementally and test larger files | Public product pages advertise data tools; Android availability unknown |
| Charts | Create/edit chart, chart type, labels, legend | Start with a limited tested chart set | Publicly advertised; exact mobile options unknown |
| Tables/pivots | Tables, pivot tables, conditional formatting | Advanced scope; verify feature and licensing before planning | Unknown |
| Import/export | CSV delimiter/encoding, XLSX, PDF | Preserve data types and leading zeros; warn about CSV limitations | Common formats advertised; options unknown |
| Print/layout | Print area, page orientation/scaling, repeat headers | Useful output preview and fit-to-page controls | Unknown |
| Collaboration/review | Comments, protect cells/sheets, shared editing | Later phase with permission model | Public suite features; Android unknown |

## 4. Presentation / slides

| Use case / option family | Inspect / test | MyOffice target | Status |
|---|---|---|---|
| Deck and slides | New/open PPT/PPTX, add/duplicate/delete/reorder slides | Reliable slide list and canvas | Publicly documented; target UI unknown |
| Layout/theme | Slide layouts, themes, background, master support | Start with common layouts and themes owned by MyOffice | Unknown |
| Content | Text boxes, images, shapes, tables, charts, audio/video where supported | Test rendering and unsupported-object handling | Unknown |
| Formatting | Fonts, alignment, colors, object size/position, layers/order | Mobile-friendly editing controls | Unknown |
| Effects | Transitions, animations, timing | Advanced phase; preserve or flag unsupported effects | Public WPS materials mention effects; Android unknown |
| Notes/presenter | Speaker notes, presenter view, laser/pointer, full-screen slideshow | Check device-specific behaviour | Public WPS material describes presentation mode/pointer; verify Android |
| Export/share | PPTX, PDF, images, slideshow/print | Avoid implying perfect fidelity until tested | Unknown |

## 5. PDF toolkit

WPS publicly advertises a broad PDF toolkit; availability and entitlement vary by platform/build/plan. Treat each operation below as a separate test case, not as a confirmed feature in Android 26.10.0.

| Use case / option family | Inspect / test | MyOffice target | Status |
|---|---|---|---|
| Read/render | Open, page navigation, thumbnails, zoom, search, night/reading mode | Stable renderer and large-document performance | Publicly advertised |
| Annotation | Highlight, underline, strikeout, freehand, text note, shapes | Preserve annotations and do not alter source until saved | Publicly advertised; verify |
| Pages | Rotate, reorder, insert, extract, delete, split, merge | Work on a copy by default for destructive edits | Public WPS PDF pages advertise these categories; Android verify |
| Forms | Fill fields, checkboxes, signatures, save form | Retain field values; test flattening behaviour | Public suite advertises forms/signatures; Android verify |
| Signature/stamp | Draw/import signature, stamp, date | Securely handle sensitive documents; no false claim of legal validity | Publicly advertised; verify |
| PDF text/images | Edit text, add/remove images, crop | Advanced; test fonts/layout and explain limitations | Publicly advertised on some surfaces; Android verify |
| Convert | PDF↔DOCX/XLSX/PPTX/images, office→PDF | Measure fidelity, OCR need, and unsupported content | Android product page advertises conversion; exact menu/limits verify |
| OCR/scan | Camera scan, crop, rotate, deskew, OCR, searchable PDF | Local-first processing where possible; disclose any remote processing | Publicly advertised in ecosystem; Android verify |
| Compress/optimize | Quality/resolution settings, size preview | Explain quality trade-offs | Public feature directory; Android verify |
| Security | Password/encryption, permissions, redact, unlock | Redaction must remove underlying content, not just cover it; do not implement unlock bypasses | Feature/plan varies; verify |
| Metadata/watermark | Page number, header/footer, watermark, metadata | Preview and reversible copy | Public feature directory; Android verify |
| AI PDF | Summarize, ask questions, extract key facts, translate | Optional, privacy-controlled, cite page/selection where possible | Public WPS marketing advertises AI PDF; target build/plan unknown |

## 6. Templates, scanner, images, and utilities

- **Templates:** browse/search categories, preview, download/use, edit and save a copy. Verify whether mobile templates require sign-in or premium access.
- **Scan:** capture page, crop edges, rotate, reorder pages, multi-page capture, OCR, export to PDF or editable document. Verify camera/storage permissions and whether any processing is remote.
- **Image/photo tools:** import/crop/rotate/resize, background removal/upscaling/cleanup where advertised. Do not assume desktop/web tools are in Android app.
- **Archive/file utilities:** compression, extraction, batch rename, repair, backup, QR sharing, file transfer or scanner utilities are candidate categories only. Verify each item in the installed app.
- **Clipboard and cross-app integration:** Android share intents, open-with, copy/paste, file picker, external storage/cloud providers.
- **Accessibility and localization:** TalkBack, large text, contrast/theme, Hindi and other language UI, keyboard behaviour, right-to-left content where relevant.

## 7. Cloud, collaboration, and accounts

| Use case | What to investigate | MyOffice design guardrail | Status |
|---|---|---|---|
| Sign-in/account | Supported identity methods, sign-out, account recovery | Never store passwords in plaintext; minimize account data | WPS offers cloud/account ecosystem; target methods unknown |
| Cloud sync | Upload/download, offline edits, conflict resolution, sync status | Explicit conflict handling and offline-first local files | Publicly advertised; exact Android flow unknown |
| External cloud | Google Drive/OneDrive and other integrations, permission prompts | Use official APIs and least privilege; never collect provider credentials directly | Android product page lists third-party cloud support |
| Share links | Link creation, expiry, view/edit permission, revoke | Explicit audience and permissions; no accidental public links | Public WPS collaboration claims; target availability unknown |
| Co-edit | Concurrent edits, live cursors, comments/reactions | Needs backend, versioning and conflict model; defer until core stable | Publicly advertised on suite pages |
| Comments/revisions | Comment threads, track changes, version history | Preserve author/time metadata and access controls | Suite help describes collaboration/review; Android unknown |
| Backup/recovery | Restore older version, deleted-file recovery, autosave | Clear restore point and confirmation | Public WPS ecosystem claims; target details unknown |

## 8. AI and smart tools

Public WPS marketing advertises AI-assisted writing, slide generation, spreadsheet analysis/chart creation, document summarization, PDF question-answering, translation, and layout refinement. Do not assume these are free, available in India, or present in Android 26.10.0.

For each AI entry, inspect:
- Entry point and supported input types.
- Login, premium, quota, region, or network requirements.
- Whether content is uploaded to a server and what disclosure is shown.
- Output insertion/revision flow and undo.
- Error, cancellation, latency, and offline behaviour.

**MyOffice approach:** AI must be optional, transparent about network use, and designed to avoid sending sensitive legal or personal documents without informed user action.

## 9. Settings, reliability, privacy, and edge cases

Test the following across all core modules:
- Screen rotation, small screens, keyboard shown/hidden, background/resume, low memory.
- Large documents, corrupted files, unusual fonts, tables/images, mixed-language text.
- Offline launch, network loss during save/upload, sync conflict.
- Read-only files, no storage permission, insufficient space, duplicate filename.
- App process killed during edit, autosave/recovery, unsaved-changes prompt.
- Password-protected files and unsupported encryption.
- Android share targets, print service, external file provider failures.
- Accessibility text size, TalkBack focus, contrast, Hindi content.
- Subscription/feature locks and misleading or unexpected upgrade prompts.
- Privacy permissions, crash logs, diagnostics, file deletion and cache cleanup.

## 10. Repeatable traversal protocol for each screen

For every screen/menu/action:
1. Capture screen title and visible controls (hide private information).
2. Record entry path and prerequisites (signed in, online, premium, permissions).
3. Tap each visible action once, recording the resulting screen/dialog.
4. Record fields, defaults, toggles, format options, disabled states and validation.
5. Test Back/Cancel and confirm whether state is retained or discarded.
6. Test Save/Export and verify the resulting file outside the editor.
7. Test negative paths: cancel, invalid input, offline, permission denied, duplicate file.
8. Mark evidence as screenshot/video, public docs, or inference.
9. Assign confidence and a MyOffice priority: P0 data safety, P1 core workflow, P2 productivity, P3 advanced/cloud/AI.

Do not infer a hidden option merely because it exists in desktop/web documentation.

## 11. Suggested MyOffice implementation order

1. **P0:** local file open/save, atomic writes, unsaved-changes protection, recovery, file picker, recent files.
2. **P1:** text document basics and DOCX round-trip tests.
3. **P1:** PDF viewer, search, navigation, basic annotations.
4. **P2:** spreadsheet editing basics and CSV/XLSX data integrity.
5. **P2:** presentation viewing/basic editing.
6. **P2:** sharing/export, print, templates, accessibility/localization.
7. **P3:** advanced PDF editing/OCR/conversion, charts/formulas/effects.
8. **P3:** cloud sync/collaboration and optional AI, after privacy/security review.

## 12. Sources

- WPS Office Android: https://www.wps.com/office/android/
- WPS Office official homepage and suite features: https://www.wps.com/
- WPS feature directory (mostly web/online feature catalogue; not an Android guarantee): https://explore.wps.com/features
- WPS unified suite overview: https://www.wps.ai/en-us/docs/wps-pdf/why-choose-wps-pdf/unified-office-suite/common-guides/overview/
- WPS Academy introduction: https://www.wps.com/academy/introduction-of-wps-office/about-wps/1862972/
- WPS Office help system requirements / suite overview: https://help.wps.com/articles/system-requirements-for-wps-office/
- User-provided official YouTube channel: https://youtube.com/@wpsofficeofficial

## Next step

Continue with one feature family at a time and collect direct evidence from the installed Android build or individual tutorial videos. The channel-level link alone does not establish the contents of every video. No APK decompilation, REA run, network interception, or internal-code analysis is claimed in this report.
