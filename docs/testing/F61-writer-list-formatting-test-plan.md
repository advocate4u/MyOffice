# F61 acceptance tests: Writer bullet and numbered lists

| ID | Scenario | Expected result |
|---|---|---|
| F61-01 | Select a single line and tap Bullets | A bullet marker is added to that line |
| F61-02 | Select several lines and tap Bullets | Each non-empty selected line receives a bullet |
| F61-03 | Tap Bullets again on an all-bulleted selection | Bullet markers are removed |
| F61-04 | Select several lines and tap Numbered | Lines receive sequential numbering starting at 1 |
| F61-05 | Tap Numbered again on an all-numbered selection | Number prefixes are removed |
| F61-06 | Apply a list to a line with bold/color spans | Existing character formatting remains on the text |
| F61-07 | Save a plain-text list | Literal bullet/number markers persist in the TXT file |
| F61-08 | Save a formatted HTML list | List markers and character formatting remain readable after reopening |
| F61-09 | Use Hindi/English and punctuation in list lines | Text remains intact |
| F61-10 | Use list controls with no non-empty text selected | A helpful message appears; no crash |
| F61-11 | Use list controls offline | Formatting works without network access |

## Current limitations

- List markers are editable text prefixes rather than semantic Word/HTML list structures.
- Automatic renumbering after manual edits is not implemented.
- Device acceptance tests remain pending.
