# F76 acceptance tests: RTF formatting export

| ID | Scenario | Expected result |
|---|---|---|
| F76-01 | Select text and apply Bold, then export RTF | RTF output wraps text in a bold group |
| F76-02 | Apply Italic, Underline and Strikethrough | Corresponding RTF control words are emitted |
| F76-03 | Apply Bigger/Smaller | RTF font-size controls are emitted within safe bounds |
| F76-04 | Mix formatted and plain text | Style groups are separated and scope does not leak into later text |
| F76-05 | Include braces, backslashes and tabs | RTF escapes literal characters and emits tab controls |
| F76-06 | Include newlines | Paragraph control words are emitted |
| F76-07 | Include Hindi/English and emoji | Unicode RTF escapes are emitted |
| F76-08 | Open exported RTF in a compatible office app | Supported character formatting renders as expected |
| F76-09 | Cancel Save document picker | No destination file is written |
| F76-10 | Export plain text | Export remains valid RTF |

## Limitations

This feature exports a subset of character formatting only. Font colors, highlights, paragraph alignment, margins, lists, tables, images, headers/footers and other RTF features are not supported. Device and third-party office-app tests remain pending.
