# F67 acceptance tests: DOCX paragraph alignment

| ID | Scenario | Expected result |
|---|---|---|
| F67-01 | Import a DOCX with left-aligned paragraphs | Imported Writer content uses left alignment |
| F67-02 | Import a DOCX with centered paragraphs | Imported Writer content uses centered alignment |
| F67-03 | Import a DOCX with right-aligned paragraphs | Imported Writer content uses right alignment |
| F67-04 | Import a DOCX with justified paragraphs | Imported Writer content uses justified alignment where supported |
| F67-05 | Export a Writer document with center alignment | DOCX paragraph properties include center alignment |
| F67-06 | Export a Writer document with right alignment | DOCX paragraph properties include right alignment |
| F67-07 | Export a Writer document with left alignment | DOCX uses default/explicit left alignment |
| F67-08 | Reopen imported DOCX and save formatted HTML | Character styling and paragraph alignment remain in the editor session; HTML persistence may not preserve all paragraph gravity metadata |
| F67-09 | Import a DOCX without paragraph properties | Default left alignment is used |
| F67-10 | Import malformed DOCX XML | Clear import error; app remains usable |

## Current limitations

- Alignment support is limited to left, center, right and justified.
- Tables, page layout, margins, columns and advanced paragraph styles are not preserved.
- Device acceptance tests remain pending.
