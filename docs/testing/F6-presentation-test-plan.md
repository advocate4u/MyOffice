# F6 acceptance tests: Offline text slide presentations

Run on the debug APK in airplane mode. This is a text-only presentation milestone, not PPTX support.

| ID | Scenario | Expected result |
|---|---|---|
| F6-01 | Tap Presentation from home | Slide deck list opens |
| F6-02 | Create a new deck | First slide editor opens |
| F6-03 | Enter Hindi/English title and body | Text is visible and preserved |
| F6-04 | Add a slide | New blank slide appears |
| F6-05 | Navigate Previous/Next | Current slide text is retained while navigating |
| F6-06 | Save deck | Deck appears in list with slide count |
| F6-07 | Reopen deck | Slides, titles and body text are restored |
| F6-08 | Share current slide text | Android share chooser opens with current slide content |
| F6-09 | Leave with unsaved changes | Confirmation offers keep editing, discard or save |
| F6-10 | Use in airplane mode | Create/edit/save/reopen works offline |
| F6-11 | Open unsupported/corrupt deck file | Clear error appears; app remains usable |
| F6-12 | Force-stop and relaunch | Saved deck remains available |

## Current limitations

- Text only: no themes, images, charts, transitions, animations, slideshow mode or speaker notes.
- No PPT/PPTX import/export or PowerPoint compatibility.
- Share sends the current slide as text; it does not generate a presentation file.

## Build verification

- GitHub Actions run completes successfully.
- Artifact `MyOffice-debug-apk` contains `app-debug.apk`.
- Install and run F6-01 through F6-12 on Android before marking the feature device-verified.
