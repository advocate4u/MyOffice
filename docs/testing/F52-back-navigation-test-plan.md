# F52 acceptance tests: Back navigation

| ID | Scenario | Expected result |
|---|---|---|
| F52-01 | Open Home → Notes → a note editor, then press Android Back | Returns to the previous screen |
| F52-02 | Navigate through multiple screens and press Back repeatedly | Each press returns to the most recently visited screen |
| F52-03 | Press Back in the CSV editor with unsaved changes | Existing save/discard/keep-editing protection appears |
| F52-04 | Press Back in the presentation editor with unsaved changes | Existing save/discard/keep-editing protection appears |
| F52-05 | Press Back during slideshow | Returns to the presentation editor |
| F52-06 | Reach Home with no previous screen and press Back once | “Press Back again to close MyOffice” appears |
| F52-07 | Press Back again within two seconds | MyOffice closes |
| F52-08 | Wait more than two seconds after first Back, then press again | First press prompts again instead of closing |
| F52-09 | Use in-screen Back controls | Navigation remains usable and does not crash |
| F52-10 | Use navigation offline | Back navigation does not require network access |

## Limitations

Test the route stack and unsaved-change handling on a real Android device before marking this feature verified.
