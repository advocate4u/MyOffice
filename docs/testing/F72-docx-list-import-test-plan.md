# F72 acceptance tests: DOCX bullet and numbered-list import

| ID | Scenario | Expected result |
|---|---|---|
| F72-01 | Open DOCX with a common bullet list | Imported paragraphs begin with an editable bullet marker |
| F72-02 | Open DOCX with decimal numbering | Imported paragraphs begin with sequential editable numbers |
| F72-03 | Open DOCX with multiple list IDs | Counters are tracked separately by numbering ID and level |
| F72-04 | Open DOCX with nested levels | Level-specific definition is used where available; unsupported formats degrade safely |
| F72-05 | Open DOCX without numbering.xml | Normal paragraph text still imports without crashing |
| F72-06 | Save imported list as HTML and reopen | Bullet/number prefixes remain visible as text |
| F72-07 | Export imported list to DOCX | List markers remain readable, even though they are emitted as text rather than native numbering definitions |
| F72-08 | Open malformed numbering XML | Clear import error appears; app remains usable |
| F72-09 | Inspect source DOCX | Original file remains unchanged |

## Limitations

This milestone converts common list markers into editable text prefixes. It does not preserve Word numbering definitions, custom bullet glyphs, multilevel restart rules, list indentation, list styles or semantic list metadata. Device acceptance tests remain pending.
