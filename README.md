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
- No login or network permission is required for these core features.
- DOCX/XLSX/PPTX editing, native PDF rendering, scanner/OCR, cloud sync and advanced office functionality are not implemented yet.

## Build and APK workflow

GitHub Actions builds `app-debug.apk` on pushes to `main` and uploads it as `MyOffice-debug-apk`:
https://github.com/advocate4u/MyOffice/actions/workflows/android-debug-apk.yml

Check the latest run before claiming that a feature build succeeded. A successful CI build is not a substitute for installation and device-level acceptance testing.

## Investigation and testing

- [WPS reverse-engineering protocol](docs/investigation/08-wps-reverse-engineering-protocol.md)
- [WPS-to-MyOffice feature evidence matrix](docs/investigation/09-wps-to-myoffice-feature-evidence-matrix.md)
- [F1 local file hub test plan](docs/testing/F1-local-file-hub-test-plan.md)
- [F2 offline notes test plan](docs/testing/F2-offline-notes-test-plan.md)

## Current status

F0/F1 file hub prototype and F2 offline notes implementation are in source. F2 has been committed; CI build is triggered by the push. Device acceptance tests remain pending until the APK is installed and tested on Android. Work proceeds one feature at a time, with a build after each feature.
