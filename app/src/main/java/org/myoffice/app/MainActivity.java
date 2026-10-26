package org.myoffice.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.util.Base64;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
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
import java.util.List;

public final class MainActivity extends Activity {
    private static final int PICK_FILE = 4101;
    private static final int PICK_IMAGES = 4102;
    private static final int SAVE_PDF = 4103;
    private static final int SAVE_TEXT = 4104;
    private static final int SAVE_CSV = 4105;
    private String pendingCsvExport;
    private String pendingTextExport;
    private File pendingPdfFile;
    private static final String PREFS = "myoffice_recent";
    private static final String KEY = "uris";
    private static final int MAX_RECENTS = 12;
    private static final String NOTE_PREFIX = "MYOFFICE_NOTE_TITLE:";
    private LinearLayout page;
    private LinearLayout recentList;
    private EditText recentSearchInput;
    private SharedPreferences prefs;
    private final ArrayList<String> recentUris = new ArrayList<>();
    private ArrayList<ArrayList<EditText>> sheetCells = new ArrayList<>();
    private File currentCsvFile;
    private String sheetOriginalSnapshot = "";
    private static final class Slide {
        String title;
        String body;
        Slide(String title, String body) { this.title = title; this.body = body; }
    }
    private ArrayList<Slide> deckSlides = new ArrayList<>();
    private int deckIndex = 0;
    private File currentDeckFile;
    private EditText deckTitleInput;
    private EditText deckBodyInput;
    private String deckOriginalSnapshot = "";

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
        page.addView(action("Spreadsheet: CSV sheets", v -> showSpreadsheetList()));
        page.addView(action("Presentation: slide decks", v -> showPresentationList()));
        page.addView(action("Images to PDF (offline)", v -> openImagePicker()));
        page.addView(action("Refresh recent files", v -> { loadRecents(); showHome(); }));
        page.addView(gap(12));
        page.addView(label("Recent files", 21, true));
        page.addView(label("Stored on this device. Files themselves are not copied.", 12, false));
        recentSearchInput = new EditText(this);
        recentSearchInput.setSingleLine(true);
        recentSearchInput.setHint("Search recent files by name");
        recentSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderRecents(); }
            @Override public void afterTextChanged(Editable s) { }
        });
        page.addView(recentSearchInput);
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
        if (request == PICK_IMAGES) {
            if (result != RESULT_OK || data == null) return;
            ArrayList<Uri> images = new ArrayList<>();
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri item = data.getClipData().getItemAt(i).getUri();
                    if (item != null) images.add(item);
                }
            } else if (data.getData() != null) images.add(data.getData());
            if (images.isEmpty()) return;
            if (images.size() > 20) {
                Toast.makeText(this, "Choose up to 20 images at a time.", Toast.LENGTH_LONG).show();
                return;
            }
            createPdfFromImagesAsync(images);
            return;
        }
        if (request == SAVE_CSV) {
            String export = pendingCsvExport;
            pendingCsvExport = null;
            if (result != RESULT_OK || data == null || data.getData() == null || export == null) return;
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    getContentResolver().openOutputStream(data.getData(), "wt"), "UTF-8")) {
                writer.write(export);
                writer.flush();
                Toast.makeText(this, "CSV exported.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Could not export CSV.", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (request == SAVE_TEXT) {
            String export = pendingTextExport;
            pendingTextExport = null;
            if (result != RESULT_OK || data == null || data.getData() == null || export == null) return;
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    getContentResolver().openOutputStream(data.getData(), "wt"), "UTF-8")) {
                writer.write(export);
                writer.flush();
                Toast.makeText(this, "Text file exported.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Could not export text file.", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (request == SAVE_PDF) {
            if (result != RESULT_OK || data == null || data.getData() == null) {
                if (pendingPdfFile != null) pendingPdfFile.delete();
                pendingPdfFile = null;
                return;
            }
            Uri destination = data.getData();
            try {
                if (pendingPdfFile == null || !pendingPdfFile.isFile()) throw new IllegalStateException("Temporary PDF is missing");
                try (InputStream in = new FileInputStream(pendingPdfFile);
                     java.io.OutputStream out = getContentResolver().openOutputStream(destination, "wt")) {
                    if (out == null) throw new IllegalStateException("Destination cannot be written");
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
                    out.flush();
                }
                pendingPdfFile.delete();
                pendingPdfFile = null;
                Toast.makeText(this, "PDF saved.", Toast.LENGTH_SHORT).show();
                openSelected(destination);
            } catch (Exception e) {
                Toast.makeText(this, "Could not save PDF. Choose a writable location.", Toast.LENGTH_LONG).show();
            }
            return;
        }
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



    private void startCsvExport(String suggestedName, String content) {
        pendingCsvExport = content;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, suggestedName.endsWith(".csv") ? suggestedName : suggestedName + ".csv");
        try { startActivityForResult(intent, SAVE_CSV); }
        catch (Exception e) {
            pendingCsvExport = null;
            Toast.makeText(this, "Could not open the Save CSV picker.", Toast.LENGTH_LONG).show();
        }
    }

    private String safeTextFilename(String value) {
        StringBuilder safe = new StringBuilder();
        for (char ch : value.toCharArray()) {
            if (ch == '/' || ch == '\\' || ch == ':' || ch == '*' || ch == '?' || ch == '"' || ch == '<' || ch == '>' || ch == '|') {
                safe.append('_');
            } else {
                safe.append(ch);
            }
        }
        String result = safe.toString().trim();
        return result.isEmpty() ? "MyOffice_Note" : result;
    }

    private void startTextExport(String suggestedName, String content) {
        pendingTextExport = content;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, suggestedName.endsWith(".txt") ? suggestedName : suggestedName + ".txt");
        try { startActivityForResult(intent, SAVE_TEXT); }
        catch (Exception e) {
            pendingTextExport = null;
            Toast.makeText(this, "Could not open the Save text picker.", Toast.LENGTH_LONG).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try { startActivityForResult(intent, PICK_IMAGES); }
        catch (Exception e) { Toast.makeText(this, "Could not open the image picker.", Toast.LENGTH_LONG).show(); }
    }

    private void createPdfFromImagesAsync(ArrayList<Uri> images) {
        Toast.makeText(this, "Preparing PDF on this device…", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            File temp = null;
            try {
                temp = File.createTempFile("myoffice_images_", ".pdf", getCacheDir());
                PdfDocument document = new PdfDocument();
                try {
                    for (int i = 0; i < images.size(); i++) {
                        Bitmap bitmap = decodeImage(images.get(i));
                        if (bitmap == null) throw new IllegalArgumentException("An image could not be decoded");
                        int pageWidth = bitmap.getWidth() > bitmap.getHeight() ? 842 : 595;
                        int pageHeight = bitmap.getWidth() > bitmap.getHeight() ? 595 : 842;
                        PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i + 1).create();
                        PdfDocument.Page page = document.startPage(info);
                        float scale = Math.min((pageWidth - 48f) / bitmap.getWidth(), (pageHeight - 48f) / bitmap.getHeight());
                        float drawWidth = bitmap.getWidth() * scale;
                        float drawHeight = bitmap.getHeight() * scale;
                        float left = (pageWidth - drawWidth) / 2f;
                        float top = (pageHeight - drawHeight) / 2f;
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                        page.getCanvas().drawBitmap(bitmap, null, new RectF(left, top, left + drawWidth, top + drawHeight), paint);
                        document.finishPage(page);
                        bitmap.recycle();
                    }
                    try (FileOutputStream out = new FileOutputStream(temp)) { document.writeTo(out); }
                } finally {
                    document.close();
                }
                File ready = temp;
                runOnUiThread(() -> showSavePdfPicker(ready));
            } catch (Exception e) {
                if (temp != null) temp.delete();
                runOnUiThread(() -> Toast.makeText(this, "Could not convert these images to PDF.", Toast.LENGTH_LONG).show());
            }
        }, "myoffice-image-to-pdf").start();
    }

    private Bitmap decodeImage(Uri uri) throws Exception {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            if (in == null) throw new IllegalStateException("Image unavailable");
            BitmapFactory.decodeStream(in, null, bounds);
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw new IllegalArgumentException("Invalid image");
        int sample = 1;
        while (Math.max(bounds.outWidth / sample, bounds.outHeight / sample) > 1800) sample *= 2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sample;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            if (in == null) throw new IllegalStateException("Image unavailable");
            return BitmapFactory.decodeStream(in, null, options);
        }
    }

    private void showSavePdfPicker(File tempPdf) {
        pendingPdfFile = tempPdf;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "MyOffice_Images.pdf");
        try { startActivityForResult(intent, SAVE_PDF); }
        catch (Exception e) {
            tempPdf.delete();
            pendingPdfFile = null;
            Toast.makeText(this, "Could not open the Save PDF picker.", Toast.LENGTH_LONG).show();
        }
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
        if (mime.equals("text/csv") || lower.endsWith(".csv")) {
            try {
                if (sizeOf(uri) > 1024 * 1024) throw new IllegalArgumentException("CSV is larger than the 1 MiB import limit");
                String csv = readText(uri);
                if (csv.endsWith("[Preview limited to the first 1 MiB.]")) {
                    throw new IllegalArgumentException("CSV is larger than the 1 MiB import limit");
                }
                Toast.makeText(this, "Imported as a new local sheet; original file is unchanged.", Toast.LENGTH_LONG).show();
                showSpreadsheetEditor(null, parseCsv(csv), true);
                return;
            } catch (Exception e) {
                Toast.makeText(this, "Could not import this CSV. Files over 1 MiB are not supported yet.", Toast.LENGTH_LONG).show();
                showDetails(uri, name, mime);
                return;
            }
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
        content.addView(action("Edit a copy in Writer", v -> {
            if (bodyText.endsWith("[Preview limited to the first 1 MiB.]")) {
                Toast.makeText(this, "This file is too large to import into Writer yet.", Toast.LENGTH_LONG).show();
            } else {
                showWriterEditor(null, bodyText);
            }
        }));
        EditText findInput = new EditText(this);
        findInput.setSingleLine(true);
        findInput.setHint("Find text in this file");
        content.addView(findInput);
        TextView body = label(bodyText, 15, false);
        body.setGravity(Gravity.TOP | Gravity.START);
        body.setTextIsSelectable(true);
        body.setPadding(dp(12), dp(12), dp(12), dp(12));
        body.setBackgroundColor(0xFFFFFFFF);
        content.addView(action("Find in text", v -> {
            String query = findInput.getText().toString();
            if (query.trim().isEmpty()) {
                body.setText(bodyText);
                Toast.makeText(this, "Enter text to find.", Toast.LENGTH_SHORT).show();
                return;
            }
            SpannableString highlighted = new SpannableString(bodyText);
            int matches = 0;
            for (int index = 0; index <= bodyText.length() - query.length(); index++) {
                if (bodyText.regionMatches(true, index, query, 0, query.length())) {
                    highlighted.setSpan(new BackgroundColorSpan(0xFFFFE082), index, index + query.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    highlighted.setSpan(new ForegroundColorSpan(0xFF202020), index, index + query.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    matches++;
                    index += Math.max(0, query.length() - 1);
                }
            }
            body.setText(highlighted);
            Toast.makeText(this, matches == 0 ? "No matches found." : matches + " match(es) highlighted.", Toast.LENGTH_SHORT).show();
        }));
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
                card.addView(action("Delete note", v -> confirmDeleteFile(selected, "note", () -> showNotes())));
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
        content.addView(action("Export as .txt", v -> {
            String noteTitle = title.getText().toString().trim();
            if (noteTitle.isEmpty()) noteTitle = "Untitled note";
            String filename = safeTextFilename(noteTitle) + ".txt";
            startTextExport(filename, noteTitle + "\\n\\n" + body.getText().toString());
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
        content.addView(action("Share PDF", v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setClipData(ClipData.newUri(getContentResolver(), name, uri));
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try { startActivity(Intent.createChooser(share, "Share PDF")); }
            catch (Exception e) { Toast.makeText(this, "No compatible sharing app was found.", Toast.LENGTH_LONG).show(); }
        }));
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
                card.addView(action("Delete document", v -> confirmDeleteFile(selected, "document", () -> showWriterList())));
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
        content.addView(action("Export as .txt", v -> {
            String filename = existing == null ? "MyOffice_Document.txt" : existing.getName();
            startTextExport(filename, body.getText().toString());
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


    // First Spreadsheet milestone: editable CSV grid, stored locally as UTF-8 CSV.
    private File spreadsheetDirectory() {
        File dir = new File(getFilesDir(), "spreadsheets");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private File[] csvFiles() {
        File[] files = spreadsheetDirectory().listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null) return new File[0];
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files;
    }

    private String readCsvFile(File file) throws Exception {
        try (InputStream in = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
            return out.toString("UTF-8");
        }
    }

    private ArrayList<ArrayList<String>> parseCsv(String text) {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < text.length() && text.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else if (quoted) {
                    quoted = false;
                } else if (field.length() == 0) {
                    quoted = true;
                } else {
                    field.append(ch);
                }
            } else if (ch == ',' && !quoted) {
                row.add(field.toString());
                field.setLength(0);
            } else if ((ch == '\n' || ch == '\r') && !quoted) {
                if (ch == '\r' && i + 1 < text.length() && text.charAt(i + 1) == '\n') i++;
                row.add(field.toString());
                field.setLength(0);
                rows.add(row);
                row = new ArrayList<>();
            } else {
                field.append(ch);
            }
        }
        if (field.length() > 0 || !row.isEmpty() || rows.isEmpty()) {
            row.add(field.toString());
            rows.add(row);
        }
        int maxColumns = 1;
        for (ArrayList<String> r : rows) maxColumns = Math.max(maxColumns, r.size());
        for (ArrayList<String> r : rows) while (r.size() < maxColumns) r.add("");
        return rows;
    }

    private String csvEscape(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void showSpreadsheetList() {
        LinearLayout content = column();
        TextView back = label("‹  Back to MyOffice", 16, true);
        back.setOnClickListener(v -> showHome());
        content.addView(back);
        content.addView(label("Spreadsheet · CSV", 26, true));
        content.addView(label("Edit rows and columns offline. CSV is supported; formulas and XLSX are not included yet.", 13, false));
        content.addView(action("＋  New CSV sheet", v -> {
            ArrayList<ArrayList<String>> blank = new ArrayList<>();
            for (int r = 0; r < 4; r++) {
                ArrayList<String> row = new ArrayList<>();
                for (int col = 0; col < 4; col++) row.add("");
                blank.add(row);
            }
            showSpreadsheetEditor(null, blank, true);
        }));
        File[] files = csvFiles();
        if (files.length == 0) {
            content.addView(gap(12));
            content.addView(label("No CSV sheets yet.", 15, false));
        } else {
            for (File file : files) {
                String preview = "";
                try { preview = readCsvFile(file).split("\\R", 2)[0]; } catch (Exception ignored) { }
                final File selected = file;
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(dp(12), dp(8), dp(12), dp(8));
                card.setBackgroundColor(0xFFFFFFFF);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
                params.setMargins(0, dp(5), 0, dp(5));
                content.addView(card, params);
                card.addView(label(file.getName(), 17, true));
                card.addView(label(preview.isEmpty() ? "Empty sheet" : preview, 13, false));
                card.addView(label("Modified " + DateFormat.getDateTimeInstance().format(new Date(file.lastModified())), 11, false));
                card.addView(action("Delete sheet", v -> confirmDeleteFile(selected, "sheet", () -> showSpreadsheetList())));
                card.setOnClickListener(v -> {
                    try { showSpreadsheetEditor(selected, parseCsv(readCsvFile(selected)), true); }
                    catch (Exception e) { Toast.makeText(this, "Could not open this CSV file.", Toast.LENGTH_LONG).show(); }
                });
            }
        }
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }

    private ArrayList<ArrayList<String>> readSheetCells() {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        for (ArrayList<EditText> row : sheetCells) {
            ArrayList<String> values = new ArrayList<>();
            for (EditText cell : row) values.add(cell.getText().toString());
            data.add(values);
        }
        return data;
    }

    private String serializeSheet(ArrayList<ArrayList<String>> data) {
        StringBuilder out = new StringBuilder();
        for (int r = 0; r < data.size(); r++) {
            ArrayList<String> row = data.get(r);
            for (int col = 0; col < row.size(); col++) {
                if (col > 0) out.append(',');
                out.append(csvEscape(row.get(col)));
            }
            if (r < data.size() - 1) out.append("\r\n");
        }
        return out.toString();
    }

    private void saveSpreadsheet() {
        String csv = serializeSheet(readSheetCells());
        File target = currentCsvFile;
        if (target == null) target = new File(spreadsheetDirectory(), "sheet_" + System.currentTimeMillis() + ".csv");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
            writer.write(csv);
            writer.flush();
            Toast.makeText(this, "CSV sheet saved on this device.", Toast.LENGTH_SHORT).show();
            showSpreadsheetList();
        } catch (Exception e) {
            Toast.makeText(this, "Could not save CSV sheet. Check available storage.", Toast.LENGTH_LONG).show();
        }
    }


    private int[] parseCellReference(String reference) {
        String value = reference.trim().toUpperCase(Locale.ROOT);
        int index = 0;
        int column = 0;
        while (index < value.length() && value.charAt(index) >= 'A' && value.charAt(index) <= 'Z') {
            column = column * 26 + (value.charAt(index) - 'A' + 1);
            index++;
        }
        if (index == 0 || index == value.length()) return null;
        int row;
        try { row = Integer.parseInt(value.substring(index)); }
        catch (NumberFormatException e) { return null; }
        if (row < 1 || column < 1) return null;
        return new int[]{row - 1, column - 1};
    }

    private String cellName(int row, int column) {
        StringBuilder letters = new StringBuilder();
        int value = column + 1;
        while (value > 0) {
            int remainder = (value - 1) % 26;
            letters.insert(0, (char) ('A' + remainder));
            value = (value - 1) / 26;
        }
        return letters.toString() + (row + 1);
    }

    private String evaluateAggregateFormula(String formula, ArrayList<ArrayList<String>> data) {
        String value = formula.trim();
        int open = value.indexOf('(');
        int close = value.lastIndexOf(')');
        if (!value.startsWith("=") || open < 2 || close != value.length() - 1) return null;
        String function = value.substring(1, open).trim().toUpperCase(Locale.ROOT);
        if (!function.equals("SUM") && !function.equals("AVERAGE")
                && !function.equals("MIN") && !function.equals("MAX") && !function.equals("COUNT")) return null;
        String[] range = value.substring(open + 1, close).split(":", -1);
        if (range.length != 2) return "ERROR: use a cell range such as A1:A4";
        int[] start = parseCellReference(range[0]);
        int[] finish = parseCellReference(range[1]);
        if (start == null || finish == null) return "ERROR: invalid cell range";
        int firstRow = Math.min(start[0], finish[0]);
        int lastRow = Math.max(start[0], finish[0]);
        int firstCol = Math.min(start[1], finish[1]);
        int lastCol = Math.max(start[1], finish[1]);
        double sum = 0;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        int count = 0;
        for (int r = firstRow; r <= lastRow && r < data.size(); r++) {
            for (int col = firstCol; col <= lastCol && col < data.get(r).size(); col++) {
                String cell = data.get(r).get(col).trim();
                if (cell.isEmpty()) continue;
                try {
                    double number = Double.parseDouble(cell);
                    sum += number;
                    min = Math.min(min, number);
                    max = Math.max(max, number);
                    count++;
                } catch (NumberFormatException ignored) { }
            }
        }
        if (function.equals("COUNT")) return String.valueOf(count);
        if (count == 0) return "ERROR: range has no numeric cells";
        double result;
        if (function.equals("SUM")) result = sum;
        else if (function.equals("AVERAGE")) result = sum / count;
        else if (function.equals("MIN")) result = min;
        else result = max;
        if (result == Math.rint(result) && Math.abs(result) < 9e15) return String.valueOf((long) result);
        return String.format(Locale.ROOT, "%.6f", result).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private void showFormulaPreview() {
        ArrayList<ArrayList<String>> data = readSheetCells();
        StringBuilder report = new StringBuilder();
        int formulas = 0;
        for (int r = 0; r < data.size(); r++) {
            for (int col = 0; col < data.get(r).size(); col++) {
                String value = data.get(r).get(col).trim();
                if (!value.startsWith("=")) continue;
                formulas++;
                String result = evaluateAggregateFormula(value, data);
                if (report.length() > 0) report.append("\n");
                report.append(cellName(r, col)).append("  ").append(value).append("  →  ")
                        .append(result == null ? "Unsupported formula" : result);
            }
        }
        if (formulas == 0) report.append("No formula cells found. Try =SUM(A1:A4), =AVERAGE(A1:A4), =MIN(A1:A4), =MAX(A1:A4), or =COUNT(A1:A4).");
        new AlertDialog.Builder(this)
                .setTitle("Formula preview · read-only")
                .setMessage(report.toString())
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showSpreadsheetEditor(File existing, ArrayList<ArrayList<String>> data, boolean resetOriginal) {
        currentCsvFile = existing;
        LinearLayout content = column();
        TextView back = label("‹  Back to Spreadsheet", 16, true);
        content.addView(back);
        content.addView(label("CSV sheet editor", 23, true));
        content.addView(label("Tap a cell to edit it. Add rows/columns as needed, then save. No formulas yet.", 13, false));
        LinearLayout tools = new LinearLayout(this);
        tools.setOrientation(LinearLayout.HORIZONTAL);
        tools.addView(action("+ Row", v -> {
            ArrayList<ArrayList<String>> current = readSheetCells();
            int columns = current.isEmpty() ? 1 : current.get(0).size();
            ArrayList<String> row = new ArrayList<>();
            for (int i = 0; i < columns; i++) row.add("");
            current.add(row);
            showSpreadsheetEditor(currentCsvFile, current, false);
        }), new LinearLayout.LayoutParams(0, -2, 1));
        tools.addView(action("+ Column", v -> {
            ArrayList<ArrayList<String>> current = readSheetCells();
            for (ArrayList<String> row : current) row.add("");
            showSpreadsheetEditor(currentCsvFile, current, false);
        }), new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(tools);
        content.addView(action("Preview formula results", v -> showFormulaPreview()));
        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);
        sheetCells = new ArrayList<>();
        for (int r = 0; r < data.size(); r++) {
            LinearLayout rowView = new LinearLayout(this);
            rowView.setOrientation(LinearLayout.HORIZONTAL);
            ArrayList<EditText> cellRow = new ArrayList<>();
            for (int col = 0; col < data.get(r).size(); col++) {
                EditText cell = new EditText(this);
                cell.setSingleLine(true);
                cell.setTextSize(14);
                cell.setPadding(dp(6), dp(4), dp(6), dp(4));
                cell.setHint("R" + (r + 1) + "C" + (col + 1));
                cell.setText(data.get(r).get(col));
                LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(dp(124), dp(52));
                cellParams.setMargins(dp(1), dp(1), dp(1), dp(1));
                rowView.addView(cell, cellParams);
                cellRow.add(cell);
            }
            grid.addView(rowView);
            sheetCells.add(cellRow);
        }
        HorizontalScrollView horizontal = new HorizontalScrollView(this);
        horizontal.addView(grid);
        content.addView(horizontal, new LinearLayout.LayoutParams(-1, -2));
        content.addView(action("Export CSV copy", v -> {
            String filename = currentCsvFile == null ? "MyOffice_Sheet.csv" : currentCsvFile.getName();
            startCsvExport(filename, serializeSheet(readSheetCells()));
        }));
        content.addView(action("Save CSV sheet", v -> saveSpreadsheet()));
        content.addView(action("Discard changes", v -> new AlertDialog.Builder(this)
                .setTitle("Discard this sheet edit?")
                .setMessage("Unsaved changes will be lost.")
                .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Discard", (dialog, which) -> showSpreadsheetList())
                .show()));
        if (resetOriginal) sheetOriginalSnapshot = serializeSheet(data);
        back.setOnClickListener(v -> {
            if (serializeSheet(readSheetCells()).equals(sheetOriginalSnapshot)) {
                showSpreadsheetList();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Unsaved changes")
                        .setMessage("Save this CSV sheet before leaving?")
                        .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                        .setNeutralButton("Discard", (dialog, which) -> showSpreadsheetList())
                        .setPositiveButton("Save", (dialog, which) -> saveSpreadsheet())
                        .show();
            }
        });
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }


    // First Presentation milestone: local text-only slide decks with navigation and persistence.
    private File presentationDirectory() {
        File dir = new File(getFilesDir(), "presentations");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private File[] deckFiles() {
        File[] files = presentationDirectory().listFiles((dir, name) -> name.endsWith(".deck"));
        if (files == null) return new File[0];
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files;
    }

    private String encodeDeckText(String value) {
        return Base64.encodeToString(value.getBytes(java.nio.charset.StandardCharsets.UTF_8), Base64.NO_WRAP);
    }

    private String decodeDeckText(String value) {
        return new String(Base64.decode(value, Base64.DEFAULT), java.nio.charset.StandardCharsets.UTF_8);
    }

    private String serializeDeck(ArrayList<Slide> slides) {
        StringBuilder out = new StringBuilder("MYOFFICE_DECK_V1\n");
        for (Slide slide : slides) {
            out.append(encodeDeckText(slide.title)).append('\n');
            out.append(encodeDeckText(slide.body)).append('\n');
        }
        return out.toString();
    }

    private ArrayList<Slide> readDeck(File file) throws Exception {
        String raw;
        try (InputStream in = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
            raw = out.toString("UTF-8");
        }
        String[] lines = raw.split("\\n", -1);
        if (lines.length == 0 || !lines[0].equals("MYOFFICE_DECK_V1")) throw new IllegalArgumentException("Unsupported slide deck");
        ArrayList<Slide> slides = new ArrayList<>();
        for (int i = 1; i + 1 < lines.length; i += 2) {
            if (i + 1 < lines.length) slides.add(new Slide(decodeDeckText(lines[i]), decodeDeckText(lines[i + 1])));
        }
        if (slides.isEmpty()) slides.add(new Slide("", ""));
        return slides;
    }

    private void showPresentationList() {
        LinearLayout content = column();
        TextView back = label("‹  Back to MyOffice", 16, true);
        back.setOnClickListener(v -> showHome());
        content.addView(back);
        content.addView(label("Presentation · Slide decks", 25, true));
        content.addView(label("Create simple offline text slides. Themes, images, animations and PPTX are not included yet.", 13, false));
        content.addView(action("＋  New slide deck", v -> {
            deckSlides = new ArrayList<>();
            deckSlides.add(new Slide("", ""));
            deckIndex = 0;
            currentDeckFile = null;
            showPresentationEditor(true);
        }));
        File[] files = deckFiles();
        if (files.length == 0) {
            content.addView(gap(12));
            content.addView(label("No slide decks yet.", 15, false));
        } else {
            for (File file : files) {
                String title = file.getName();
                int count = 0;
                try {
                    ArrayList<Slide> slides = readDeck(file);
                    count = slides.size();
                    if (!slides.get(0).title.trim().isEmpty()) title = slides.get(0).title.trim();
                } catch (Exception ignored) { }
                final File selected = file;
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(dp(12), dp(8), dp(12), dp(8));
                card.setBackgroundColor(0xFFFFFFFF);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
                params.setMargins(0, dp(5), 0, dp(5));
                content.addView(card, params);
                card.addView(label(title, 17, true));
                card.addView(label(count + " slide(s) · Modified " + DateFormat.getDateTimeInstance().format(new Date(file.lastModified())), 12, false));
                card.addView(action("Delete deck", v -> confirmDeleteFile(selected, "slide deck", () -> showPresentationList())));
                card.setOnClickListener(v -> {
                    try {
                        deckSlides = readDeck(selected);
                        deckIndex = 0;
                        currentDeckFile = selected;
                        showPresentationEditor(true);
                    } catch (Exception e) {
                        Toast.makeText(this, "Could not open this slide deck.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }

    private void syncDeckEditor() {
        if (deckSlides.isEmpty()) deckSlides.add(new Slide("", ""));
        if (deckTitleInput != null && deckBodyInput != null) {
            deckSlides.get(deckIndex).title = deckTitleInput.getText().toString();
            deckSlides.get(deckIndex).body = deckBodyInput.getText().toString();
        }
    }

    private void savePresentation() {
        syncDeckEditor();
        File target = currentDeckFile;
        if (target == null) target = new File(presentationDirectory(), "presentation_" + System.currentTimeMillis() + ".deck");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
            writer.write(serializeDeck(deckSlides));
            writer.flush();
            Toast.makeText(this, "Slide deck saved on this device.", Toast.LENGTH_SHORT).show();
            showPresentationList();
        } catch (Exception e) {
            Toast.makeText(this, "Could not save slide deck.", Toast.LENGTH_LONG).show();
        }
    }

    private void showPresentationEditor(boolean resetOriginal) {
        if (deckSlides.isEmpty()) deckSlides.add(new Slide("", ""));
        deckIndex = Math.max(0, Math.min(deckIndex, deckSlides.size() - 1));
        Slide slide = deckSlides.get(deckIndex);
        LinearLayout content = column();
        TextView back = label("‹  Back to presentations", 16, true);
        content.addView(back);
        content.addView(label("Slide editor", 24, true));
        content.addView(label("Text-only offline presentation. Edit a slide, add slides, then save.", 13, false));
        content.addView(label("Slide " + (deckIndex + 1) + " of " + deckSlides.size(), 14, true));
        content.addView(label("Slide title", 14, true));
        deckTitleInput = new EditText(this);
        deckTitleInput.setSingleLine(true);
        deckTitleInput.setHint("Slide title");
        deckTitleInput.setText(slide.title);
        content.addView(deckTitleInput);
        content.addView(label("Slide content", 14, true));
        deckBodyInput = new EditText(this);
        deckBodyInput.setGravity(Gravity.TOP | Gravity.START);
        deckBodyInput.setMinLines(8);
        deckBodyInput.setHint("Write the main points for this slide…");
        deckBodyInput.setText(slide.body);
        deckBodyInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        content.addView(deckBodyInput, new LinearLayout.LayoutParams(-1, dp(250)));
        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.addView(action("Previous", v -> {
            syncDeckEditor();
            if (deckIndex > 0) { deckIndex--; showPresentationEditor(false); }
        }), new LinearLayout.LayoutParams(0, -2, 1));
        navigation.addView(action("Next", v -> {
            syncDeckEditor();
            if (deckIndex < deckSlides.size() - 1) { deckIndex++; showPresentationEditor(false); }
        }), new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(navigation);
        content.addView(action("+ Add slide", v -> {
            syncDeckEditor();
            deckSlides.add(new Slide("", ""));
            deckIndex = deckSlides.size() - 1;
            showPresentationEditor(false);
        }));
        content.addView(action("Save slide deck", v -> savePresentation()));
        content.addView(action("Start slideshow", v -> {
            syncDeckEditor();
            showSlideshow();
        }));
        content.addView(action("Share current slide text", v -> {
            syncDeckEditor();
            Slide current = deckSlides.get(deckIndex);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, current.title + "\n\n" + current.body);
            try { startActivity(Intent.createChooser(share, "Share slide text")); }
            catch (Exception e) { Toast.makeText(this, "No compatible sharing app was found.", Toast.LENGTH_LONG).show(); }
        }));
        content.addView(action("Discard changes", v -> new AlertDialog.Builder(this)
                .setTitle("Discard this deck edit?")
                .setMessage("Unsaved changes will be lost.")
                .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Discard", (dialog, which) -> showPresentationList())
                .show()));
        if (resetOriginal) {
            syncDeckEditor();
            deckOriginalSnapshot = serializeDeck(deckSlides);
        }
        back.setOnClickListener(v -> {
            syncDeckEditor();
            if (serializeDeck(deckSlides).equals(deckOriginalSnapshot)) {
                showPresentationList();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Unsaved changes")
                        .setMessage("Save this slide deck before leaving?")
                        .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                        .setNeutralButton("Discard", (dialog, which) -> showPresentationList())
                        .setPositiveButton("Save", (dialog, which) -> savePresentation())
                        .show();
            }
        });
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }

    private void confirmDeleteFile(File file, String itemLabel, Runnable refresh) {
        new AlertDialog.Builder(this)
                .setTitle("Delete " + itemLabel + "?")
                .setMessage("This permanently removes the local MyOffice copy. It cannot be undone.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (file.delete()) {
                        Toast.makeText(this, itemLabel + " deleted.", Toast.LENGTH_SHORT).show();
                        refresh.run();
                    } else {
                        Toast.makeText(this, "Could not delete " + itemLabel.toLowerCase(Locale.ROOT) + ".", Toast.LENGTH_LONG).show();
                    }
                }).show();
    }


    private void showSlideshow() {
        if (deckSlides.isEmpty()) deckSlides.add(new Slide("", ""));
        deckIndex = Math.max(0, Math.min(deckIndex, deckSlides.size() - 1));
        Slide slide = deckSlides.get(deckIndex);
        LinearLayout content = column();
        TextView exit = label("‹  Exit slideshow", 16, true);
        exit.setOnClickListener(v -> showPresentationEditor(false));
        content.addView(exit);
        content.addView(label("Slide " + (deckIndex + 1) + " of " + deckSlides.size(), 13, false));
        TextView title = label(slide.title.isEmpty() ? " " : slide.title, 30, true);
        title.setGravity(Gravity.CENTER);
        title.setPadding(dp(8), dp(24), dp(8), dp(24));
        content.addView(title);
        TextView body = label(slide.body.isEmpty() ? " " : slide.body, 22, false);
        body.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(16), dp(20), dp(16), dp(20));
        body.setBackgroundColor(0xFFFFFFFF);
        content.addView(body, new LinearLayout.LayoutParams(-1, dp(360)));
        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        navigation.addView(action("Previous", v -> {
            if (deckIndex > 0) { deckIndex--; showSlideshow(); }
        }), new LinearLayout.LayoutParams(0, -2, 1));
        navigation.addView(action("Next", v -> {
            if (deckIndex < deckSlides.size() - 1) { deckIndex++; showSlideshow(); }
        }), new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(navigation);
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
        String query = recentSearchInput == null ? "" : recentSearchInput.getText().toString().trim().toLowerCase(Locale.ROOT);
        int shown = 0;
        for (int i = recentUris.size() - 1; i >= 0; i--) {
            final String value = recentUris.get(i);
            Uri uri = Uri.parse(value);
            String name = nameOf(uri);
            if (!query.isEmpty() && !name.toLowerCase(Locale.ROOT).contains(query)) continue;
            shown++;
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
        if (shown == 0) recentList.addView(label("No recent files match that name.", 14, false));
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
