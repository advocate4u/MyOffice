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
