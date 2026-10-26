# F60 acceptance tests: Writer rich-format save safety

| ID | Scenario | Expected result |
|---|---|---|
| F60-01 | Open an unchanged HTML document and press Back | Returns to Writer list without a false unsaved-changes prompt |
| F60-02 | Edit an HTML document and press Back | Unsaved-changes prompt appears |
| F60-03 | Choose Save for a changed HTML document | Existing HTML is updated with rich formatting preserved |
| F60-04 | Create a plain-text document and save without formatting | A plain-text document is saved |
| F60-05 | Apply bold/color/underline to a new document and tap Save document | Formatted HTML copy is saved instead of silently losing the formatting |
| F60-06 | Apply formatting to a TXT document and choose Save from the Back prompt | A formatted HTML copy is created; the original TXT remains unchanged |
| F60-07 | Edit text only in an existing TXT document and save | Plain-text save behavior remains available |
| F60-08 | Change formatting but not the visible characters | Unsaved-changes prompt detects the formatting change |
| F60-09 | Save a formatted document, reopen it offline | Supported character formatting is restored |
| F60-10 | Use Hindi/English text with formatting | Text remains readable and formatting survives HTML save/reopen |

## Current limitations

- Rich formatting is stored in HTML; exporting to TXT intentionally removes formatting.
- DOCX export currently focuses on text and does not yet preserve the full rich-text styling model.
- Device acceptance tests remain pending.
