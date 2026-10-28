# F87 — Writer date/time insertion test plan

## Feature
The Writer formatting toolbar provides Insert date, Insert time, and Insert date & time actions. Values use the device's current locale and time zone.

## Manual checks
1. Open Writer and place the cursor in the middle of a sentence.
2. Tap Insert date; confirm a localized date is inserted at the cursor without replacing surrounding text.
3. Select a word and tap Insert time; confirm only the selected word is replaced by the localized time.
4. Tap Insert date & time at the end of a document and confirm both date and time are inserted.
5. Repeat with Hindi/India device locale and English locale.
6. Undo text edits and verify the inserted text participates in the existing undo history.
7. Save as HTML/TXT/DOCX/RTF where supported, reopen, and confirm inserted characters remain.
8. Confirm no network permission or network access is needed.

## Limitations
- The inserted value is static text, not a live field that updates later.
- Formatting and export fidelity remain limited to the format's existing support.
- Device locale and time settings determine the display format.

## CI verification
The Android debug build must compile successfully, package identity must remain `org.myoffice.app.debug`, and the workflow must upload a clearly labeled test-only APK. A debug APK must not be presented as a production update.


## F88 — Special-character picker checks

1. Open Writer and place the cursor inside a sentence.
2. Open **Special characters**, choose ©, and confirm it is inserted exactly at the cursor.
3. Select a word, choose ₹, and confirm the selected word is replaced while surrounding text remains intact.
4. Verify punctuation and typographic symbols such as em dash, ellipsis, curly quotes, degree, multiplication, and division display correctly.
5. Cancel the picker and confirm the document text is unchanged.
6. Save and reopen a supported format and confirm the inserted symbols remain intact.
7. Repeat with Hindi and English text in the same document.


## F89 — Signature block insertion checks

1. Open Writer, place the cursor at the intended signature location, and tap **Signature block**.
2. Confirm the block inserts a signature line plus Signature and Date labels at the cursor.
3. Select existing text first and confirm only the selection is replaced.
4. Confirm the cursor moves to the end of the inserted block.
5. Save and reopen a supported format; verify the lines remain in the document.
6. Verify undo removes the inserted block using the existing Writer undo control.


## F90 — Writer Find Next checks

1. Open a document with repeated text and place the cursor before the first occurrence.
2. Tap **Find in document**, enter a word, and choose **Find next**.
3. Confirm the next matching occurrence is selected and the cursor/selection moves to it.
4. Repeat Find Next to verify searching continues from the current selection and wraps to the beginning.
5. Search with different letter casing and confirm matching is case-insensitive.
6. Search for a missing term and confirm a clear “No matching text found” message appears.
7. Submit an empty query and confirm the document remains unchanged.


## F91 — Clear character formatting checks

1. Format a selected phrase with bold, italic, underline, font size, and a text color.
2. Select that phrase and tap **Clear character formatting**.
3. Confirm character-level spans are removed while the text itself remains unchanged.
4. Confirm paragraph alignment/heading styles are not intentionally reset by this character-formatting action.
5. With no selection, tap the action and confirm it asks the user to select text instead of changing content.
6. Verify the action does not affect text outside the selected range.
