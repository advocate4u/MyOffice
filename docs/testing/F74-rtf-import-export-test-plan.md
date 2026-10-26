# F74 acceptance tests: RTF import and export

| ID | Scenario | Expected result |
|---|---|---|
| F74-01 | Open basic RTF text | Visible text imports into a new Writer copy |
| F74-02 | Open RTF with paragraph breaks and tabs | Newlines and tabs remain readable |
| F74-03 | Open RTF with escaped braces and backslashes | Literal characters are restored |
| F74-04 | Open RTF with `\\uN?` Unicode escapes | Unicode characters are decoded |
| F74-05 | Open RTF with font/color/style metadata groups | Metadata groups do not appear as document text |
| F74-06 | Open RTF with picture/object destinations | Unsupported binary objects are skipped safely |
| F74-07 | Open RTF larger than 10 MiB | Import is rejected with a clear message |
| F74-08 | Export Writer text to RTF | Save picker suggests an .rtf filename and output has an RTF header |
| F74-09 | Export Hindi/English and punctuation | Text is encoded using RTF Unicode escapes and remains readable in a compatible app |
| F74-10 | Cancel export picker | No output file is written |
| F74-11 | Open exported RTF in a compatible office app | Plain text and paragraphs render as expected |
| F74-12 | Compare original RTF after import | Original source remains unchanged |

## Limitations

RTF formatting, images, tables, page layout and embedded objects are not preserved. This milestone imports text and exports basic text only. Device and third-party office-app tests remain pending.
