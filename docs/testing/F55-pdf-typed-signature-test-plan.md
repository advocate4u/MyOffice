# F55 acceptance tests: PDF typed signature

| ID | Scenario | Expected result |
|---|---|---|
| F55-01 | Open a local PDF and choose Fill & Sign · Typed signature | Name-entry dialog appears for the current page |
| F55-02 | Submit an empty signature | Validation message appears; no export begins |
| F55-03 | Enter a name and export | Save dialog creates a new annotated PDF copy |
| F55-04 | Open exported copy | Signature text is visible near the lower part of the selected page |
| F55-05 | Inspect other pages | Only the selected page receives the signature |
| F55-06 | Inspect original PDF | Original source remains unchanged |
| F55-07 | Use airplane mode | Signature export works locally |
| F55-08 | Enter a long name | Verify it stays within page bounds; report clipping if any |
| F55-09 | Verify legal/digital-signature expectations | UI clearly says typed signature is not certificate-based digital signing |

## Current limitations

- Signature is typed text flattened into the rendered page; it is not a cryptographic PDF signature or identity verification.
- Export flattens page content and can reduce text selectability/searchability and image quality.
- Device acceptance tests remain pending.