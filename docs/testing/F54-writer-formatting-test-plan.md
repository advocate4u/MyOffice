# F54 acceptance tests: Writer formatting

| ID | Scenario | Expected result |
|---|---|---|
| F54-01 | Select text and apply font size 8 pt | Selected characters render smaller |
| F54-02 | Apply font size 48 pt | Selected characters render larger within supported range |
| F54-03 | Enter a size below 8 or above 48 | Validation message appears; invalid size is not applied |
| F54-04 | Apply Title, Heading 1, Heading 2, and Normal | Selected text receives the expected relative size/style |
| F54-05 | Apply each available font color | Selected text uses the chosen color |
| F54-06 | Clear formatting | Supported character spans are removed while text remains unchanged |
| F54-07 | Save formatted copy as HTML and reopen | Supported rich-text spans remain visible |
| F54-08 | Export as DOCX and open in Microsoft Word or another compatible suite | Text and supported basic character styles are readable; inspect fidelity manually |
| F54-09 | Export as RTF | RTF file is created and opens in compatible editors |
| F54-10 | Format Hindi and English text | Both scripts remain readable |
| F54-11 | Apply font family and absolute font size, then export DOCX | Generated OOXML includes run font and size properties where supported |

## Compatibility limits

- This is a lightweight rich-text editor, not a full Microsoft Word layout engine.
- DOCX import supports basic text and character formatting; images, tables, section layouts, headers/footers, comments, macros and advanced styles may not survive.
- Legacy binary `.doc` is not parsed natively. Use a compatible office app to convert it to `.docx` or `.rtf` first.
- Device acceptance tests remain pending.
| F54-12 | Justify a paragraph and export DOCX | Paragraph uses justified alignment in compatible Word processors |

| F54-13 | Select text and apply Superscript/Subscript | Selected characters render as raised/lowered text and remain formatted in HTML/DOCX export |
