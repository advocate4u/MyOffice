# F17 acceptance tests: Text slideshow

Run on Android with a saved or new text slide deck.

| ID | Scenario | Expected result |
|---|---|---|
| F17-01 | Open a deck and tap Start slideshow | Read-only slide view opens |
| F17-02 | Start slideshow with unsaved editor changes | Current editor text appears in the slide view |
| F17-03 | Tap Next | Next slide title/body appears |
| F17-04 | Tap Previous | Previous slide appears |
| F17-05 | On first slide, tap Previous | Slide stays on first slide |
| F17-06 | On last slide, tap Next | Slide stays on last slide |
| F17-07 | Exit slideshow | Returns to editor with current deck content retained |
| F17-08 | Use in airplane mode | Slideshow works offline |

## Current limitations

- Text-only: no animations, images, themes, presenter notes, external display or PPTX.
- Navigation is manual; no timed auto-play.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F17-01 through F17-08 on Android before marking device-verified.
