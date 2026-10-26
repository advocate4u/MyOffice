# F33 acceptance tests: Writer Undo/Redo

Run on Android with a disposable Writer document.

| ID | Scenario | Expected result |
|---|---|---|
| F33-01 | Type new text and tap Undo | Previous text snapshot is restored |
| F33-02 | Tap Redo after Undo | Undone text is restored |
| F33-03 | Tap Undo with empty history | Informational message appears; no crash |
| F33-04 | Tap Redo with empty redo history | Informational message appears; no crash |
| F33-05 | Undo then type new text | Redo history is cleared |
| F33-06 | Use Find & Replace then Undo | Previous text is restored |
| F33-07 | Undo/Redo with Hindi and emoji | Text remains intact |
| F33-08 | Change text then leave editor | Unsaved-change prompt still detects edits |
| F33-09 | Save after Undo/Redo and reopen | Saved version matches visible text |
| F33-10 | Make more than 50 edits | History remains bounded; latest 50 snapshots are available |
| F33-11 | Use airplane mode | Undo/Redo works offline |

## Current limitations

- History is session-only and resets when the editor closes.
- Undo/Redo stores full text snapshots and is capped at 50 entries to limit memory.
- Selection/cursor position is not restored; Undo/Redo moves the cursor to the end.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F33-01 through F33-11 on Android before marking device-verified.
