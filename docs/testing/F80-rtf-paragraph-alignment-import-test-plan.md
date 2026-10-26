# F80 — RTF paragraph alignment import

## Acceptance checks
1. Prepare RTF samples with left-aligned, centered, and right-aligned paragraphs using the standard `\\ql`, `\\qc`, and `\\qr` controls.
2. Open each sample in MyOffice and choose the option to import/edit a local copy in Writer.
3. Confirm paragraph text and paragraph breaks are retained and each supported alignment is visible in Writer.
4. Include multiple adjacent paragraphs with different alignments; verify the alignment changes at the correct paragraph boundary.
5. Combine paragraph alignment with bold, italic, underline, strike-through, Unicode escapes, and font-size controls; confirm character formatting still imports.
6. Include empty paragraphs and a trailing paragraph break; confirm import completes without a crash.
7. Confirm the original RTF source remains unchanged.
8. Try an RTF file larger than 10 MiB; confirm the existing safety limit is enforced.

## Limitations
- Only common left, center and right paragraph alignment controls are mapped. Justification, indents, margins, tabs, line spacing, tables, and full RTF fidelity are not included.
- RTF viewers and word processors may interpret unsupported control words differently.

## CI/device verification
- CI must compile the debug APK and verify package identity.
- Physical-device checks are still required to validate the alignment spans and RTF import path.
