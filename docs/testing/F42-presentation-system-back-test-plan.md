# F42 acceptance tests: Presentation system Back protection

Run on Android with a disposable local slide deck.

| ID | Scenario | Expected result |
|---|---|---|
| F42-01 | Open a saved deck and press system Back without edits | Return to Presentation list |
| F42-02 | Edit slide title/body and press system Back | Unsaved-changes dialog appears |
| F42-03 | Choose Keep editing | Editor remains open with changes preserved |
| F42-04 | Choose Discard | Return to list without saving edits |
| F42-05 | Choose Save | Deck saves and returns to list |
| F42-06 | Start slideshow and press system Back | Return to slide editor |
| F42-07 | Exit slideshow using on-screen control | Return to slide editor |
| F42-08 | Use in-editor Back | Same unsaved-change prompt appears |
| F42-09 | Use airplane mode | Navigation protection works offline |

## Current limitations

- Device tests should cover gesture navigation and three-button navigation.
- This only protects the current editor session; it does not provide autosave.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F42-01 through F42-09 on Android before marking device-verified.
