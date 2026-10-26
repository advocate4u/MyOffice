# F32 acceptance tests: Writer word and character count

Run on Android in a disposable Writer document.

| ID | Scenario | Expected result |
|---|---|---|
| F32-01 | Open an empty Writer document | Shows 0 words and 0 characters |
| F32-02 | Type a simple sentence | Word and character counts update while typing |
| F32-03 | Add punctuation and numbers | Character count includes punctuation and digits |
| F32-04 | Enter multiple spaces and newlines | Word count uses whitespace-separated tokens |
| F32-05 | Enter Hindi and emoji | Character count reflects Java UTF-16 string length; no crash |
| F32-06 | Use Find & Replace | Counts update after replacement |
| F32-07 | Save and reopen document | Text is preserved; count recalculates from content |
| F32-08 | Use airplane mode | Statistics update offline |

## Current limitations

- Word count is whitespace-based, not a linguistic tokenizer.
- Character count is Java string length (UTF-16 code units), not grapheme clusters or bytes.

## Build verification

- GitHub Actions run completes successfully.
- Artifact contains the debug APK.
- Execute F32-01 through F32-08 on Android before marking device-verified.
