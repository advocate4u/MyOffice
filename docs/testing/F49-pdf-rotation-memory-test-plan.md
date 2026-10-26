# F49 acceptance tests: Remember PDF rotation per document

| ID | Scenario | Expected result |
|---|---|---|
| F49-01 | Rotate PDF A once and leave reader | Rotation preference is saved locally |
| F49-02 | Reopen PDF A | Its saved orientation is restored |
| F49-03 | Open PDF B | PDF B uses its own saved orientation or 0° by default |
| F49-04 | Rotate PDF B and reopen PDF A | Each document keeps its independent orientation |
| F49-05 | Rotate through 360° | Saved orientation cycles back to 0° |
| F49-06 | Use airplane mode | Rotation memory works offline |
| F49-07 | Inspect original PDF | Source bytes remain unchanged |
| F49-08 | Revoke access to a PDF | Reader reports unavailable source rather than crashing |

## Current limitations

- Rotation is keyed by the selected document URI. If the same file is exposed through a different URI, it may have separate settings.
- Rotation is a viewing transform only and is not written back into the PDF.
- Device acceptance testing is required before marking verified.
