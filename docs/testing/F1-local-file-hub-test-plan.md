# F1 acceptance tests: Local file hub

Run against the MyOffice debug APK on Android 8+; preferably also test on the user's Samsung Galaxy S21 FE.

| ID | Scenario | Expected result |
|---|---|---|
| F1-01 | Launch in airplane mode | Home screen appears without network |
| F1-02 | Open a small UTF-8 TXT file | System picker appears; file details and text preview appear |
| F1-03 | Text contains Hindi and emoji | Text is readable and not corrupted |
| F1-04 | Return to home | Opened file appears in Recent files |
| F1-05 | Force-stop and relaunch | Recent list persists if URI permission remains valid |
| F1-06 | Select CSV/Markdown/JSON/XML/log | Bounded text preview appears |
| F1-07 | Select PDF or DOCX | Metadata screen appears; no claim of native rendering/editing |
| F1-08 | Choose Open with another app | Android chooser opens when compatible handlers exist |
| F1-09 | Delete/move source or revoke provider access | App does not crash; inaccessible item is handled gracefully |
| F1-10 | Select a very large text file | Preview stops at 1 MiB and shows a limit notice |
| F1-11 | Cancel file picker | Home remains usable |
| F1-12 | No storage permission granted | User-selected files still open through SAF |
| F1-13 | Repeatedly open files | Recent list stays capped at 12 and newest is first |
| F1-14 | Inspect network behavior | Core feature works without internet and makes no app network requests |

## Build verification
- GitHub Actions workflow named MyOffice Android Debug APK must complete successfully.
- Artifact MyOffice-debug-apk must contain app-debug.apk.
- Install APK on a real Android device and execute all tests above before marking F1 device-verified.
