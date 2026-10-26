# F57 acceptance tests: Writer rich formatting

| ID | Scenario | Expected result |
|---|---|---|
| F57-01 | Select text and tap Bold | Selected range becomes bold |
| F57-02 | Select text and tap Italic | Selected range becomes italic |
| F57-03 | Select text and tap Underline | Selected range becomes underlined |
| F57-04 | Select text and tap Strike | Selected range becomes struck through |
| F57-05 | Select text and use Bigger/Smaller | Relative size span is applied to selection |
| F57-06 | Select text and choose Red or Blue | Selected text color changes |
| F57-07 | Select text and tap Highlight | Yellow background highlight is applied |
| F57-08 | Tap Left/Center/Right | Editor text gravity changes accordingly |
| F57-09 | Save formatted copy as HTML, leave and reopen | Character formatting is restored |
| F57-10 | Edit an existing HTML document and tap Save | Formatted HTML file is updated |
| F57-11 | Edit existing TXT and tap Save | Plain text file remains supported |
| F57-12 | Make formatting-only change and press Back | Unsaved-change confirmation appears |
| F57-13 | Use Hindi and English text with formatting | Both scripts remain readable |
| F57-14 | Use all formatting offline | No network is required |

## Scope and limitations

- This milestone preserves supported character formatting in local HTML documents, not native Word DOCX styles.
- DOCX export remains a separate compatibility task and does not yet guarantee round-trip formatting.
- Font family, paragraph spacing, margins, tables, images, headers/footers, comments, tracked changes and full DOC/DOCX/RTF fidelity are not implemented by this feature.
- Device acceptance tests are pending until installed and exercised on Android.
