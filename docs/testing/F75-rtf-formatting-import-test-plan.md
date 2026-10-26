# F75 acceptance tests: RTF character formatting import

| ID | Scenario | Expected result |
|---|---|---|
| F75-01 | Open RTF with bold runs | Imported range receives a bold span |
| F75-02 | Open RTF with italic, underline and strike | Corresponding spans are applied |
| F75-03 | Open RTF with font-size controls | Relative size spans are applied within safe bounds |
| F75-04 | Open nested groups with different styles | Style state is restored when each group closes |
| F75-05 | Open RTF with Unicode escapes and fallback characters | Unicode character is imported and fallback bytes are skipped |
| F75-06 | Open RTF containing font/color metadata groups | Metadata text is not shown as body content |
| F75-07 | Open RTF with unsupported color/table/picture formatting | Text remains readable; unsupported formatting is omitted |
| F75-08 | Save imported content as HTML | Supported imported character spans persist in HTML |
| F75-09 | Export imported content as DOCX | Supported spans are represented in the DOCX export subset |
| F75-10 | Open malformed RTF | Import fails gracefully without crashing |

## Limitations

This is a subset of RTF. Color tables, paragraph alignment, lists, page layout, tables, images, embedded objects and style-preserving RTF export are not supported. Device and third-party office-app tests remain pending.

| F75-07 | Import RTF with `\\super`, `\\sub`, `\\nosupersub` | Superscript/subscript spans are applied and reset correctly |
