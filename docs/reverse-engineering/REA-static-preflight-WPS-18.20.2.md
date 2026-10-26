# REA-led WPS APK investigation — first static preflight

Date: 2026-10-26  
Target supplied in this conversation: WPS_Office_18.20.2.apk  
Evidence status: **STATIC-PREFLIGHT only**. This is not a completed REA/JADX decompilation and not runtime verification.

## 1. Artifact identity

| Field | Observed value |
|---|---|
| File name | WPS_Office_18.20.2.apk |
| Size | 187,912,105 bytes (about 179.2 MiB; file manager may show 187.9 MB) |
| SHA-256 | 5f5d88fe2988a5f415843da40c4d0481713d19bf14355475d963928df1c5b8a6 |
| Manifest package | cn.wps.moffice_eng |
| Version name | 18.20.2 |
| Version code | 1544 |
| minSdk | 24 |
| targetSdk | 35 |
| compileSdk | 35 |
| DEX files | 17 (classes.dex through classes17.dex) |
| Native libraries | 151: 76 arm64-v8a and 75 armeabi-v7a |
| ZIP entries | 18,968 |
| Uncompressed archive size | 343,088,620 bytes |

The package and version are consistent with the filename, but that alone does **not** establish that the APK is official, intact, safe, or working.

## 2. Signing identity warning

The legacy JAR-signature certificate in META-INF/BALA.RSA reports:

- Subject/issuer: CN="rockz5555 OU=Droid Freedom Unit O=Droid Freedom Inc L=Unknown S=Unknown C=LK"
- SHA-256 certificate fingerprint: 0cfb4663831a0f8bdb973aad44e221a8ba78f7f684bd0b17d3b44bec82316484
- Signature algorithm: SHA256withRSA, 2048-bit RSA key.

This is a third-party-looking, self-issued identity, not evidence of an official WPS publisher signature. It may indicate a repackaged/re-signed build, but this observation alone does not prove malicious behavior. **Do not treat this file as a trusted official WPS installer until its source and signature are independently verified.** Do not sign MyOffice with this certificate or reuse its assets.

## 3. Manifest observations

A custom read-only parser read the Android binary XML. This is a preliminary static inventory, not REA's evidence-producing analysis engine.

- Declared components: 728 activities, 20 activity aliases, 177 services, 17 receivers and 19 providers.
- Permissions include INTERNET, network/Wi-Fi state and change permissions, CAMERA, RECORD_AUDIO, MANAGE_EXTERNAL_STORAGE, READ/WRITE_EXTERNAL_STORAGE, SYSTEM_ALERT_WINDOW, GET_ACCOUNTS, POST_NOTIFICATIONS, WAKE_LOCK, NFC and app-specific broadcast permissions.
- Application class: cn.wps.moffice.base.KApplication.
- Application manifest sets usesCleartextTraffic=true, declares a network security config, largeHeap=true, and legacy external-storage compatibility. These are manifest declarations only; their runtime implications need separate investigation.
- Manifest entries include Writer-related activities, a scanner launcher alias, PDF conversion service, scanner/cloud-sync services, note editing/search activities, and spreadsheet/presentation/PDF multi-process service families.

These names are useful leads for REA class/method tracing. Component names and DEX strings do not prove that a feature is reachable in the UI or works without an account/network.

## 4. Archive inventory clues

- resources.arsc: 48,796,512 bytes.
- 17 DEX files; many DEX files contain strings matching terms such as Writer, Spreadsheet, Presentation, PDF, scanner, OCR, template, cloud, convert, export, print, share and AI.
- Native libraries include PDF-related libkwopdf.so, OpenCV, and other libraries. The presence of a library is not proof of how or when it is used.
- Both arm64-v8a and armeabi-v7a native libraries are present.

## 5. REA execution status and blocker

The official REA guide says Android package inspection uses rea inspect-android-package with a full JDK 17+ and the pinned jadx-headless-mcp-0.7.1-all.jar; it also supports class search, class inspection, method inspection and reference tracing. See:

- https://github.com/morluto/rea
- https://github.com/morluto/rea/blob/main/docs/android-analysis.md

**REA has not yet been run against this APK in the current execution environment.** The environment has Java 21 but does not have JADX, Apktool or REA installed; outbound DNS/network access to fetch the REA repository and pinned JADX JAR failed. Therefore this report does not claim REA decompilation, method-level findings, authenticity verification or runtime testing.

Next authorized REA probes should be run on a machine with the official REA package and pinned JADX engine:

1. rea inspect-android-package WPS_Office_18.20.2.apk
2. rea search-android-classes WPS_Office_18.20.2.apk cn.wps.moffice.writer.Writer
3. Search for cn.wps.moffice.kfs.mfs.core.spreadsheet, cn.wps.moffice.kfs.mfs.core.presentation, cn.wps.moffice.main.scan, cn.wps.moffice.pdf, and note editor/search classes.
4. Inspect relevant classes and methods, then trace references from the document-open flow to editor setup, save/export, permissions, entitlement checks and network boundaries.
5. Preserve REA output with artifact hashes and classify each claim as static evidence, inferred, or runtime-observed.

## 6. MyOffice design decisions informed by this preflight

- Use an independent package ID, app name and original icon.
- Implement public/user-visible workflows rather than copying WPS code, brand assets, private APIs or proprietary implementation.
- Keep core notes, text writing, CSV editing and local PDF/image workflows offline-first.
- Add tests for storage access, cancellation, unsupported files, permissions and failure branches.
- Do not infer a WPS workflow solely from a manifest class or a string match.
