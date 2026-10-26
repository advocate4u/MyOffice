# F52 acceptance tests: MyAdv attribution and APK signing

## CI signing configuration

Configure these repository Actions secrets only with a keystore that the project owner is authorized to use:

- `MYADV_KEYSTORE_BASE64`: base64-encoded JKS/PKCS12 keystore bytes
- `MYADV_KEYSTORE_PASSWORD`: keystore password
- `MYADV_KEY_ALIAS`: signing key alias
- `MYADV_KEY_PASSWORD`: key password

Do not commit the keystore, passwords, or private key to Git.

| ID | Scenario | Expected result |
|---|---|---|
| F52-01 | Build without the four secrets | CI creates and uploads the debug APK fallback |
| F52-02 | Build with all four valid secrets | CI creates the release APK using the configured MyAdv key |
| F52-03 | Signed build | `apksigner verify --verbose --print-certs` passes |
| F52-04 | One signing secret is missing | Workflow selects debug fallback, not a partially signed release |
| F52-05 | Invalid keystore or wrong password | Signed release build fails rather than publishing a falsely labelled signed APK |
| F52-06 | Install signed APK on a clean test device | Android Package Installer accepts the APK if the platform requirements are met |
| F52-07 | Install an update over an earlier build | Update succeeds only when package ID and signing certificate match; otherwise uninstall the previous build first or use the same authorized keystore |
| F52-08 | Inspect app Home | “By MyAdv” attribution is visible |

## Important Android behavior

Android verifies APK signatures during installation and update. The app itself cannot force Android's installer to accept an APK or bypass signature mismatch checks. This workflow verifies the signature in CI; the Android Package Installer performs its own signature checks on the device.

## Current limitations

- A signed release APK is not available until all four secrets are configured with a valid keystore.
- Debug APK signing keys may differ across CI environments; do not rely on debug APKs as stable upgrade packages.
- Device installation and update tests remain pending.