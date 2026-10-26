# F79 — RTF paragraph alignment export

## Acceptance checks
1. Create a Writer document and enter at least four separate paragraphs.
2. Set one paragraph left-aligned, one centered, and one right-aligned using the Writer alignment controls.
3. Export a copy as RTF and open it in a compatible word processor or RTF viewer.
4. Confirm each paragraph's supported alignment is retained and the paragraph text remains intact.
5. Combine alignment with bold, italic, underline, and font-size formatting; confirm the existing character-format export still works.
6. Export a document containing blank lines and a trailing newline; confirm paragraph boundaries remain intact and export completes.
7. Confirm exporting does not modify the original local Writer document.
8. Try a document with no explicit alignment spans; it should export left-aligned by default.

## Limitations
- This feature exports paragraph alignment only. justified alignment, RTF paragraph alignment import, margins, indents, line spacing, tables, and full RTF fidelity are not included.
- External RTF viewers may render fonts and spacing differently.

## CI/device verification
- CI must compile the debug APK and verify the package identity.
- Physical-device checks are still required for Writer alignment controls and the exported RTF in an external viewer.
