# F26 acceptance tests: PDF zoom controls

Run on Android with a readable multi-page local PDF.

| ID | Scenario | Expected result |
|---|---|---|
| F26-01 | Open a PDF | First page renders at 100% |
| F26-02 | Tap + | Render scale increases by 25%, up to 200% |
| F26-03 | Tap − | Render scale decreases by 25%, down to 75% |
| F26-04 | Tap 100% | Page returns to default scale |
| F26-05 | Zoom above fit width | Horizontal scroll can reach page content |
| F26-06 | Scroll vertically on zoomed page | Page viewport remains navigable |
| F26-07 | Change page while zoomed | Page navigation works and keeps selected zoom |
| F26-08 | Reach maximum/minimum zoom | Further taps do not exceed bounds |
| F26-09 | Open a malformed or encrypted PDF | Recoverable error appears; no crash |
| F26-10 | Use airplane mode | Local PDF reading and zoom work offline |

## Current limitations

- Zoom uses discrete buttons rather than pinch gestures.
- Large PDFs may take longer to re-render at high zoom; rendering is capped to avoid unbounded bitmap allocation.
- Search, annotations and text editing are not part of this feature.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F26-01 through F26-10 on Android before marking device-verified.
