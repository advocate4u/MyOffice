# F54 acceptance tests: Writer paragraph styles

| ID | Scenario | Expected result |
|---|---|---|
| F54-01 | Select a paragraph and choose Heading 1 | Selected paragraph becomes bold and larger |
| F54-02 | Choose Heading 2 | Paragraph gets the smaller heading style |
| F54-03 | Choose Paragraph | Paragraph text is set to normal relative size |
| F54-04 | Choose Align left / Center paragraph / Align right / Justify | Alignment span is applied to the selected paragraph |
| F54-05 | Select a range within a paragraph | Style applies to the containing paragraph |
| F54-06 | Save a formatted document as HTML and reopen it | Basic supported character formatting remains |
| F54-07 | Export to DOCX and open in Microsoft Word / compatible office app | Verify text and supported formatting; note that complex layout may differ |
| F54-08 | Apply heading to an empty document | Helpful prompt appears; app does not crash |
| F54-09 | Use Hindi and English mixed text | Input remains editable |
| F54-10 | Use airplane mode | Formatting and local save work offline |

## Compatibility scope

These controls are an incremental compatibility milestone, not full Microsoft Word parity. Advanced styles, section breaks, headers/footers, tracked changes, embedded objects, macros, and complex DOCX layout still require separate implementation and testing.

## Current limitations

- DOCX export may not preserve every Android rich-text span or paragraph style.
- HTML is the primary local rich-text save format for this editor.
- Device and cross-application acceptance tests remain pending.
