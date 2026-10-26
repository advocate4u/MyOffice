# F73 acceptance tests: DOCX table text import

| ID | Scenario | Expected result |
|---|---|---|
| F73-01 | Open DOCX containing a simple one-row table | Cell text is separated by tabs |
| F73-02 | Open DOCX containing multiple table rows | Rows are separated by newlines and cells by tabs |
| F73-03 | Open table cells containing multiple paragraphs | Paragraphs within the same cell remain separated by line breaks |
| F73-04 | Open DOCX with text before and after a table | Surrounding paragraphs remain readable and in document order |
| F73-05 | Open DOCX with Hindi/English or XML special characters in cells | Text remains readable and escaped safely |
| F73-06 | Save imported content as HTML or DOCX | Cell text remains editable as tab/newline-separated text |
| F73-07 | Inspect original DOCX | Original source remains unchanged |
| F73-08 | Open malformed document XML | Import fails gracefully without crashing |

## Limitations

Tables are flattened into plain tab/newline-separated text. This does not preserve cell borders, column widths, merged cells, shading, nested-table layout, formulas or editable table objects. Device acceptance tests remain pending.
