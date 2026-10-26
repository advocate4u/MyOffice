# F66 acceptance tests: DOCX paragraph alignment

| ID | Scenario | Expected result |
|---|---|---|
| F66-01 | Open DOCX with centered paragraph | Imported paragraph displays centered in Writer where Android honors paragraph spans |
| F66-02 | Open DOCX with right-aligned paragraph | Imported paragraph displays right-aligned |
| F66-03 | Open DOCX with justified paragraph | Import falls back to normal/left alignment when exact justification cannot be represented by the current editor |
| F66-04 | Export a document with imported center/right alignment | DOCX paragraph properties contain corresponding `w:jc` values |
| F66-05 | Export a document whose overall editor alignment is Center or Right | Global editor alignment is used when no per-paragraph alignment span exists |
| F66-06 | Mix left and center paragraphs | Per-paragraph alignment is kept independently where supported |
| F66-07 | Save imported content as HTML then reopen | Alignment survives HTML round-trip where supported |
| F66-08 | Use Hindi and English in aligned paragraphs | Text remains readable |
| F66-09 | Open DOCX with missing or malformed paragraph properties | Import defaults safely to left alignment |

## Limitations

Paragraph alignment is a subset of Word layout. Indents, tabs, spacing before/after, line spacing, lists, page layout and section-level settings are not fully preserved. Device and third-party office-app tests remain pending.
