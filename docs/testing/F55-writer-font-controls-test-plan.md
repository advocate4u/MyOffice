# F55 acceptance tests: Writer font controls

| ID | Scenario | Expected result |
|---|---|---|
| F55-01 | Select text and choose 8 sp | Selected text becomes smaller |
| F55-02 | Choose 12, 16, 24 or 32 sp | Selected text changes to the chosen size |
| F55-03 | Open Font size with no selection | Prompt asks the user to select text |
| F55-04 | Apply Serif, Sans or Monospace to selected text | Selected text adopts the requested typeface span |
| F55-05 | Combine font family with bold, underline and color | Text remains editable and visible |
| F55-06 | Save formatted HTML and reopen it | Basic supported formatting is retained |
| F55-07 | Export as DOCX and inspect in a compatible office app | Verify what the DOCX writer preserves; advanced layout is not assumed |
| F55-08 | Select a range backwards | The selected range receives the font change |
| F55-09 | Use Hindi and English characters | Text stays editable |
| F55-10 | Use airplane mode | Formatting works locally |

## Compatibility scope

This is a basic rich-text implementation. Microsoft Word compatibility is not complete: advanced font embedding, themes, style inheritance, complex scripts, tables, tracked changes, macros, and embedded objects need separate work and interoperability testing.

## Current limitations

- Actual typeface appearance depends on fonts installed on Android and on the target office app.
- HTML is the primary local rich-text save format.
- Device acceptance tests remain pending.
