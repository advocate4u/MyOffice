# F81 — Basic PPTX text import

## Acceptance checks
1. Select a local `.pptx` file from the Android file picker or open it from recents.
2. Confirm MyOffice imports it as a new editable local text-only slide deck.
3. Check that slides are listed in numeric slide-file order and that the first non-empty paragraph becomes the slide title, with remaining paragraphs in the body.
4. Open several slides and confirm Unicode text, punctuation and paragraph breaks remain readable.
5. Edit a slide and save the local deck; confirm the source PPTX remains unchanged.
6. Test a presentation with empty slides and a presentation with no readable slide XML; empty slides should be represented safely, and unsupported files should show an error rather than crash.
7. Try a PPTX over 25 MiB, one with over 100 slides, or one with expanded slide XML over the configured limit; confirm the import stops safely.
8. Confirm the parser rejects XML DTD/external-entity declarations and does not fetch external resources.

## Limitations
- This is text-only import. Themes, images, charts, SmartArt, tables, animations, notes, speaker comments, layout fidelity, custom slide ordering from relationships, and PPTX export are not supported.
- The imported deck is a new MyOffice document; the original PPTX is never modified.

## CI/device verification
- CI must compile the debug APK and verify package identity.
- Physical-device checks are required for the file picker, representative PPTX samples and edit/save behavior.
