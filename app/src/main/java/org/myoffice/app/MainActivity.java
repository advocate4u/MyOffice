package org.myoffice.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public final class MainActivity extends Activity {
    private static final int PICK_FILE = 4101;
    private static final String PREFS = "myoffice_recent";
    private static final String KEY = "uris";
    private static final int MAX_RECENTS = 12;
    private static final String NOTE_PREFIX = "MYOFFICE_NOTE_TITLE:";
    private LinearLayout page;
    private LinearLayout recentList;
    private SharedPreferences prefs;
    private final ArrayList<String> recentUris = new ArrayList<>();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        loadRecents();
        showHome();
    }

    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density + 0.5f); }

    private TextView label(String value, int size, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(0xFF202735);
        if (bold) t.setTypeface(null, android.graphics.Typeface.BOLD);
        t.setPadding(0, dp(5), 0, dp(5));
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(20), dp(20), dp(20), dp(24));
        l.setBackgroundColor(0xFFF5F7FB);
        return l;
    }

    private Button action(String title, View.OnClickListener click) {
        Button b = new Button(this);
        b.setText(title);
        b.setAllCaps(false);
        b.setOnClickListener(click);
        return b;
    }

    private void showHome() {
        page = column();
        page.addView(label("MyOffice", 29, true));
        TextView subtitle = label("Your files. Your learning. Offline.", 15, false);
        subtitle.setTextColor(0xFF536078);
        page.addView(subtitle);
        page.addView(gap(12));
        TextView intro = label("Open a document from your phone or a connected document provider. No account required.", 16, false);
        intro.setPadding(dp(14), dp(14), dp(14), dp(14));
        intro.setBackgroundColor(0xFFFFFFFF);
        page.addView(intro);
        page.addView(action("＋  Open a file", v -> openPicker()));
        page.addView(action("＋  Create a note (offline)", v -> showNoteEditor(null, "", "")));
        page.addView(action("My notes", v -> showNotes()));
        page.addView(action("Writer: text documents", v -> showWriterList()));
        page.addView(action("Refresh recent files", v -> { loadRecents(); showHome(); }));
        page.addView(gap(12));
        page.addView(label("Recent files", 21, true));
        page.addView(label("Stored on this device. Files themselves are not copied.", 12, false));
        recentList = new LinearLayout(this);
        recentList.setOrientation(LinearLayout.VERTICAL);
        page.addView(recentList);
        renderRecents();
        page.addView(gap(14));
        TextView foot = label("This first build previews text-based files. PDF rendering and DOCX/XLSX/PPTX editing are planned features, not included yet.", 12, false);
        foot.setTextColor(0xFF596477);
        page.addView(foot);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(page);
        setContentView(scroll);
    }

    private View gap(int height) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(height)));
        return v;
    }

    private void openPicker() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try { startActivityForResult(i, PICK_FILE); }
        catch (Exception e) { Toast.makeText(this, "Could not open the system file picker.", Toast.LENGTH_LONG).show(); }
    }

    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request != PICK_FILE || result != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        try {
            int flags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri, flags);
        } catch (SecurityException ignored) { }
        String key = uri.toString();
        recentUris.remove(key);
        recentUris.add(key);
        while (recentUris.size() > MAX_RECENTS) recentUris.remove(0);
        saveRecents();
        openSelected(uri);
    }

    private String nameOf(Uri uri) {
        String name = uri.getLastPathSegment() == null ? "Unnamed file" : uri.getLastPathSegment();
        Cursor c = null;
        try {
            c = getContentResolver().query(uri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                int index = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0 && !c.isNull(index)) name = c.getString(index);
            }
        } catch (Exception ignored) { }
        finally { if (c != null) c.close(); }
        return name;
    }

    private long sizeOf(Uri uri) {
        Cursor c = null;
        try {
            c = getContentResolver().query(uri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                int index = c.getColumnIndex(OpenableColumns.SIZE);
                if (index >= 0 && !c.isNull(index)) return c.getLong(index);
            }
        } catch (Exception ignored) { }
        finally { if (c != null) c.close(); }
        return -1;
    }

    private String sizeText(long bytes) {
        if (bytes < 0) return "Size unavailable";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format(Locale.getDefault(), "%.1f KB", bytes / 1024.0);
        return String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private void openSelected(Uri uri) {
        String name = nameOf(uri);
        String mime = getContentResolver().getType(uri);
        if (mime == null) mime = "Unknown file type";
        String lower = name.toLowerCase(Locale.ROOT);
        if (mime.equals("application/pdf") || lower.endsWith(".pdf")) {
            showPdf(uri, name);
            return;
        }
        boolean textFile = mime.startsWith("text/") || mime.equals("application/json")
                || lower.endsWith(".txt") || lower.endsWith(".md") || lower.endsWith(".csv")
                || lower.endsWith(".log") || lower.endsWith(".json") || lower.endsWith(".xml");
        if (textFile) {
            try { showText(uri, name, mime, readText(uri)); return; }
            catch (Exception e) { Toast.makeText(this, "Could not read this text file.", Toast.LENGTH_LONG).show(); }
        }
        showDetails(uri, name, mime);
    }

    private String readText(Uri uri) throws Exception {
        try (InputStream in = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (in == null) throw new IllegalStateException("File unavailable");
            byte[] buffer = new byte[8192];
            int total = 0, count;
            while ((count = in.read(buffer)) != -1 && total < 1024 * 1024) {
                int keep = Math.min(count, 1024 * 1024 - total);
                out.write(buffer, 0, keep);
                total += keep;
            }
            String value = out.toString("UTF-8");
            if (total >= 1024 * 1024) value += "\n\n[Preview limited to the first 1 MiB.]";
            return value;
        }
    }

    private void showText(Uri uri, String name, String mime, String bodyText) {
        LinearLayout content = column();
        TextView back = label("‹  Back to MyOffice", 16, true);
        back.setOnClickListener(v -> showHome());
        content.addView(back);
        content.addView(label(name, 22, true));
        content.addView(label(mime + "  •  " + sizeText(sizeOf(uri)), 12, false));
        TextView body = label(bodyText, 15, false);
        body.setGravity(Gravity.TOP | Gravity.START);
        body.setTextIsSelectable(true);
        body.setPadding(dp(12), dp(12), dp(12), dp(12));
        body.setBackgroundColor(0xFFFFFFFF);
        content.addView(body);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }

    private void showDetails(Uri uri, String name, String mime) {
        LinearLayout content = column();
        TextView back = label("‹  Back to MyOffice", 16, true);
        back.setOnClickListener(v -> showHome());
        content.addView(back);
        content.addView(gap(12));
        content.addView(label(name, 23, true));
        content.addView(label("Type: " + mime, 15, false));
        content.addView(label("Size: " + sizeText(sizeOf(uri)), 15, false));
        TextView message = label("Internal preview for this format is not available in this first build. You can open it in another installed app.", 15, false);
        message.setPadding(dp(14), dp(14), dp(14), dp(14));
        message.setBackgroundColor(0xFFFFFFFF);
        content.addView(message);
        content.addView(action("Open with another app", v -> {
            Intent view = new Intent(Intent.ACTION_VIEW);
            view.setDataAndType(uri, mime);
            view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try { startActivity(Intent.createChooser(view, "Open file with")); }
            catch (Exception e) { Toast.makeText(this, "No compatible app was found.", Toast.LENGTH_LONG).show(); }
        }));
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }


    // Notes are stored only in this app's private files directory; no account or network is needed.
    private File notesDirectory() {
        File dir = new File(getFilesDir(), "notes");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private File[] noteFiles() {
        File[] files = notesDirectory().listFiles((dir, name) -> name.endsWith(".note"));
        if (files == null) return new File[0];
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files;
    }

    private String[] readNote(File file) throws Exception {
        try (InputStream in = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
            String raw = out.toString("UTF-8");
            int newline = raw.indexOf('\n');
            String first = newline < 0 ? raw : raw.substring(0, newline);
            String title = first.startsWith(NOTE_PREFIX) ? first.substring(NOTE_PREFIX.length()) : file.getName();
            String body = newline < 0 ? "" : raw.substring(newline + 1);
            if (body.startsWith("\n")) body = body.substring(1);
            return new String[]{title, body};
        }
    }

    private void showNotes() {
        LinearLayout content = column();
        TextView back = label("‹  Back to MyOffice", 16, true);
        content.addView(back);
        back.setOnClickListener(v -> showHome());
        content.addView(label("My notes", 26, true));
        content.addView(label("Saved locally on this device. Notes remain available offline.", 14, false));
        content.addView(action("＋  Create a note", v -> showNoteEditor(null, "", "")));
        File[] files = noteFiles();
        if (files.length == 0) {
            content.addView(gap(12));
            content.addView(label("No notes yet. Create a note to start learning or writing.", 15, false));
        } else {
            for (File file : files) {
                String[] data;
                try { data = readNote(file); }
                catch (Exception e) { data = new String[]{file.getName(), "Could not read this note"}; }
                final File selected = file;
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(dp(12), dp(8), dp(12), dp(8));
                card.setBackgroundColor(0xFFFFFFFF);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
                params.setMargins(0, dp(5), 0, dp(5));
                content.addView(card, params);
                card.addView(label(data[0].isEmpty() ? "Untitled note" : data[0], 17, true));
                String preview = data[1].replace('\n', ' ').trim();
                if (preview.length() > 100) preview = preview.substring(0, 100) + "…";
                card.addView(label(preview.isEmpty() ? "Empty note" : preview, 13, false));
                card.addView(label("Modified " + DateFormat.getDateTimeInstance().format(new Date(file.lastModified())), 11, false));
                card.setOnClickListener(v -> {
                    try {
                        String[] note = readNote(selected);
                        showNoteEditor(selected, note[0], note[1]);
                    } catch (Exception e) {
                        Toast.makeText(this, "Could not open this note.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }

    private void showNoteEditor(File existing, String initialTitle, String initialBody) {
        LinearLayout content = column();
        TextView back = label("‹  Back to notes", 16, true);
        content.addView(back);
        content.addView(label("Offline note editor", 24, true));
        content.addView(label("Your writing stays on this device until you choose to share or export it.", 13, false));
        content.addView(label("Title", 14, true));
        EditText title = new EditText(this);
        title.setSingleLine(true);
        title.setHint("e.g. Science homework");
        title.setText(initialTitle);
        content.addView(title);
        content.addView(label("Note", 14, true));
        EditText body = new EditText(this);
        body.setGravity(Gravity.TOP | Gravity.START);
        body.setMinLines(12);
        body.setHint("Write here… Hindi and English text are supported.");
        body.setText(initialBody);
        body.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        content.addView(body, new LinearLayout.LayoutParams(-1, dp(280)));
        content.addView(action("Save note", v -> {
            String noteTitle = title.getText().toString().trim();
            if (noteTitle.isEmpty()) noteTitle = "Untitled note";
            File target = existing;
            if (target == null) target = new File(notesDirectory(), "note_" + System.currentTimeMillis() + ".note");
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
                writer.write(NOTE_PREFIX + noteTitle + "\n\n");
                writer.write(body.getText().toString());
                writer.flush();
                Toast.makeText(this, "Note saved on this device.", Toast.LENGTH_SHORT).show();
                showNotes();
            } catch (Exception e) {
                Toast.makeText(this, "Could not save note. Check available storage.", Toast.LENGTH_LONG).show();
            }
        }));
        content.addView(action("Discard changes", v -> new AlertDialog.Builder(this)
                .setTitle("Discard this edit?")
                .setMessage("Unsaved changes will be lost.")
                .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Discard", (dialog, which) -> showNotes())
                .show()));
        back.setOnClickListener(v -> {
            if (title.getText().toString().equals(initialTitle)
                    && body.getText().toString().equals(initialBody)) {
                showNotes();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Unsaved changes")
                        .setMessage("Save this note before leaving?")
                        .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                        .setNeutralButton("Discard", (dialog, which) -> showNotes())
                        .setPositiveButton("Save", (dialog, which) -> {
                            String noteTitle = title.getText().toString().trim();
                            if (noteTitle.isEmpty()) noteTitle = "Untitled note";
                            File target = existing;
                            if (target == null) target = new File(notesDirectory(), "note_" + System.currentTimeMillis() + ".note");
                            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
                                writer.write(NOTE_PREFIX + noteTitle + "\n\n");
                                writer.write(body.getText().toString());
                                writer.flush();
                                showNotes();
                            } catch (Exception e) {
                                Toast.makeText(this, "Could not save note.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }


    // Basic local PDF reader using Android's built-in PdfRenderer. It does not upload the document.
    private void showPdf(Uri uri, String name) {
        LinearLayout content = column();
        TextView back = label("‹  Back to MyOffice", 16, true);
        back.setOnClickListener(v -> showHome());
        content.addView(back);
        content.addView(label(name, 20, true));
        TextView pageLabel = label("Opening PDF…", 13, false);
        content.addView(pageLabel);
        ImageView pageImage = new ImageView(this);
        pageImage.setAdjustViewBounds(true);
        pageImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        pageImage.setBackgroundColor(0xFFFFFFFF);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(-1, dp(520));
        content.addView(pageImage, imageParams);
        int[] pageIndex = new int[]{0};
        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        Button previous = action("Previous page", v -> {
            if (pageIndex[0] > 0) {
                pageIndex[0]--;
                renderPdfPage(uri, pageIndex[0], pageImage, pageLabel, previousRef[0], nextRef[0], pageIndex);
            }
        });
        Button next = action("Next page", v -> {
            pageIndex[0]++;
            renderPdfPage(uri, pageIndex[0], pageImage, pageLabel, previousRef[0], nextRef[0], pageIndex);
        });
        previousRef[0] = previous;
        nextRef[0] = next;
        navigation.addView(previous, new LinearLayout.LayoutParams(0, -2, 1));
        navigation.addView(next, new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(navigation);
        content.addView(label("Local PDF preview. Editing, OCR and conversion are not included in this reader yet.", 12, false));
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
        renderPdfPage(uri, 0, pageImage, pageLabel, previous, next, pageIndex);
    }

    private final Button[] previousRef = new Button[1];
    private final Button[] nextRef = new Button[1];

    private void renderPdfPage(Uri uri, int requestedPage, ImageView image, TextView pageLabel,
                               Button previous, Button next, int[] pageIndex) {
        try (ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(uri, "r")) {
            if (descriptor == null) throw new IllegalStateException("PDF source is unavailable");
            try (PdfRenderer renderer = new PdfRenderer(descriptor)) {
                int pageCount = renderer.getPageCount();
                if (pageCount <= 0) throw new IllegalStateException("This PDF has no pages");
                int index = Math.max(0, Math.min(requestedPage, pageCount - 1));
                try (PdfRenderer.Page page = renderer.openPage(index)) {
                    float scale = Math.min(2.0f, Math.min(1400f / page.getWidth(), 1800f / page.getHeight()));
                    int width = Math.max(1, Math.round(page.getWidth() * scale));
                    int height = Math.max(1, Math.round(page.getHeight() * scale));
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    Object old = image.getTag();
                    if (old instanceof Bitmap && old != bitmap && !((Bitmap) old).isRecycled()) {
                        ((Bitmap) old).recycle();
                    }
                    image.setImageBitmap(bitmap);
                    image.setTag(bitmap);
                    pageIndex[0] = index;
                    pageLabel.setText("Page " + (index + 1) + " of " + pageCount);
                    previous.setEnabled(index > 0);
                    next.setEnabled(index < pageCount - 1);
                }
            }
        } catch (Exception e) {
            pageLabel.setText("Could not render this PDF. It may be encrypted, damaged, or unsupported.");
            previous.setEnabled(false);
            next.setEnabled(false);
            Toast.makeText(this, "PDF preview is unavailable for this file.", Toast.LENGTH_LONG).show();
        }
    }


    // First Writer milestone: reliable UTF-8 plain-text documents, fully local and usable offline.
    private File writerDirectory() {
        File dir = new File(getFilesDir(), "writer");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private File[] writerFiles() {
        File[] files = writerDirectory().listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) return new File[0];
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files;
    }

    private String readWriterFile(File file) throws Exception {
        try (InputStream in = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
            return out.toString("UTF-8");
        }
    }

    private void showWriterList() {
        LinearLayout content = column();
        TextView back = label("‹  Back to MyOffice", 16, true);
        back.setOnClickListener(v -> showHome());
        content.addView(back);
        content.addView(label("Writer", 27, true));
        content.addView(label("Create and edit plain-text documents locally. Rich formatting and DOCX are planned separately.", 13, false));
        content.addView(action("＋  New text document", v -> showWriterEditor(null, "")));
        File[] files = writerFiles();
        if (files.length == 0) {
            content.addView(gap(12));
            content.addView(label("No Writer documents yet.", 15, false));
        } else {
            for (File file : files) {
                String text;
                try { text = readWriterFile(file); }
                catch (Exception e) { text = ""; }
                String firstLine = text.split("\\R", 2)[0].trim();
                if (firstLine.isEmpty()) firstLine = "Untitled document";
                if (firstLine.length() > 80) firstLine = firstLine.substring(0, 80) + "…";
                String preview = text.replace('\n', ' ').trim();
                if (preview.length() > 140) preview = preview.substring(0, 140) + "…";
                final File selected = file;
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(dp(12), dp(8), dp(12), dp(8));
                card.setBackgroundColor(0xFFFFFFFF);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
                params.setMargins(0, dp(5), 0, dp(5));
                content.addView(card, params);
                card.addView(label(firstLine, 17, true));
                card.addView(label(preview.isEmpty() ? "Empty document" : preview, 13, false));
                card.addView(label("Modified " + DateFormat.getDateTimeInstance().format(new Date(file.lastModified())), 11, false));
                card.setOnClickListener(v -> {
                    try { showWriterEditor(selected, readWriterFile(selected)); }
                    catch (Exception e) { Toast.makeText(this, "Could not open this document.", Toast.LENGTH_LONG).show(); }
                });
            }
        }
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }

    private void saveWriterFile(File existing, String body) {
        File target = existing;
        if (target == null) target = new File(writerDirectory(), "document_" + System.currentTimeMillis() + ".txt");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
            writer.write(body);
            writer.flush();
            Toast.makeText(this, "Document saved on this device.", Toast.LENGTH_SHORT).show();
            showWriterList();
        } catch (Exception e) {
            Toast.makeText(this, "Could not save document. Check available storage.", Toast.LENGTH_LONG).show();
        }
    }

    private void showWriterEditor(File existing, String initialText) {
        LinearLayout content = column();
        TextView back = label("‹  Back to Writer", 16, true);
        content.addView(back);
        content.addView(label("Writer · Plain text", 24, true));
        content.addView(label("UTF-8 local document. Hindi and English are supported; changes are saved only when you tap Save.", 13, false));
        EditText body = new EditText(this);
        body.setGravity(Gravity.TOP | Gravity.START);
        body.setMinLines(16);
        body.setTextSize(16);
        body.setHint("Start writing…");
        body.setText(initialText);
        body.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        content.addView(body, new LinearLayout.LayoutParams(-1, dp(400)));
        content.addView(action("Save document", v -> saveWriterFile(existing, body.getText().toString())));
        content.addView(action("Share text", v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, body.getText().toString());
            try { startActivity(Intent.createChooser(share, "Share document text")); }
            catch (Exception e) { Toast.makeText(this, "No compatible sharing app was found.", Toast.LENGTH_LONG).show(); }
        }));
        content.addView(action("Discard changes", v -> new AlertDialog.Builder(this)
                .setTitle("Discard this edit?")
                .setMessage("Unsaved changes will be lost.")
                .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Discard", (dialog, which) -> showWriterList())
                .show()));
        back.setOnClickListener(v -> {
            if (body.getText().toString().equals(initialText)) {
                showWriterList();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Unsaved changes")
                        .setMessage("Save this document before leaving?")
                        .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                        .setNeutralButton("Discard", (dialog, which) -> showWriterList())
                        .setPositiveButton("Save", (dialog, which) -> saveWriterFile(existing, body.getText().toString()))
                        .show();
            }
        });
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }

    private void renderRecents() {
        if (recentList == null) return;
        recentList.removeAllViews();
        if (recentUris.isEmpty()) {
            recentList.addView(label("No recent files yet. Tap Open a file to get started.", 14, false));
            return;
        }
        for (int i = recentUris.size() - 1; i >= 0; i--) {
            final String value = recentUris.get(i);
            Uri uri = Uri.parse(value);
            String name = nameOf(uri);
            String mime = getContentResolver().getType(uri);
            if (mime == null) mime = "Unknown file type";
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(12), dp(8), dp(12), dp(8));
            card.setBackgroundColor(0xFFFFFFFF);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.setMargins(0, dp(4), 0, dp(4));
            recentList.addView(card, params);
            card.addView(label(name, 16, true));
            card.addView(label(mime + "  •  " + sizeText(sizeOf(uri)), 12, false));
            card.addView(label("Tap to open", 11, false));
            card.setOnClickListener(v -> openSelected(Uri.parse(value)));
        }
    }

    private void loadRecents() {
        recentUris.clear();
        String stored = prefs.getString(KEY, "");
        if (stored.isEmpty()) return;
        for (String line : stored.split("\n")) {
            if (line.isEmpty()) continue;
            try {
                Uri uri = Uri.parse(line);
                // Querying metadata also validates that the persisted permission still works.
                nameOf(uri);
                recentUris.add(line);
            } catch (Exception ignored) { }
        }
        while (recentUris.size() > MAX_RECENTS) recentUris.remove(0);
    }

    private void saveRecents() {
        StringBuilder b = new StringBuilder();
        for (String uri : recentUris) b.append(uri).append("\n");
        prefs.edit().putString(KEY, b.toString()).apply();
    }
}
