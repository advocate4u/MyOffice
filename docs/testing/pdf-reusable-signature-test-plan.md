# Reusable PDF typed signature acceptance tests

| ID | Scenario | Expected result |
|---|---|---|
| SIG-01 | Open Fill & Sign on a clean install | Signature field is empty and remember checkbox is unchecked |
| SIG-02 | Enter a name and check Remember this signature | New signed copy is exported and name is saved locally |
| SIG-03 | Open Fill & Sign again | Previously saved signature is prefilled |
| SIG-04 | Change name and keep remember checked | New name replaces the saved local signature |
| SIG-05 | Uncheck remember and enter a different name | Current PDF uses the entered name; prior saved value is not overwritten |
| SIG-06 | Use airplane mode | Signature reuse works locally |
| SIG-07 | Clear app data | Stored signature is removed by Android with the rest of app-private preferences |
| SIG-08 | Inspect exported PDF | Signature is typed/flattened and not a certificate-based digital signature |

## Privacy and limitations

- Signature text is stored only in app-private SharedPreferences; it is not uploaded.
- This is a convenience feature, not identity verification or a cryptographic PDF signature.
- Device acceptance tests remain pending.
