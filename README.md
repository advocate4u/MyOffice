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
- Local file hub: system document picker, recent URI list, bounded text previews, file metadata and open-with chooser.
- Offline notes (feature F2): create, edit, save and reopen notes in app-private storage; Hindi/English text; unsaved-change confirmation.
- Basic PDF reader (feature F3): render local PDF pages with Android PdfRenderer and navigate previous/next pages; rendering stays on-device and unsupported/encrypted files show an error.
- Writer first milestone (feature F4): create, edit, save and reopen local UTF-8 plain-text documents; share text through Android's chooser; unsaved-change confirmation.
- Spreadsheet first milestone (feature F5): editable local CSV grid with add-row/add-column controls, quoted-field parsing/escaping, and local save/reopen.
- Presentation first milestone (feature F6): create/edit/save/reopen local text-only slide decks, navigate slides, add slides, and share current slide text.
- Image-to-PDF first milestone (feature F7): select up to 20 local images, create a multi-page PDF on-device, and choose a destination through Android's Save document picker.
- Text export (feature F8): export local notes and Writer content to a user-selected UTF-8 `.txt` file using Android's Save document picker.
- CSV import (feature F9): open an existing local CSV into the editable grid, with a 1 MiB import limit and save-as-new behavior that leaves the original file unchanged.
- CSV export (feature F10): export the current editable grid to a user-selected UTF-8 `.csv` file, with standard comma/quote escaping.
- PDF sharing (feature F11): share a PDF opened in MyOffice through Android's share chooser, with read-only URI access.
- Local deletion (feature F12): delete app-private notes, Writer documents, CSV sheets and slide decks after a confirmation dialog.
- Recent-file search (feature F13): filter the recent file list by filename, case-insensitively, without uploading file data.
- Text import (feature F14): open a supported text file in preview and optionally edit a local copy in Writer, preserving the original source.
- Find-in-text (feature F15): search a text preview case-insensitively and highlight matching phrases locally.
- No login or network permission is required for these core features.
- Rich-text formatting, DOCX import/export, formulas, XLSX/PPTX import/export, slide themes/images/animations, camera scanning/OCR, PDF annotation/search and advanced office functionality are not implemented yet.

## Build and APK workflow

GitHub Actions builds `MyOffice-0.2.0-debug.apk` on pushes to `main` and uploads it as `MyOffice-0.2.0-debug-apk`:
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
- [F14 Text import test plan](docs/testing/F14-text-import-test-plan.md)
- [F15 Find-in-text test plan](docs/testing/F15-find-in-text-test-plan.md)

## Current status

F0/F1 file hub prototype, F2 offline notes, F3 basic PDF reader, F4 plain-text Writer, F5 CSV spreadsheet grid, F6 text-only presentations, F7 image-to-PDF conversion, F8 text export and F9 CSV import are in source. F2 through F8 build steps passed. F9 has been committed; CI build is triggered by the push. App label and original adaptive launcher icon are configured in the proposed 0.2.0 update. Device acceptance tests remain pending until the APK is installed and tested on Android. Work proceeds one feature at a time, with a build after each feature.
