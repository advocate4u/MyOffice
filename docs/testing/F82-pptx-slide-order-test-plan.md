# F82 — PPTX custom slide order

## Acceptance checks
1. Create a PPTX with at least three slides and reorder them in PowerPoint/LibreOffice so the presentation order differs from the slide filename numbering.
2. Import the PPTX into MyOffice and confirm the editable deck follows the presentation slide list and relationship targets, not simply the numeric slide filenames.
3. Confirm each slide's text remains associated with the correct slide after reordering.
4. Import a standard PPTX with conventional sequential slide filenames and confirm its order remains correct.
5. Try a package with missing or nonstandard presentation relationship parts; confirm the importer falls back to numeric slide filenames rather than crashing.
6. Confirm the original PPTX remains unchanged.

## Limitations
- The importer reads common internal relationship targets. It does not preserve slide themes, layouts, images, animations, notes, or other visual presentation properties.

## CI/device verification
- CI must compile the debug APK and verify package identity.
- Physical-device checks are required using a PPTX whose slide order has been manually changed.
