# F64 acceptance tests: Formatted DOCX export

| ID | Scenario | Expected result |
|---|---|---|
| F64-01 | Select text, apply Bold, export DOCX | WordprocessingML run contains bold formatting |
| F64-02 | Apply Italic, Underline and Strikethrough | Supported run properties are written to DOCX |
| F64-03 | Apply red/blue font color | DOCX contains the selected RGB font color |
| F64-04 | Apply yellow highlight | DOCX uses a yellow text highlight |
| F64-05 | Apply Bigger/Smaller | DOCX run font size reflects the relative-size span |
| F64-06 | Center or right-align the editor, export | Paragraph alignment property is written |
| F64-07 | Include Hindi/English, punctuation, XML special characters and emoji | Exported package remains valid XML and preserves readable text |
| F64-08 | Include tabs and blank lines | Paragraph breaks and tab characters remain |
| F64-09 | Open exported DOCX in a compatible office app | Text and supported formatting render as expected |
| F64-10 | Cancel Save document picker | No destination document is written |
| F64-11 | Export a plain TXT document | Plain text remains exportable to DOCX |
| F64-12 | Export from HTML-backed Writer document | Suggested filename ends in .docx |

## Compatibility limitations

This milestone preserves a subset of character spans and the editor's overall alignment. It does not preserve Word tables, images, headers/footers, margins from imported files, page breaks, custom styles, comments, tracked changes, fields, embedded objects or full DOCX round-trip fidelity. Device and third-party office-app acceptance tests remain pending.
