# F51 acceptance tests: MyAdv release signing

| ID | Scenario | Expected result |
|---|---|---|
| F51-01 | Configure all four MyAdv signing secrets and build | CI creates a MyAdv-signed release APK |
| F51-02 | Verify release APK with Android apksigner | Signature verification succeeds |
| F51-03 | Inspect APK package ID | Exactly org.myoffice.app |
| F51-04 | Verify APK alignment | zipalign check succeeds |
| F51-05 | Build without signing secrets | Debug APK still builds; release signing is skipped clearly |
| F51-06 | Update installed MyOffice release signed with the same key | Android accepts the update when version code is not downgraded |
| F51-07 | Update a debug-signed installation with MyAdv-signed release | Android rejects the certificate mismatch; uninstall the debug build first if switching signing channels |
| F51-08 | Install MyOffice beside MyAdv | Both coexist because their application IDs differ |

## Required repository secrets

Configure these same MyAdv signing secrets in MyOffice under Settings → Secrets and variables → Actions:

- MYADV_KEYSTORE_BASE64
- MYADV_STORE_PASSWORD
- MYADV_KEY_ALIAS
- MYADV_KEY_PASSWORD

Never commit the keystore or passwords to source control.

## Platform behavior

Android verifies APK signing during installation and updates automatically. App code cannot run before installation to perform a separate pre-install check. CI verifies the signed artifact before publishing it.

## Limitations

A MyAdv-signed APK is not produced until this repository can access the same keystore secrets. Device install/update tests remain pending.
