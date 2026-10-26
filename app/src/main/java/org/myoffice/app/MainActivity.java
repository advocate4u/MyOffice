package org.myoffice.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.AbsoluteSizeSpan;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
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
import java.util.Collections;
import java.util.Locale;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class MainActivity extends Activity {
    private static final int PICK_FILE = 4101;
    private static final int PICK_IMAGES = 4102;
    private static final int SAVE_PDF = 4103;
    private static final int SAVE_TEXT = 4104;
    private static final int SAVE_CSV = 4105;
    private static final int SAVE_DOCX = 4106;
    private static final int SAVE_PDF_COPY = 4107;
    private static final int PICK_PDFS = 4109;
    private static final int SAVE_RTF = 4110;
    private static final int SAVE_PNG = 4111;
    private static final int SAVE_XLSX = 4108;
    private File pendingDocxFile;
    private File pendingPngFile;
    private File pendingXlsxFile;
    private String pendingCsvExport;
    private String pendingTextExport;
    private String pendingRtfExport;
    private File pendingPdfFile;
    private int currentPdfRotationDegrees = 0;
    private Uri pendingPdfCopySource;
    private ArrayList<Uri> pendingPdfMergeSources;
    private static final String PREFS = "myoffice_recent";
    private static final String KEY = "uris";
    private static final String FAVORITES_PREFS = "myoffice_favorites";
    private static final String FAVORITES_KEY = "favorite_uris";
    private static final int MAX_RECENTS = 12;
    private static final String NOTE_PREFIX = "MYOFFICE_NOTE_TITLE:";
    private View currentRootView;
    private final ArrayList<View> navigationHistory = new ArrayList<>();
    private boolean restoringBackNavigation = false;
    private long lastBackPressedAt = 0L;
    private String currentScreenRoute = "home";
    private final ArrayList<String> routeHistory = new ArrayList<>();
    private final ArrayList<Boolean> homeHistory = new ArrayList<>();
    private boolean homeVisible = true;
    private Runnable noteEditorBackHandler;
    private Runnable writerEditorBackHandler;
    private LinearLayout page;
    private LinearLayout recentList;
    private EditText recentSearchInput;
    private Button recentSortButton;
    private int recentSortMode = 0;
    private int recentFilterMode = 0;
    private int noteSortMode = 0;
    private int writerSortMode = 0;
    private int spreadsheetSortMode = 0;
    private int presentationSortMode = 0;
    private final ArrayList<Button> recentFilterButtons = new ArrayList<>();
    private SharedPreferences prefs;
    private final ArrayList<String> recentUris = new ArrayList<>();
    private final ArrayList<String> favoriteUris = new ArrayList<>();
    private final ArrayList<String> selectedRecentUris = new ArrayList<>();
    private final ArrayList<String> visibleRecentUris = new ArrayList<>();
    private boolean favoritesOnly = false;
    private boolean recentSelectionMode = false;
    private Button recentSelectionButton;
    private Button shareSelectedButton;
    private Button selectVisibleButton;
    private ArrayList<ArrayList<EditText>> sheetCells = new ArrayList<>();
    private File currentCsvFile;
    private String sheetOriginalSnapshot = "";
    private boolean spreadsheetEditorOpen = false;
    private boolean presentationEditorOpen = false;
    private boolean slideshowOpen = false;
    private static final class Slide {
        String title;
        String body;
        Slide(String title, String body) { this.title = title; this.body = body; }
    }
    private static final class RtfStyleState {
        boolean bold;
        boolean italic;
        boolean underline;
        boolean strike;
        float sizeRatio = 1.0f;
        RtfStyleState copy() {
            RtfStyleState copy = new RtfStyleState();
            copy.bold = bold;
            copy.italic = italic;
            copy.underline = underline;
            copy.strike = strike;
            copy.sizeRatio = sizeRatio;
            return copy;
        }
        void reset() {
            bold = false; italic = false; underline = false; strike = false; sizeRatio = 1.0f;
        }
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
        loadFavorites();
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
        recentSelectionMode = false;
        selectedRecentUris.clear();
        page = column();
        page.addView(label("MyOffice", 29, true));
        TextView subtitle = label("Your files. Your learning. Offline.  •  By MyAdv", 15, false);
        subtitle.setTextColor(0xFF536078);
        page.addView(subtitle);
        page.addView(gap(12));
        TextView intro = label("Open a document from your phone or a connected document provider. No account required.", 16, false);
        intro.setPadding(dp(14), dp(14), dp(14), dp(14));
        intro.setBackgroundColor(0xFFFFFFFF);
        page.addView(intro);
        page.addView(action("＋  Open a file", v -> openPicker()));
        addShortcutRow(page, "Create note", "Quick note · Offline", v -> showNoteEditor(null, "", ""),
                "Writer", "Text documents", v -> showWriterList());
        addShortcutRow(page, "My notes", "Saved notes", v -> showNotes(),
                "Spreadsheet", "CSV sheets", v -> showSpreadsheetList());
        addShortcutRow(page, "Presentation", "Text slide decks", v -> showPresentationList(),
                "Images to PDF", "Create a PDF offline", v -> openImagePicker());
        page.addView(action("★  Favorite files", v -> showFavorites()));
        page.addView(action("Refresh recent files", v -> { loadRecents(); showHome(); }));
        page.addView(action("Clear recent history", v -> new AlertDialog.Builder(this)
                .setTitle("Clear recent history?")
                .setMessage("This removes recent-file entries from MyOffice only. It does not delete any documents.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Clear history", (dialog, which) -> {
                    recentUris.clear();
                    saveRecents();
                    renderRecents();
                    Toast.makeText(this, "Recent history cleared.", Toast.LENGTH_SHORT).show();
                }).show()));
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
        recentSortButton = action("Sort: Newest first", v -> {
            recentSortMode = (recentSortMode + 1) % 4;
            updateRecentSortLabel();
            renderRecents();
        });
        page.addView(recentSortButton);
        page.addView(action("☆ Favorites only", v -> {
            favoritesOnly = !favoritesOnly;
            v.setAlpha(favoritesOnly ? 1.0f : 0.85f);
            if (v instanceof Button) ((Button) v).setText(favoritesOnly ? "★ Showing favorites" : "☆ Favorites only");
            renderRecents();
        }));
        recentSelectionButton = action("Select files", v -> {
            recentSelectionMode = !recentSelectionMode;
            if (!recentSelectionMode) selectedRecentUris.clear();
            recentSelectionButton.setText(recentSelectionMode ? "Cancel selection" : "Select files");
            updateShareSelectedButton();
            updateSelectVisibleButton();
            renderRecents();
        });
        page.addView(recentSelectionButton);
        selectVisibleButton = action("Select visible", v -> {
            if (!recentSelectionMode) {
                recentSelectionMode = true;
                recentSelectionButton.setText("Cancel selection");
                renderRecents();
            }
            boolean allVisibleSelected = !visibleRecentUris.isEmpty();
            for (String value : visibleRecentUris) {
                if (!selectedRecentUris.contains(value)) {
                    allVisibleSelected = false;
                    break;
                }
            }
            if (allVisibleSelected) {
                selectedRecentUris.removeAll(visibleRecentUris);
            } else {
                for (String value : visibleRecentUris) {
                    if (!selectedRecentUris.contains(value)) selectedRecentUris.add(value);
                }
            }
            updateShareSelectedButton();
            updateSelectVisibleButton();
            renderRecents();
        });
        selectVisibleButton.setEnabled(false);
        page.addView(selectVisibleButton);
        shareSelectedButton = action("Share selected (0)", v -> shareSelectedRecentFiles());
        shareSelectedButton.setEnabled(false);
        page.addView(shareSelectedButton);
        LinearLayout filters = new LinearLayout(this);
        filters.setOrientation(LinearLayout.HORIZONTAL);
        recentFilterButtons.clear();
        String[] filterLabels = {"All", "PDF", "Text", "CSV", "Office"};
        for (int i = 0; i < filterLabels.length; i++) {
            final int filter = i;
            Button button = action(filterLabels[i], v -> {
                recentFilterMode = filter;
                updateRecentFilterButtons();
                renderRecents();
            });
            recentFilterButtons.add(button);
            filters.addView(button, new LinearLayout.LayoutParams(0, -2, 1));
        }
        page.addView(filters);
        updateRecentFilterButtons();
        recentList = new LinearLayout(this);
        recentList.setOrientation(LinearLayout.VERTICAL);
        page.addView(recentList);
        renderRecents();
        page.addView(gap(14));
        TextView foot = label("Open and manage local files offline. Full DOCX/XLSX/PPTX formatting compatibility is still in development.", 12, false);
        foot.setTextColor(0xFF596477);
        page.addView(foot);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(page);
        navigationHistory.clear();
        routeHistory.clear();
        homeHistory.clear();
        currentRootView = null;
        currentScreenRoute = "home";
        setAppContentView(scroll, "home");
        homeVisible = true;
    }

    private void addShortcutRow(LinearLayout parent,
                                String leftTitle, String leftSubtitle, View.OnClickListener leftClick,
                                String rightTitle, String rightSubtitle, View.OnClickListener rightClick) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
        rowParams.setMargins(0, dp(3), 0, dp(3));
        parent.addView(row, rowParams);
        row.addView(shortcutTile(leftTitle, leftSubtitle, leftClick),
                new LinearLayout.LayoutParams(0, dp(84), 1));
        View spacer = new View(this);
        row.addView(spacer, new LinearLayout.LayoutParams(dp(8), 1));
        row.addView(shortcutTile(rightTitle, rightSubtitle, rightClick),
                new LinearLayout.LayoutParams(0, dp(84), 1));
    }

    private View shortcutTile(String title, String subtitle, View.OnClickListener click) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER_VERTICAL);
        tile.setPadding(dp(12), dp(10), dp(12), dp(10));
        tile.setBackgroundColor(0xFFFFFFFF);
        tile.setClickable(true);
        tile.setFocusable(true);
        tile.setOnClickListener(click);
        tile.addView(label(title, 16, true));
        TextView detail = label(subtitle, 11, false);
        detail.setTextColor(0xFF536078);
        tile.addView(detail);
        return tile;
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
        if (request == PICK_PDFS) {
            ArrayList<Uri> sources = pendingPdfMergeSources;
            pendingPdfMergeSources = null;
            if (result != RESULT_OK || data == null || sources == null) return;
            ArrayList<Uri> chosen = new ArrayList<>(sources);
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri selected = data.getClipData().getItemAt(i).getUri();
                    if (selected != null && !containsUri(chosen, selected)) chosen.add(selected);
                    if (selected != null) persistReadPermission(data, selected);
                }
            } else if (data.getData() != null) {
                Uri selected = data.getData();
                if (!containsUri(chosen, selected)) chosen.add(selected);
                persistReadPermission(data, selected);
            }
            if (chosen.size() < 2) {
                Toast.makeText(this, "Choose at least one additional PDF to merge.", Toast.LENGTH_LONG).show();
                return;
            }
            createMergedPdfAsync(chosen, nameOf(chosen.get(0)));
            return;
        }
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
        if (request == SAVE_PDF_COPY) {
            Uri source = pendingPdfCopySource;
            pendingPdfCopySource = null;
            if (result != RESULT_OK || data == null || data.getData() == null || source == null) return;
            try (InputStream in = getContentResolver().openInputStream(source);
                 java.io.OutputStream out = getContentResolver().openOutputStream(data.getData(), "wt")) {
                if (in == null || out == null) throw new IllegalStateException("A document stream is unavailable");
                byte[] buffer = new byte[8192];
                int count;
                while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
                out.flush();
                Toast.makeText(this, "PDF copy saved.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Could not save this PDF copy.", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (request == SAVE_PNG) {
            File temp = pendingPngFile;
            pendingPngFile = null;
            if (result != RESULT_OK || data == null || data.getData() == null || temp == null) {
                if (temp != null) temp.delete();
                return;
            }
            try (InputStream in = new FileInputStream(temp);
                 java.io.OutputStream out = getContentResolver().openOutputStream(data.getData(), "wt")) {
                if (out == null) throw new IllegalStateException("Destination cannot be written");
                byte[] buffer = new byte[8192];
                int count;
                while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
                out.flush();
                temp.delete();
                Toast.makeText(this, "PDF page exported as PNG.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                temp.delete();
                Toast.makeText(this, "Could not save the PNG image.", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (request == SAVE_XLSX) {
            File temp = pendingXlsxFile;
            pendingXlsxFile = null;
            if (result != RESULT_OK || data == null || data.getData() == null || temp == null) {
                if (temp != null) temp.delete();
                return;
            }
            try (InputStream in = new FileInputStream(temp);
                 java.io.OutputStream out = getContentResolver().openOutputStream(data.getData(), "wt")) {
                if (out == null) throw new IllegalStateException("Destination cannot be written");
                byte[] buffer = new byte[8192];
                int count;
                while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
                out.flush();
                temp.delete();
                Toast.makeText(this, "XLSX exported.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                temp.delete();
                Toast.makeText(this, "Could not export XLSX.", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (request == SAVE_DOCX) {
            File temp = pendingDocxFile;
            pendingDocxFile = null;
            if (result != RESULT_OK || data == null || data.getData() == null || temp == null) {
                if (temp != null) temp.delete();
                return;
            }
            try (InputStream in = new FileInputStream(temp);
                 java.io.OutputStream out = getContentResolver().openOutputStream(data.getData(), "wt")) {
                if (out == null) throw new IllegalStateException("Destination cannot be written");
                byte[] buffer = new byte[8192];
                int count;
                while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
                out.flush();
                temp.delete();
                Toast.makeText(this, "DOCX exported.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                temp.delete();
                Toast.makeText(this, "Could not export DOCX.", Toast.LENGTH_LONG).show();
            }
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
        if (request == SAVE_RTF) {
            String export = pendingRtfExport;
            pendingRtfExport = null;
            if (result != RESULT_OK || data == null || data.getData() == null || export == null) return;
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    getContentResolver().openOutputStream(data.getData(), "wt"), "UTF-8")) {
                writer.write(export);
                writer.flush();
                Toast.makeText(this, "RTF exported.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Could not export RTF.", Toast.LENGTH_LONG).show();
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



    private void openXlsxPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try { startActivityForResult(intent, PICK_FILE); }
        catch (Exception e) { Toast.makeText(this, "Could not open the XLSX picker.", Toast.LENGTH_LONG).show(); }
    }

    private String xlsxColumnName(int zeroBasedColumn) {
        StringBuilder name = new StringBuilder();
        int value = zeroBasedColumn + 1;
        while (value > 0) {
            int remainder = (value - 1) % 26;
            name.insert(0, (char) ('A' + remainder));
            value = (value - 1) / 26;
        }
        return name.toString();
    }

    private void writeXlsxFile(File target, ArrayList<ArrayList<String>> rows) throws Exception {
        if (rows.size() > 500) throw new IllegalArgumentException("XLSX export is limited to 500 rows");
        for (ArrayList<String> row : rows) {
            if (row.size() > 50) throw new IllegalArgumentException("XLSX export is limited to 50 columns");
        }
        String contentTypes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                + "</Types>";
        String rootRelationships = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                + "</Relationships>";
        String workbook = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<sheets><sheet name=\"MyOffice\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>";
        String workbookRelationships = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                + "</Relationships>";
        StringBuilder sheet = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
        for (int r = 0; r < rows.size(); r++) {
            sheet.append("<row r=\"").append(r + 1).append("\">");
            ArrayList<String> row = rows.get(r);
            for (int col = 0; col < row.size(); col++) {
                String value = row.get(col) == null ? "" : row.get(col);
                String reference = xlsxColumnName(col) + (r + 1);
                if (value.matches("-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[Ee][+-]?[0-9]+)?")
                        && !value.isEmpty()) {
                    sheet.append("<c r=\"").append(reference).append("\"><v>")
                            .append(value).append("</v></c>");
                } else {
                    sheet.append("<c r=\"").append(reference).append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">")
                            .append(xmlEscape(value)).append("</t></is></c>");
                }
            }
            sheet.append("</row>");
        }
        sheet.append("</sheetData></worksheet>");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(target))) {
            addDocxEntry(zip, "[Content_Types].xml", contentTypes);
            addDocxEntry(zip, "_rels/.rels", rootRelationships);
            addDocxEntry(zip, "xl/workbook.xml", workbook);
            addDocxEntry(zip, "xl/_rels/workbook.xml.rels", workbookRelationships);
            addDocxEntry(zip, "xl/worksheets/sheet1.xml", sheet.toString());
        }
    }

    private void startXlsxExport(String suggestedName, ArrayList<ArrayList<String>> rows) {
        File temp = null;
        try {
            temp = File.createTempFile("myoffice_sheet_", ".xlsx", getCacheDir());
            writeXlsxFile(temp, rows);
            pendingXlsxFile = temp;
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.putExtra(Intent.EXTRA_TITLE, suggestedName.toLowerCase(Locale.ROOT).endsWith(".xlsx") ? suggestedName : suggestedName + ".xlsx");
            startActivityForResult(intent, SAVE_XLSX);
        } catch (Exception e) {
            if (temp != null) temp.delete();
            pendingXlsxFile = null;
            Toast.makeText(this, "Could not prepare XLSX export.", Toast.LENGTH_LONG).show();
        }
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


    private String xmlEscape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    private void addDocxEntry(ZipOutputStream zip, String name, String value) throws Exception {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private void writeDocxFile(File target, String text) throws Exception {
        String contentTypes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>"
                + "</Types>";
        String relationships = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>"
                + "</Relationships>";
        StringBuilder document = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"><w:body>");
        String[] paragraphs = text.split("\\n", -1);
        for (String paragraph : paragraphs) {
            document.append("<w:p>");
            String[] segments = paragraph.split("\\t", -1);
            for (int i = 0; i < segments.length; i++) {
                document.append("<w:r><w:t xml:space=\"preserve\">")
                        .append(xmlEscape(segments[i])).append("</w:t></w:r>");
                if (i < segments.length - 1) document.append("<w:r><w:tab/></w:r>");
            }
            document.append("</w:p>");
        }
        document.append("<w:sectPr><w:pgSz w:w=\"11906\" w:h=\"16838\"/>")
                .append("<w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"708\" w:footer=\"708\" w:gutter=\"0\"/>")
                .append("</w:sectPr></w:body></w:document>");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(target))) {
            addDocxEntry(zip, "[Content_Types].xml", contentTypes);
            addDocxEntry(zip, "_rels/.rels", relationships);
            addDocxEntry(zip, "word/document.xml", document.toString());
        }
    }

    private String docxRunProperties(Spanned richText, int position) {
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean strike = false;
        Integer foreground = null;
        Integer background = null;
        Float sizeChange = null;
        Integer absoluteSizePx = null;
        String fontFamily = null;
        Object[] spans = richText.getSpans(position, Math.min(richText.length(), position + 1), Object.class);
        for (Object span : spans) {
            if (span instanceof StyleSpan) {
                int style = ((StyleSpan) span).getStyle();
                if ((style & Typeface.BOLD) != 0) bold = true;
                if ((style & Typeface.ITALIC) != 0) italic = true;
            } else if (span instanceof UnderlineSpan) {
                underline = true;
            } else if (span instanceof StrikethroughSpan) {
                strike = true;
            } else if (span instanceof ForegroundColorSpan) {
                foreground = ((ForegroundColorSpan) span).getForegroundColor();
            } else if (span instanceof BackgroundColorSpan) {
                background = ((BackgroundColorSpan) span).getBackgroundColor();
            } else if (span instanceof RelativeSizeSpan) {
                sizeChange = ((RelativeSizeSpan) span).getSizeChange();
            } else if (span instanceof AbsoluteSizeSpan) {
                absoluteSizePx = ((AbsoluteSizeSpan) span).getSize();
            } else if (span instanceof TypefaceSpan) {
                fontFamily = ((TypefaceSpan) span).getFamily();
            }
        }
        StringBuilder properties = new StringBuilder();
        if (bold) properties.append("<w:b/>");
        if (italic) properties.append("<w:i/>");
        if (underline) properties.append("<w:u w:val=\"single\"/>");
        if (strike) properties.append("<w:strike/>");
        if (fontFamily != null && !fontFamily.trim().isEmpty()) {
            String safeFamily = xmlEscape(fontFamily.trim());
            properties.append("<w:rFonts w:ascii=\"").append(safeFamily)
                    .append("\" w:hAnsi=\"").append(safeFamily)
                    .append("\" w:eastAsia=\"").append(safeFamily).append("\"/>");
        }
        if (foreground != null) properties.append("<w:color w:val=\"")
                .append(String.format(Locale.ROOT, "%06X", foreground & 0xFFFFFF)).append("\"/>");
        if (background != null) {
            int color = background & 0xFFFFFF;
            if (color == 0xFFFF00) properties.append("<w:highlight w:val=\"yellow\"/>");
            else properties.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"")
                    .append(String.format(Locale.ROOT, "%06X", color)).append("\"/>");
        }
        int halfPoints = -1;
        if (absoluteSizePx != null) halfPoints = Math.max(16, Math.min(96, Math.round(absoluteSizePx * 1.5f)));
        else if (sizeChange != null) halfPoints = Math.max(16, Math.min(96, Math.round(24f * sizeChange)));
        if (halfPoints >= 0) {
            properties.append("<w:sz w:val=\"").append(halfPoints).append("\"/>");
            properties.append("<w:szCs w:val=\"").append(halfPoints).append("\"/>");
        }
        return properties.length() == 0 ? "" : "<w:rPr>" + properties + "</w:rPr>";
    }

    private void appendFormattedDocxParagraph(StringBuilder document, Spanned richText, int start, int end) {
        String plain = richText.toString();
        int position = start;
        while (position < end) {
            if (plain.charAt(position) == '\t') {
                document.append("<w:r><w:tab/></w:r>");
                position++;
                continue;
            }
            String properties = docxRunProperties(richText, position);
            int runStart = position;
            while (position < end && plain.charAt(position) != '\t'
                    && properties.equals(docxRunProperties(richText, position))) {
                int codePoint = plain.codePointAt(position);
                position += Character.charCount(codePoint);
            }
            document.append("<w:r>");
            if (!properties.isEmpty()) document.append(properties);
            document.append("<w:t xml:space=\"preserve\">")
                    .append(xmlEscape(plain.substring(runStart, position)))
                    .append("</w:t></w:r>");
        }
    }

    private void writeDocxFile(File target, Spanned richText, int alignmentGravity) throws Exception {
        String contentTypes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>"
                + "</Types>";
        String relationships = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>"
                + "</Relationships>";
        String plain = richText.toString();
        StringBuilder document = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"><w:body>");
        int start = 0;
        do {
            int newline = plain.indexOf('\n', start);
            int end = newline < 0 ? plain.length() : newline;
            document.append("<w:p>");
            AlignmentSpan.Standard[] paragraphAlignments = richText.getSpans(start, Math.min(plain.length(), Math.max(start + 1, end + 1)), AlignmentSpan.Standard.class);
            Layout.Alignment paragraphAlignment = paragraphAlignments.length > 0
                    ? paragraphAlignments[0].getAlignment() : null;
            int horizontal = alignmentGravity & Gravity.HORIZONTAL_GRAVITY_MASK;
            if (paragraphAlignment == Layout.Alignment.ALIGN_CENTER || (paragraphAlignment == null && horizontal == Gravity.CENTER_HORIZONTAL)) {
                document.append("<w:pPr><w:jc w:val=\"center\"/></w:pPr>");
            } else if (paragraphAlignment == Layout.Alignment.ALIGN_OPPOSITE || (paragraphAlignment == null && horizontal == Gravity.RIGHT)) {
                document.append("<w:pPr><w:jc w:val=\"right\"/></w:pPr>");
            } else if (paragraphAlignment == Layout.Alignment.ALIGN_NORMAL && horizontal == Gravity.FILL_HORIZONTAL) {
                document.append("<w:pPr><w:jc w:val=\"both\"/></w:pPr>");
            }
            appendFormattedDocxParagraph(document, richText, start, end);
            document.append("</w:p>");
            if (newline < 0) break;
            start = newline + 1;
        } while (start <= plain.length());
        document.append("<w:sectPr><w:pgSz w:w=\"11906\" w:h=\"16838\"/>")
                .append("<w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"708\" w:footer=\"708\" w:gutter=\"0\"/>")
                .append("</w:sectPr></w:body></w:document>");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(target))) {
            addDocxEntry(zip, "[Content_Types].xml", contentTypes);
            addDocxEntry(zip, "_rels/.rels", relationships);
            addDocxEntry(zip, "word/document.xml", document.toString());
        }
    }

    private void exportWriterAsDocx(String suggestedName, Spanned richText, int alignmentGravity) {
        File temp = null;
        try {
            temp = File.createTempFile("myoffice_writer_", ".docx", getCacheDir());
            writeDocxFile(temp, richText, alignmentGravity);
            pendingDocxFile = temp;
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            intent.putExtra(Intent.EXTRA_TITLE, suggestedName.toLowerCase(Locale.ROOT).endsWith(".docx") ? suggestedName : suggestedName + ".docx");
            startActivityForResult(intent, SAVE_DOCX);
        } catch (Exception e) {
            if (temp != null) temp.delete();
            pendingDocxFile = null;
            Toast.makeText(this, "Could not prepare DOCX export.", Toast.LENGTH_LONG).show();
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
        if (lower.endsWith(".xlsx") || mime.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            try {
                ArrayList<ArrayList<String>> imported = readXlsxSheet(uri);
                Toast.makeText(this, "First worksheet imported as a new local CSV sheet. The original XLSX is unchanged.", Toast.LENGTH_LONG).show();
                showSpreadsheetEditor(null, imported, true);
                return;
            } catch (Exception e) {
                Toast.makeText(this, "Could not import this XLSX. Basic first-sheet cell values are supported.", Toast.LENGTH_LONG).show();
                showDetails(uri, name, mime);
                return;
            }
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
        if (lower.endsWith(".rtf") || mime.equals("application/rtf") || mime.equals("text/rtf")) {
            try {
                Spanned imported = readRtfRichText(uri);
                Toast.makeText(this, "RTF text and basic bold/italic/underline/size formatting imported as a copy. Advanced formatting is not preserved.", Toast.LENGTH_LONG).show();
                showWriterEditor(null, imported);
                return;
            } catch (Exception e) {
                Toast.makeText(this, "Could not import text from this RTF file.", Toast.LENGTH_LONG).show();
                showDetails(uri, name, mime);
                return;
            }
        }
        if (lower.endsWith(".docx") || mime.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            try {
                Spanned imported = readDocxRichText(uri);
                Toast.makeText(this, "DOCX text and basic character formatting imported as a new Writer copy. Images, tables and advanced Word features are not preserved.", Toast.LENGTH_LONG).show();
                showWriterEditor(null, imported);
                return;
            } catch (Exception e) {
                Toast.makeText(this, "Could not import text from this DOCX file.", Toast.LENGTH_LONG).show();
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


    private String readDocxText(Uri uri) throws Exception {
        return readDocxRichText(uri).toString();
    }

    private SpannableStringBuilder readDocxRichText(Uri uri) throws Exception {
        if (sizeOf(uri) > 10L * 1024L * 1024L) throw new IllegalArgumentException("DOCX exceeds the 10 MiB import limit");
        byte[] documentXml = null;
        byte[] numberingXml = null;
        InputStream source = getContentResolver().openInputStream(uri);
        if (source == null) throw new IllegalStateException("DOCX source unavailable");
        try (ZipInputStream zip = new ZipInputStream(source)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if ("word/document.xml".equals(entry.getName()) || "word/numbering.xml".equals(entry.getName())) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int count;
                    int total = 0;
                    while ((count = zip.read(buffer)) != -1) {
                        total += count;
                        if (total > 5 * 1024 * 1024) throw new IllegalArgumentException("DOCX XML part exceeds the import limit");
                        out.write(buffer, 0, count);
                    }
                    if ("word/document.xml".equals(entry.getName())) documentXml = out.toByteArray();
                    else numberingXml = out.toByteArray();
                }
            }
        }
        if (documentXml == null) throw new IllegalArgumentException("DOCX document.xml was not found");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        org.w3c.dom.Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(documentXml));
        String wordNamespace = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
        Map<String, String> numToAbstract = new HashMap<>();
        Map<String, Map<Integer, String[]>> abstractLevels = new HashMap<>();
        if (numberingXml != null) {
            org.w3c.dom.Document numberingDocument = factory.newDocumentBuilder().parse(new ByteArrayInputStream(numberingXml));
            NodeList nums = numberingDocument.getElementsByTagNameNS(wordNamespace, "num");
            for (int i = 0; i < nums.getLength(); i++) {
                Node num = nums.item(i);
                String numId = wordAttribute(num, "numId");
                Node child = num.getFirstChild();
                while (child != null) {
                    if (child.getNodeType() == Node.ELEMENT_NODE && "abstractNumId".equals(child.getLocalName())) {
                        numToAbstract.put(numId, wordAttribute(child, "val"));
                    }
                    child = child.getNextSibling();
                }
            }
            NodeList abstracts = numberingDocument.getElementsByTagNameNS(wordNamespace, "abstractNum");
            for (int i = 0; i < abstracts.getLength(); i++) {
                Node abstractNum = abstracts.item(i);
                String abstractId = wordAttribute(abstractNum, "abstractNumId");
                Map<Integer, String[]> levels = new HashMap<>();
                Node level = abstractNum.getFirstChild();
                while (level != null) {
                    if (level.getNodeType() == Node.ELEMENT_NODE && "lvl".equals(level.getLocalName())) {
                        int levelIndex = 0;
                        try { levelIndex = Integer.parseInt(wordAttribute(level, "ilvl")); } catch (NumberFormatException ignored) { }
                        String format = "decimal";
                        String levelText = "";
                        Node property = level.getFirstChild();
                        while (property != null) {
                            if (property.getNodeType() == Node.ELEMENT_NODE && "numFmt".equals(property.getLocalName())) format = wordAttribute(property, "val");
                            if (property.getNodeType() == Node.ELEMENT_NODE && "lvlText".equals(property.getLocalName())) levelText = wordAttribute(property, "val");
                            property = property.getNextSibling();
                        }
                        levels.put(levelIndex, new String[]{format, levelText});
                    }
                    level = level.getNextSibling();
                }
                abstractLevels.put(abstractId, levels);
            }
        }
        Map<String, Integer> listCounters = new HashMap<>();
        NodeList paragraphs = document.getElementsByTagNameNS(wordNamespace, "p");
        SpannableStringBuilder richText = new SpannableStringBuilder();
        for (int i = 0; i < paragraphs.getLength(); i++) {
            Node paragraph = paragraphs.item(i);
            int paragraphStart = richText.length();
            richText.append(wordParagraphListPrefix(paragraph, numToAbstract, abstractLevels, listCounters));
            appendWordRichNode(paragraph, richText);
            int paragraphEnd = richText.length();
            String alignment = wordParagraphAlignment(paragraph);
            Layout.Alignment layout = "center".equals(alignment) ? Layout.Alignment.ALIGN_CENTER
                    : ("right".equals(alignment) || "end".equals(alignment)) ? Layout.Alignment.ALIGN_OPPOSITE
                    : "both".equals(alignment) || "distribute".equals(alignment) ? Layout.Alignment.ALIGN_NORMAL
                    : Layout.Alignment.ALIGN_NORMAL;
            if (paragraphEnd > paragraphStart && !"left".equals(alignment) && !"start".equals(alignment)) {
                richText.setSpan(new AlignmentSpan.Standard(layout), paragraphStart, paragraphEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (i < paragraphs.getLength() - 1) appendDocxParagraphSeparator(paragraph, paragraphs.item(i + 1), richText);
        }
        return richText;
    }

    private Node findWordAncestor(Node node, String localName) {
        Node current = node;
        while (current != null) {
            if (current.getNodeType() == Node.ELEMENT_NODE && localName.equals(current.getLocalName())) return current;
            current = current.getParentNode();
        }
        return null;
    }

    private void appendDocxParagraphSeparator(Node currentParagraph, Node nextParagraph, SpannableStringBuilder out) {
        Node currentCell = findWordAncestor(currentParagraph, "tc");
        Node nextCell = findWordAncestor(nextParagraph, "tc");
        Node currentRow = findWordAncestor(currentParagraph, "tr");
        Node nextRow = findWordAncestor(nextParagraph, "tr");
        if (currentCell != null && currentCell == nextCell) {
            out.append('\n');
        } else if (currentRow != null && currentRow == nextRow && currentCell != nextCell) {
            out.append('\t');
        } else {
            out.append('\n');
        }
    }

    private String wordParagraphListPrefix(Node paragraph, Map<String, String> numToAbstract,
                                            Map<String, Map<Integer, String[]>> abstractLevels,
                                            Map<String, Integer> counters) {
        Node paragraphProperties = null;
        Node child = paragraph.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE && "pPr".equals(child.getLocalName())) {
                paragraphProperties = child;
                break;
            }
            child = child.getNextSibling();
        }
        if (paragraphProperties == null) return "";
        Node numPr = paragraphProperties.getFirstChild();
        while (numPr != null && !(numPr.getNodeType() == Node.ELEMENT_NODE && "numPr".equals(numPr.getLocalName()))) numPr = numPr.getNextSibling();
        if (numPr == null) return "";
        String numId = "";
        int level = 0;
        Node property = numPr.getFirstChild();
        while (property != null) {
            if (property.getNodeType() == Node.ELEMENT_NODE && "numId".equals(property.getLocalName())) numId = wordAttribute(property, "val");
            if (property.getNodeType() == Node.ELEMENT_NODE && "ilvl".equals(property.getLocalName())) {
                try { level = Integer.parseInt(wordAttribute(property, "val")); } catch (NumberFormatException ignored) { }
            }
            property = property.getNextSibling();
        }
        if (numId.isEmpty()) return "";
        String abstractId = numToAbstract.get(numId);
        String[] levelInfo = abstractId == null || abstractLevels.get(abstractId) == null
                ? null : abstractLevels.get(abstractId).get(level);
        String format = levelInfo == null ? "decimal" : levelInfo[0];
        String marker = levelInfo == null ? "" : levelInfo[1];
        String counterKey = numId + ":" + level;
        if ("bullet".equalsIgnoreCase(format)) return (marker.isEmpty() ? "•" : marker) + " ";
        int next = counters.containsKey(counterKey) ? counters.get(counterKey) + 1 : 1;
        counters.put(counterKey, next);
        if (marker.isEmpty()) marker = "%1.";
        marker = marker.replace("%1", String.valueOf(next));
        if (marker.contains("%")) marker = marker.replaceAll("%[0-9]+", String.valueOf(next));
        return marker + " ";
    }

    private String wordParagraphAlignment(Node paragraph) {
        Node child = paragraph.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE && "pPr".equals(child.getLocalName())) {
                Node property = child.getFirstChild();
                while (property != null) {
                    if (property.getNodeType() == Node.ELEMENT_NODE && "jc".equals(property.getLocalName())) {
                        return wordAttribute(property, "val").toLowerCase(Locale.ROOT);
                    }
                    property = property.getNextSibling();
                }
            }
            child = child.getNextSibling();
        }
        return "left";
    }

    private void appendWordRichNode(Node node, SpannableStringBuilder out) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String local = node.getLocalName();
            if ("t".equals(local)) { out.append(node.getTextContent()); return; }
            if ("tab".equals(local)) { out.append('\t'); return; }
            if ("br".equals(local) || "cr".equals(local)) { out.append('\n'); return; }
            if ("r".equals(local)) {
                int runStart = out.length();
                Node child = node.getFirstChild();
                while (child != null) {
                    if (!(child.getNodeType() == Node.ELEMENT_NODE && "rPr".equals(child.getLocalName()))) {
                        appendWordRichNode(child, out);
                    }
                    child = child.getNextSibling();
                }
                applyWordRunProperties(node, out, runStart, out.length());
                return;
            }
        }
        Node child = node.getFirstChild();
        while (child != null) {
            appendWordRichNode(child, out);
            child = child.getNextSibling();
        }
    }

    private String wordAttribute(Node node, String name) {
        if (node == null || node.getAttributes() == null) return "";
        Node value = node.getAttributes().getNamedItemNS("http://schemas.openxmlformats.org/wordprocessingml/2006/main", name);
        if (value == null) value = node.getAttributes().getNamedItem("w:" + name);
        if (value == null) value = node.getAttributes().getNamedItem(name);
        return value == null ? "" : value.getNodeValue();
    }

    private void applyWordRunProperties(Node run, SpannableStringBuilder out, int start, int end) {
        if (end <= start) return;
        Node runProperties = null;
        Node child = run.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE && "rPr".equals(child.getLocalName())) {
                runProperties = child;
                break;
            }
            child = child.getNextSibling();
        }
        if (runProperties == null) return;
        child = runProperties.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String local = child.getLocalName();
                if ("b".equals(local)) out.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                else if ("i".equals(local)) out.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                else if ("u".equals(local)) out.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                else if ("strike".equals(local) || "dstrike".equals(local)) out.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                else if ("color".equals(local)) {
                    String hex = wordAttribute(child, "val");
                    try { if (hex.matches("(?i)[0-9a-f]{6}")) out.setSpan(new ForegroundColorSpan(0xFF000000 | Integer.parseInt(hex, 16)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); }
                    catch (NumberFormatException ignored) { }
                } else if ("highlight".equals(local)) {
                    String color = wordAttribute(child, "val");
                    int parsed = "yellow".equalsIgnoreCase(color) ? 0xFFFFFF00
                            : "green".equalsIgnoreCase(color) ? 0xFF00FF00
                            : "cyan".equalsIgnoreCase(color) ? 0xFF00FFFF
                            : "magenta".equalsIgnoreCase(color) ? 0xFFFF00FF
                            : "red".equalsIgnoreCase(color) ? 0xFFFF0000
                            : "blue".equalsIgnoreCase(color) ? 0xFF0000FF
                            : "none".equalsIgnoreCase(color) ? 0 : 0xFFFFFF00;
                    if (parsed != 0) out.setSpan(new BackgroundColorSpan(parsed), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if ("sz".equals(local)) {
                    String halfPoints = wordAttribute(child, "val");
                    try {
                        float ratio = Float.parseFloat(halfPoints) / 24f;
                        if (ratio > 0.2f && ratio < 6f) out.setSpan(new RelativeSizeSpan(ratio), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } catch (NumberFormatException ignored) { }
                }
            }
            child = child.getNextSibling();
        }
    }

    private void appendWordParagraph(Node node, StringBuilder out) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String local = node.getLocalName();
            if ("t".equals(local)) {
                out.append(node.getTextContent());
                return;
            }
            if ("tab".equals(local)) {
                out.append('\t');
                return;
            }
            if ("br".equals(local) || "cr".equals(local)) {
                out.append('\n');
                return;
            }
        }
        Node child = node.getFirstChild();
        while (child != null) {
            appendWordParagraph(child, out);
            child = child.getNextSibling();
        }
    }

    private String readRtfText(Uri uri) throws Exception {
        return readRtfRichText(uri).toString();
    }

    private void appendRtfText(SpannableStringBuilder output, String value, RtfStyleState style) {
        if (value == null || value.isEmpty()) return;
        int start = output.length();
        output.append(value);
        int end = output.length();
        if (style.bold) output.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (style.italic) output.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (style.underline) output.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (style.strike) output.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (Math.abs(style.sizeRatio - 1.0f) > 0.01f) output.setSpan(new RelativeSizeSpan(style.sizeRatio), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private SpannableStringBuilder readRtfRichText(Uri uri) throws Exception {
        if (sizeOf(uri) > 10L * 1024L * 1024L) throw new IllegalArgumentException("RTF exceeds the 10 MiB import limit");
        byte[] bytes;
        try (InputStream in = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (in == null) throw new IllegalStateException("RTF source unavailable");
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) != -1) {
                if (out.size() + count > 10 * 1024 * 1024) throw new IllegalArgumentException("RTF exceeds the import limit");
                out.write(buffer, 0, count);
            }
            bytes = out.toByteArray();
        }
        String input = new String(bytes, "ISO-8859-1");
        SpannableStringBuilder output = new SpannableStringBuilder();
        ArrayList<Boolean> skipStack = new ArrayList<>();
        ArrayList<RtfStyleState> styleStack = new ArrayList<>();
        RtfStyleState style = new RtfStyleState();
        boolean skipGroup = false;
        int unicodeFallback = 1;
        int skipFallback = 0;
        for (int i = 0; i < input.length();) {
            char ch = input.charAt(i);
            if (ch == '{') {
                skipStack.add(skipGroup);
                styleStack.add(style.copy());
                int probe = i + 1;
                if (probe < input.length() && input.charAt(probe) == '\\') {
                    probe++;
                    if (probe < input.length() && input.charAt(probe) == '*') probe++;
                    if (probe < input.length() && input.charAt(probe) == '\\') probe++;
                    int wordStart = probe;
                    while (probe < input.length() && Character.isLetter(input.charAt(probe))) probe++;
                    String destination = input.substring(wordStart, probe).toLowerCase(Locale.ROOT);
                    if (destination.equals("fonttbl") || destination.equals("colortbl") || destination.equals("stylesheet")
                            || destination.equals("info") || destination.equals("pict") || destination.equals("object")
                            || destination.equals("header") || destination.equals("footer") || destination.equals("generator")
                            || destination.equals("listtable") || destination.equals("listoverridetable")
                            || destination.equals("themedata") || destination.equals("datastore") || destination.equals("xmlnstbl")) {
                        skipGroup = true;
                    }
                }
                i++;
                continue;
            }
            if (ch == '}') {
                if (!skipStack.isEmpty()) skipGroup = skipStack.remove(skipStack.size() - 1);
                if (!styleStack.isEmpty()) style = styleStack.remove(styleStack.size() - 1);
                i++;
                continue;
            }
            if (skipGroup) { i++; continue; }
            if (skipFallback > 0) {
                if (ch == '\\' && i + 1 < input.length() && input.charAt(i + 1) == '\'') i = Math.min(input.length(), i + 4);
                else if (ch == '\\' && i + 1 < input.length()) i += 2;
                else i++;
                skipFallback--;
                continue;
            }
            if (ch != '\\') {
                if (ch != '\r' && ch != '\n') appendRtfText(output, String.valueOf(ch), style);
                i++;
                continue;
            }
            i++;
            if (i >= input.length()) break;
            char control = input.charAt(i);
            if (!Character.isLetter(control)) {
                if (control == '\\' || control == '{' || control == '}') appendRtfText(output, String.valueOf(control), style);
                else if (control == '~') appendRtfText(output, "\u00A0", style);
                else if (control == '_') appendRtfText(output, "\u2011", style);
                else if (control == '-') { /* optional hyphen: omit */ }
                else if (control == '\'') {
                    if (i + 2 < input.length()) {
                        try { appendRtfText(output, String.valueOf((char) Integer.parseInt(input.substring(i + 1, i + 3), 16)), style); }
                        catch (NumberFormatException ignored) { }
                        i += 2;
                    }
                }
                i++;
                continue;
            }
            int wordStart = i;
            while (i < input.length() && Character.isLetter(input.charAt(i))) i++;
            String word = input.substring(wordStart, i);
            boolean negative = i < input.length() && input.charAt(i) == '-';
            if (negative) i++;
            int numberStart = i;
            while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
            Integer parameter = null;
            if (i > numberStart) {
                try { parameter = Integer.parseInt((negative ? "-" : "") + input.substring(numberStart, i)); }
                catch (NumberFormatException ignored) { }
            }
            if (i < input.length() && input.charAt(i) == ' ') i++;
            if ("par".equals(word) || "line".equals(word)) appendRtfText(output, "\n", style);
            else if ("tab".equals(word)) appendRtfText(output, "\t", style);
            else if ("emdash".equals(word)) appendRtfText(output, "\u2014", style);
            else if ("endash".equals(word)) appendRtfText(output, "\u2013", style);
            else if ("bullet".equals(word)) appendRtfText(output, "\u2022", style);
            else if ("lquote".equals(word)) appendRtfText(output, "\u2018", style);
            else if ("rquote".equals(word)) appendRtfText(output, "\u2019", style);
            else if ("ldblquote".equals(word)) appendRtfText(output, "\u201C", style);
            else if ("rdblquote".equals(word)) appendRtfText(output, "\u201D", style);
            else if ("b".equals(word)) style.bold = parameter == null || parameter != 0;
            else if ("i".equals(word)) style.italic = parameter == null || parameter != 0;
            else if ("ul".equals(word) || "uld".equals(word) || "ulw".equals(word)) style.underline = parameter == null || parameter != 0;
            else if ("ulnone".equals(word)) style.underline = false;
            else if ("strike".equals(word) || "striked".equals(word)) style.strike = parameter == null || parameter != 0;
            else if ("fs".equals(word) && parameter != null) {
                float ratio = parameter / 24f;
                if (ratio > 0.2f && ratio < 6f) style.sizeRatio = ratio;
            } else if ("plain".equals(word)) style.reset();
            else if ("uc".equals(word) && parameter != null) unicodeFallback = Math.max(0, Math.min(8, parameter));
            else if ("u".equals(word) && parameter != null) {
                int code = parameter < 0 ? parameter + 65536 : parameter;
                appendRtfText(output, String.valueOf((char) code), style);
                skipFallback = unicodeFallback;
            }
        }
        return output;
    }

    private void appendRtfEscaped(StringBuilder rtf, String text) {
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\\') rtf.append("\\\\");
            else if (ch == '{') rtf.append("\\{");
            else if (ch == '}') rtf.append("\\}");
            else if (ch == '\n' || ch == '\r') {
                if (ch == '\n') rtf.append("\\par\n");
            } else if (ch == '\t') rtf.append("\\tab ");
            else if (ch >= 32 && ch <= 126) rtf.append(ch);
            else rtf.append("\\u").append((short) ch).append('?');
        }
    }

    private String escapeRtfText(String text) {
        StringBuilder rtf = new StringBuilder("{\\rtf1\\ansi\\deff0\\uc1\n");
        appendRtfEscaped(rtf, text);
        return rtf.append('}').toString();
    }

    private String rtfPropertiesAt(Spanned richText, int position) {
        boolean bold = false, italic = false, underline = false, strike = false;
        Float sizeChange = null;
        Object[] spans = richText.getSpans(position, Math.min(richText.length(), position + 1), Object.class);
        for (Object span : spans) {
            if (span instanceof StyleSpan) {
                int style = ((StyleSpan) span).getStyle();
                if ((style & Typeface.BOLD) != 0) bold = true;
                if ((style & Typeface.ITALIC) != 0) italic = true;
            } else if (span instanceof UnderlineSpan) underline = true;
            else if (span instanceof StrikethroughSpan) strike = true;
            else if (span instanceof RelativeSizeSpan) sizeChange = ((RelativeSizeSpan) span).getSizeChange();
        }
        StringBuilder props = new StringBuilder();
        if (bold) props.append("\\b ");
        if (italic) props.append("\\i ");
        if (underline) props.append("\\ul ");
        if (strike) props.append("\\strike ");
        if (sizeChange != null) props.append("\\fs").append(Math.max(4, Math.min(144, Math.round(24f * sizeChange)))).append(' ');
        return props.toString();
    }

    private String escapeRtfRichText(Spanned richText) {
        String plain = richText.toString();
        StringBuilder rtf = new StringBuilder("{\\rtf1\\ansi\\deff0\\uc1\n");
        int position = 0;
        while (position < plain.length()) {
            String properties = rtfPropertiesAt(richText, position);
            int start = position;
            while (position < plain.length() && properties.equals(rtfPropertiesAt(richText, position))) {
                position += Character.charCount(plain.codePointAt(position));
            }
            rtf.append('{').append(properties);
            appendRtfEscaped(rtf, plain.substring(start, position));
            rtf.append('}');
        }
        return rtf.append('}').toString();
    }

    private void startRtfExport(String suggestedName, String text) {
        startRtfExport(suggestedName, new SpannableString(text));
    }

    private void startRtfExport(String suggestedName, Spanned richText) {
        pendingRtfExport = escapeRtfRichText(richText);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/rtf");
        intent.putExtra(Intent.EXTRA_TITLE, suggestedName.toLowerCase(Locale.ROOT).endsWith(".rtf") ? suggestedName : suggestedName + ".rtf");
        try { startActivityForResult(intent, SAVE_RTF); }
        catch (Exception e) {
            pendingRtfExport = null;
            Toast.makeText(this, "Could not open the Save RTF picker.", Toast.LENGTH_LONG).show();
        }
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
        setAppContentView(scroll, "text");
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
        setAppContentView(scroll, "details");
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

    private void showFavorites() {
        LinearLayout content = column();
        TextView back = label("‹  Back to MyOffice", 16, true);
        back.setOnClickListener(v -> showHome());
        content.addView(back);
        content.addView(label("★ Favorite files", 25, true));
        content.addView(label("Your saved shortcuts to documents. Favorites stay on this device.", 13, false));
        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setHint("Search favorites by filename");
        content.addView(search);
        LinearLayout results = new LinearLayout(this);
        results.setOrientation(LinearLayout.VERTICAL);
        content.addView(results);
        Runnable[] render = new Runnable[1];
        render[0] = () -> {
            results.removeAllViews();
            String query = search.getText().toString().trim().toLowerCase(Locale.ROOT);
            ArrayList<String> ordered = new ArrayList<>(favoriteUris);
            Collections.reverse(ordered);
            int shown = 0;
            for (String value : ordered) {
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
                results.addView(card, params);
                card.addView(label(name, 16, true));
                card.addView(label(mime + "  •  " + sizeText(sizeOf(uri)), 12, false));
                card.addView(action("Open favorite", v -> {
                    recentUris.remove(value);
                    recentUris.add(value);
                    while (recentUris.size() > MAX_RECENTS) recentUris.remove(0);
                    saveRecents();
                    openSelected(uri);
                }));
                card.addView(action("Remove favorite", v -> {
                    favoriteUris.remove(value);
                    saveFavorites();
                    render[0].run();
                }));
            }
            if (shown == 0) {
                results.addView(label(query.isEmpty()
                        ? "No favorites yet. Star a file from Recent files to save it here."
                        : "No favorites match your search.", 14, false));
            }
        };
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { render[0].run(); }
            @Override public void afterTextChanged(Editable s) { }
        });
        render[0].run();
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setAppContentView(scroll, "favorites");
    }

    private void showNotes() {
        noteEditorBackHandler = null;
        LinearLayout content = column();
        TextView back = label("‹  Back to MyOffice", 16, true);
        content.addView(back);
        back.setOnClickListener(v -> showHome());
        content.addView(label("My notes", 26, true));
        content.addView(label("Saved locally on this device. Notes remain available offline.", 14, false));
        content.addView(action("＋  Create a note", v -> showNoteEditor(null, "", "")));
        EditText noteSearch = new EditText(this);
        noteSearch.setSingleLine(true);
        noteSearch.setHint("Search notes by title or content");
        content.addView(noteSearch);
        LinearLayout noteResults = new LinearLayout(this);
        noteResults.setOrientation(LinearLayout.VERTICAL);
        Button noteSortButton = action("Sort: Modified newest", v -> {
            noteSortMode = (noteSortMode + 1) % 2;
            noteSortButtonLabel(v, noteSortMode);
            renderNoteResults(noteResults, noteSearch.getText().toString());
        });
        content.addView(noteSortButton);
        content.addView(noteResults);
        noteSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderNoteResults(noteResults, s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });
        renderNoteResults(noteResults, "");

        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setAppContentView(scroll, "notes-list");
    }

    private void noteSortButtonLabel(View view, int mode) {
        if (view instanceof Button) ((Button) view).setText(mode == 0 ? "Sort: Modified newest" : "Sort: Title A–Z");
    }

    private String noteTitleForSort(File file) {
        try {
            String[] data = readNote(file);
            return data[0].isEmpty() ? "Untitled note" : data[0];
        } catch (Exception e) {
            return file.getName();
        }
    }

    private void duplicateNote(File source) {
        try {
            String[] data = readNote(source);
            String originalTitle = data[0].trim().isEmpty() ? "Untitled note" : data[0].trim();
            String copyTitle = originalTitle + " (copy)";
            File target = new File(notesDirectory(), "note_" + System.currentTimeMillis() + ".note");
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
                writer.write(NOTE_PREFIX + copyTitle + "\\n\\n");
                writer.write(data[1]);
                writer.flush();
            }
            Toast.makeText(this, "Note duplicated.", Toast.LENGTH_SHORT).show();
            showNotes();
        } catch (Exception e) {
            Toast.makeText(this, "Could not duplicate this note.", Toast.LENGTH_LONG).show();
        }
    }

    private void renderNoteResults(LinearLayout results, String queryText) {
        results.removeAllViews();
        File[] files = noteFiles();
        if (noteSortMode == 1) {
            Arrays.sort(files, (left, right) -> noteTitleForSort(left).compareToIgnoreCase(noteTitleForSort(right)));
        } else {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        }
        String query = queryText == null ? "" : queryText.trim().toLowerCase(Locale.ROOT);
        int shown = 0;
        for (File file : files) {
            String[] data;
            try { data = readNote(file); }
            catch (Exception e) { data = new String[]{file.getName(), "Could not read this note"}; }
            String title = data[0].isEmpty() ? "Untitled note" : data[0];
            String preview = data[1].replace('\n', ' ').trim();
            String searchable = (title + " " + preview + " " + file.getName()).toLowerCase(Locale.ROOT);
            if (!query.isEmpty() && !searchable.contains(query)) continue;
            shown++;
            if (preview.length() > 100) preview = preview.substring(0, 100) + "…";
            final File selected = file;
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(12), dp(8), dp(12), dp(8));
            card.setBackgroundColor(0xFFFFFFFF);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.setMargins(0, dp(5), 0, dp(5));
            results.addView(card, params);
            card.addView(label(title, 17, true));
            card.addView(label(preview.isEmpty() ? "Empty note" : preview, 13, false));
            card.addView(label("Modified " + DateFormat.getDateTimeInstance().format(new Date(file.lastModified())), 11, false));
            card.addView(action("Duplicate note", v -> duplicateNote(selected)));
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
        if (shown == 0) results.addView(label(query.isEmpty()
                ? "No notes yet. Create a note to start learning or writing."
                : "No notes match your search.", 15, false));
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
        content.addView(action("Share note", v -> {
            String noteTitle = title.getText().toString().trim();
            if (noteTitle.isEmpty()) noteTitle = "Untitled note";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, noteTitle);
            share.putExtra(Intent.EXTRA_TEXT, noteTitle + "\\n\\n" + body.getText().toString());
            try { startActivity(Intent.createChooser(share, "Share note")); }
            catch (Exception e) { Toast.makeText(this, "No compatible sharing app was found.", Toast.LENGTH_LONG).show(); }
        }));
        content.addView(action("Discard changes", v -> new AlertDialog.Builder(this)
                .setTitle("Discard this edit?")
                .setMessage("Unsaved changes will be lost.")
                .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Discard", (dialog, which) -> showNotes())
                .show()));
        noteEditorBackHandler = () -> {
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
        };
        back.setOnClickListener(v -> noteEditorBackHandler.run());
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setAppContentView(scroll, "note-editor");
    }


    // Basic local PDF reader using Android's built-in PdfRenderer. It does not upload the document.
    private boolean containsUri(ArrayList<Uri> uris, Uri target) {
        for (Uri uri : uris) if (uri.toString().equals(target.toString())) return true;
        return false;
    }

    private void persistReadPermission(Intent data, Uri uri) {
        try {
            int flags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri, flags);
        } catch (SecurityException ignored) { }
    }

    private void openPdfMergePicker(Uri currentSource, String currentName) {
        pendingPdfMergeSources = new ArrayList<>();
        pendingPdfMergeSources.add(currentSource);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try { startActivityForResult(intent, PICK_PDFS); }
        catch (Exception e) {
            pendingPdfMergeSources = null;
            Toast.makeText(this, "Could not open the PDF picker.", Toast.LENGTH_LONG).show();
        }
    }

    private void createMergedPdfAsync(ArrayList<Uri> sources, String sourceName) {
        Toast.makeText(this, "Preparing merged PDF on this device…", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            File temp = null;
            try {
                temp = File.createTempFile("myoffice_merged_", ".pdf", getCacheDir());
                try (FileOutputStream stream = new FileOutputStream(temp)) {
                    PdfDocument output = new PdfDocument();
                    try {
                        int outputPageNumber = 0;
                        for (Uri source : sources) {
                            try (ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(source, "r")) {
                                if (descriptor == null) throw new IllegalStateException("A PDF source is unavailable");
                                PdfRenderer renderer = new PdfRenderer(descriptor);
                                try {
                                    for (int i = 0; i < renderer.getPageCount(); i++) {
                                        if (outputPageNumber >= 500) throw new IllegalArgumentException("Merged PDF would exceed 500 pages");
                                        try (PdfRenderer.Page page = renderer.openPage(i)) {
                                            float scale = Math.min(1.25f, Math.min(1400f / page.getWidth(), 1800f / page.getHeight()));
                                            int width = Math.max(1, Math.round(page.getWidth() * scale));
                                            int height = Math.max(1, Math.round(page.getHeight() * scale));
                                            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                                            outputPageNumber++;
                                            PdfDocument.Page outputPage = output.startPage(
                                                    new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), outputPageNumber).create());
                                            outputPage.getCanvas().drawBitmap(bitmap, 0f, 0f, null);
                                            output.finishPage(outputPage);
                                            bitmap.recycle();
                                        }
                                    }
                                } finally {
                                    renderer.close();
                                }
                            }
                        }
                        if (outputPageNumber == 0) throw new IllegalArgumentException("No readable pages were found");
                        output.writeTo(stream);
                        stream.flush();
                    } finally {
                        output.close();
                    }
                }
                File ready = temp;
                runOnUiThread(() -> {
                    if (isFinishing()) { ready.delete(); return; }
                    pendingPdfFile = ready;
                    Intent create = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    create.addCategory(Intent.CATEGORY_OPENABLE);
                    create.setType("application/pdf");
                    String base = sourceName.toLowerCase(Locale.ROOT).endsWith(".pdf")
                            ? sourceName.substring(0, sourceName.length() - 4) : sourceName;
                    create.putExtra(Intent.EXTRA_TITLE, base + "_merged.pdf");
                    try { startActivityForResult(create, SAVE_PDF); }
                    catch (Exception e) {
                        pendingPdfFile = null;
                        ready.delete();
                        Toast.makeText(this, "Could not open the Save PDF picker.", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                if (temp != null) temp.delete();
                runOnUiThread(() -> Toast.makeText(this,
                        "Could not merge PDFs. Check that each source is readable and unencrypted.", Toast.LENGTH_LONG).show());
            }
        }, "MyOffice-PDF-merge").start();
    }

    private void startPdfCopy(Uri source, String name) {
        pendingPdfCopySource = source;
        String suggested = name.toLowerCase(Locale.ROOT).endsWith(".pdf") ? name : name + ".pdf";
        Intent create = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        create.addCategory(Intent.CATEGORY_OPENABLE);
        create.setType("application/pdf");
        create.putExtra(Intent.EXTRA_TITLE, suggested);
        try { startActivityForResult(create, SAVE_PDF_COPY); }
        catch (Exception e) {
            pendingPdfCopySource = null;
            Toast.makeText(this, "Could not open the Save PDF picker.", Toast.LENGTH_LONG).show();
        }
    }

    // Export only the currently selected PDF page as a PNG image, honoring viewer rotation.
    // Rendering is local and performed off the UI thread.
    private void exportPdfPageAsPng(Uri source, String sourceName, int pageIndex, int rotationDegrees) {
        Toast.makeText(this, "Preparing page image on this device…", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            File temp = null;
            try (ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(source, "r")) {
                if (descriptor == null) throw new IllegalStateException("PDF source is unavailable");
                PdfRenderer renderer = new PdfRenderer(descriptor);
                try {
                    if (pageIndex < 0 || pageIndex >= renderer.getPageCount()) {
                        throw new IllegalArgumentException("Page is outside this PDF");
                    }
                    try (PdfRenderer.Page page = renderer.openPage(pageIndex)) {
                        float scale = Math.min(2.0f, Math.min(1800f / page.getWidth(), 1800f / page.getHeight()));
                        int width = Math.max(1, Math.round(page.getWidth() * scale));
                        int height = Math.max(1, Math.round(page.getHeight() * scale));
                        Bitmap rendered = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        rendered.eraseColor(0xFFFFFFFF);
                        page.render(rendered, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                        Bitmap output = rendered;
                        int normalizedRotation = ((rotationDegrees % 360) + 360) % 360;
                        if (normalizedRotation != 0) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(normalizedRotation);
                            output = Bitmap.createBitmap(rendered, 0, 0, rendered.getWidth(), rendered.getHeight(), matrix, true);
                            if (output != rendered) rendered.recycle();
                        }
                        temp = File.createTempFile("myoffice_page_", ".png", getCacheDir());
                        try (FileOutputStream stream = new FileOutputStream(temp)) {
                            if (!output.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                                throw new IllegalStateException("PNG encoding failed");
                            }
                            stream.flush();
                        } finally {
                            output.recycle();
                        }
                    }
                } finally {
                    renderer.close();
                }
                File ready = temp;
                runOnUiThread(() -> {
                    if (isFinishing()) { ready.delete(); return; }
                    pendingPngFile = ready;
                    String base = sourceName.toLowerCase(Locale.ROOT).endsWith(".pdf")
                            ? sourceName.substring(0, sourceName.length() - 4) : sourceName;
                    Intent create = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    create.addCategory(Intent.CATEGORY_OPENABLE);
                    create.setType("image/png");
                    create.putExtra(Intent.EXTRA_TITLE, safeTextFilename(base) + "_page_" + (pageIndex + 1) + ".png");
                    try { startActivityForResult(create, SAVE_PNG); }
                    catch (Exception e) {
                        pendingPngFile = null;
                        ready.delete();
                        Toast.makeText(this, "Could not open the Save image picker.", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                if (temp != null) temp.delete();
                runOnUiThread(() -> Toast.makeText(this,
                        "Could not export this PDF page. Check that the page is readable and try again.",
                        Toast.LENGTH_LONG).show());
            }
        }, "MyOffice-PDF-page-PNG").start();
    }

    // Exports a rasterized copy so the chosen viewing rotation is baked into every page.
    // The source PDF is never modified; text selection/search in the output is not preserved.
    private void exportEditedPdfCopy(Uri source, String name, int rotationDegrees, String watermarkText) {
        exportEditedPdfCopy(source, name, rotationDegrees, watermarkText, 1, Integer.MAX_VALUE);
    }

    private void exportEditedPdfCopy(Uri source, String name, int rotationDegrees, String watermarkText,
                                     int requestedStartPage, int requestedEndPage) {
        exportEditedPdfCopy(source, name, rotationDegrees, watermarkText, requestedStartPage, requestedEndPage, -1);
    }

    private void exportEditedPdfCopy(Uri source, String name, int rotationDegrees, String watermarkText,
                                     int requestedStartPage, int requestedEndPage, int annotationPageIndex) {
        String preparationMessage = annotationPageIndex >= 0 ? "Preparing annotated PDF copy…"
                : (requestedStartPage != 1 || requestedEndPage != Integer.MAX_VALUE
                    ? "Preparing extracted PDF pages…"
                    : (watermarkText == null || watermarkText.trim().isEmpty() ? "Preparing rotated PDF copy…" : "Preparing stamped PDF copy…"));
        Toast.makeText(this, preparationMessage, Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            File temp = null;
            try (ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(source, "r")) {
                if (descriptor == null) throw new IllegalStateException("PDF source is unavailable");
                temp = File.createTempFile("myoffice_rotated_", ".pdf", getCacheDir());
                try (PdfRenderer renderer = new PdfRenderer(descriptor);
                     FileOutputStream stream = new FileOutputStream(temp)) {
                    PdfDocument output = new PdfDocument();
                    try {
                        int count = renderer.getPageCount();
                        if (count <= 0) throw new IllegalStateException("PDF has no pages");
                        if (count > 500) throw new IllegalStateException("This PDF has over 500 pages; export a smaller document");
                        if (requestedStartPage < 1 || requestedEndPage < requestedStartPage || requestedStartPage > count) {
                            throw new IllegalArgumentException("Choose a valid page range within the PDF.");
                        }
                        int firstPageIndex = requestedStartPage - 1;
                        int lastPageExclusive = Math.min(count, requestedEndPage);
                        for (int i = firstPageIndex; i < lastPageExclusive; i++) {
                            try (PdfRenderer.Page page = renderer.openPage(i)) {
                                float scale = Math.min(1.5f, Math.min(1400f / page.getWidth(), 1800f / page.getHeight()));
                                int width = Math.max(1, Math.round(page.getWidth() * scale));
                                int height = Math.max(1, Math.round(page.getHeight() * scale));
                                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                                if (rotationDegrees != 0) {
                                    Matrix matrix = new Matrix();
                                    matrix.postRotate(rotationDegrees);
                                    Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                    if (rotated != bitmap) bitmap.recycle();
                                    bitmap = rotated;
                                }
                                PdfDocument.Page outputPage = output.startPage(
                                        new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), i - firstPageIndex + 1).create());
                                outputPage.getCanvas().drawBitmap(bitmap, 0f, 0f, null);
                                if (watermarkText != null && !watermarkText.trim().isEmpty() && (annotationPageIndex < 0 || i == annotationPageIndex)) {
                                    Paint stampPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                    boolean typedSignature = watermarkText.startsWith("SIGNATURE:");
                                    boolean typedNote = watermarkText.startsWith("NOTE:");
                                    boolean checkmark = watermarkText.startsWith("CHECKMARK:");
                                    boolean xmark = watermarkText.startsWith("XMARK:");
                                    String stampText = typedSignature ? watermarkText.substring("SIGNATURE:".length()).trim()
                                            : typedNote ? watermarkText.substring("NOTE:".length()).trim()
                                            : (checkmark || xmark) ? "" : watermarkText.trim();
                                    stampPaint.setColor(typedSignature ? 0xFF183A73 : (typedNote ? 0xFF202124 : checkmark ? 0xFF188038 : 0x99D32F2F));
                                    stampPaint.setTextSize(Math.max((typedSignature || typedNote) ? 22f : 26f,
                                            bitmap.getWidth() * ((typedSignature || typedNote) ? 0.035f : 0.045f)));
                                    stampPaint.setTextAlign((typedSignature || typedNote) ? Paint.Align.LEFT : Paint.Align.CENTER);
                                    stampPaint.setTypeface(Typeface.create(Typeface.DEFAULT, typedSignature ? Typeface.ITALIC : (typedNote ? Typeface.NORMAL : Typeface.BOLD)));
                                    if (checkmark || xmark) {
                                        Paint markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                        markPaint.setColor(checkmark ? 0xFF188038 : 0xFFD32F2F);
                                        markPaint.setStyle(Paint.Style.STROKE);
                                        markPaint.setStrokeCap(Paint.Cap.ROUND);
                                        markPaint.setStrokeJoin(Paint.Join.ROUND);
                                        markPaint.setStrokeWidth(Math.max(8f, bitmap.getWidth() * 0.012f));
                                        float x = bitmap.getWidth() * 0.72f;
                                        float y = bitmap.getHeight() * 0.78f;
                                        if (checkmark) {
                                            outputPage.getCanvas().drawLine(x, y, x + bitmap.getWidth() * 0.055f,
                                                    y + bitmap.getHeight() * 0.035f, markPaint);
                                            outputPage.getCanvas().drawLine(x + bitmap.getWidth() * 0.055f,
                                                    y + bitmap.getHeight() * 0.035f, x + bitmap.getWidth() * 0.16f,
                                                    y - bitmap.getHeight() * 0.10f, markPaint);
                                        } else {
                                            float size = bitmap.getWidth() * 0.11f;
                                            outputPage.getCanvas().drawLine(x, y - size, x + size, y, markPaint);
                                            outputPage.getCanvas().drawLine(x, y, x + size, y - size, markPaint);
                                        }
                                    } else if (typedSignature) {
                                        float left = bitmap.getWidth() * 0.10f;
                                        float baseline = bitmap.getHeight() * 0.88f;
                                        outputPage.getCanvas().drawText(stampText, left, baseline, stampPaint);
                                        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                        linePaint.setColor(0xFF183A73);
                                        linePaint.setStrokeWidth(2f);
                                        outputPage.getCanvas().drawLine(left, baseline + 7f, left + stampPaint.measureText(stampText), baseline + 7f, linePaint);
                                    } else if (typedNote) {
                                        float left = bitmap.getWidth() * 0.06f;
                                        float top = bitmap.getHeight() * 0.72f;
                                        float maxWidth = bitmap.getWidth() * 0.88f;
                                        float lineHeight = stampPaint.getTextSize() * 1.35f;
                                        ArrayList<String> lines = new ArrayList<>();
                                        for (String paragraph : stampText.split("\\n")) {
                                            StringBuilder line = new StringBuilder();
                                            for (String word : paragraph.split("\\s+")) {
                                                String candidate = line.length() == 0 ? word : line + " " + word;
                                                if (stampPaint.measureText(candidate) > maxWidth && line.length() > 0) {
                                                    lines.add(line.toString());
                                                    line.setLength(0);
                                                    line.append(word);
                                                } else {
                                                    if (line.length() > 0) line.append(' ');
                                                    line.append(word);
                                                }
                                            }
                                            if (line.length() > 0) lines.add(line.toString());
                                        }
                                        if (lines.size() > 5) lines = new ArrayList<>(lines.subList(0, 5));
                                        float boxHeight = lines.size() * lineHeight + 20f;
                                        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                                        backgroundPaint.setColor(0xE6FFF9C4);
                                        outputPage.getCanvas().drawRoundRect(left - 8f, top - 8f,
                                                left + maxWidth, top + boxHeight, 10f, 10f, backgroundPaint);
                                        float baseline = top + stampPaint.getTextSize();
                                        for (String line : lines) {
                                            String fitted = line;
                                            while (stampPaint.measureText(fitted) > maxWidth && fitted.length() > 1) {
                                                fitted = fitted.substring(0, fitted.length() - 1);
                                            }
                                            outputPage.getCanvas().drawText(fitted, left, baseline, stampPaint);
                                            baseline += lineHeight;
                                        }
                                    } else {
                                        outputPage.getCanvas().save();
                                        outputPage.getCanvas().rotate(-30f, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
                                        outputPage.getCanvas().drawText(stampText, bitmap.getWidth() / 2f,
                                                bitmap.getHeight() / 2f, stampPaint);
                                        outputPage.getCanvas().restore();
                                    }
                                }
                                output.finishPage(outputPage);
                                bitmap.recycle();
                            }
                        }
                        output.writeTo(stream);
                        stream.flush();
                    } finally {
                        output.close();
                    }
                }
                File ready = temp;
                runOnUiThread(() -> {
                    if (isFinishing()) { ready.delete(); return; }
                    pendingPdfFile = ready;
                    Intent create = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    create.addCategory(Intent.CATEGORY_OPENABLE);
                    create.setType("application/pdf");
                    String base = name.toLowerCase(Locale.ROOT).endsWith(".pdf")
                            ? name.substring(0, name.length() - 4) : name;
                    String suggestedSuffix;
                    if (annotationPageIndex >= 0) suggestedSuffix = "_annotated.pdf";
                    else if (watermarkText != null && !watermarkText.trim().isEmpty()) suggestedSuffix = "_stamped.pdf";
                    else if (firstPageForName(requestedStartPage, requestedEndPage)) suggestedSuffix = "_rotated.pdf";
                    else suggestedSuffix = "_pages_" + requestedStartPage + "-" + requestedEndPage + ".pdf";
                    create.putExtra(Intent.EXTRA_TITLE, base + suggestedSuffix);
                    try { startActivityForResult(create, SAVE_PDF); }
                    catch (Exception e) {
                        pendingPdfFile = null;
                        ready.delete();
                        Toast.makeText(this, "Could not open the Save PDF picker.", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                if (temp != null) temp.delete();
                runOnUiThread(() -> Toast.makeText(this,
                        "Could not export PDF. Check the page range and try a smaller, unencrypted PDF.", Toast.LENGTH_LONG).show());
            }
        }, "MyOffice-PDF-export").start();
    }

    private boolean firstPageForName(int requestedStartPage, int requestedEndPage) {
        return requestedStartPage == 1 && requestedEndPage == Integer.MAX_VALUE;
    }

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
        HorizontalScrollView pdfHorizontal = new HorizontalScrollView(this);
        pdfHorizontal.setFillViewport(true);
        pdfHorizontal.addView(pageImage, new HorizontalScrollView.LayoutParams(-2, -2));
        content.addView(pdfHorizontal, new LinearLayout.LayoutParams(-1, dp(520)));
        SharedPreferences pdfPositions = getSharedPreferences("myoffice_pdf_positions", MODE_PRIVATE);
        String pdfPositionKey = uri.toString();
        int[] pageIndex = new int[]{Math.max(0, pdfPositions.getInt(pdfPositionKey, 0))};
        SharedPreferences pdfRotations = getSharedPreferences("myoffice_pdf_rotations", MODE_PRIVATE);
        currentPdfRotationDegrees = pdfRotations.getInt(pdfPositionKey, 0);
        float[] zoomLevel = new float[]{1.0f};
        EditText pageInput = new EditText(this);
        pageInput.setSingleLine(true);
        pageInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        pageInput.setHint("Page number");
        pageInput.setText("1");
        LinearLayout pageJump = new LinearLayout(this);
        pageJump.setOrientation(LinearLayout.HORIZONTAL);
        pageJump.addView(pageInput, new LinearLayout.LayoutParams(0, -2, 1));
        pageJump.addView(action("Go to page", v -> {
            try {
                int requested = Integer.parseInt(pageInput.getText().toString().trim());
                if (requested < 1) throw new NumberFormatException();
                renderPdfPage(uri, requested - 1, pageImage, pageLabel, pageInput, previousRef[0], nextRef[0], pageIndex, zoomLevel);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a page number starting at 1.", Toast.LENGTH_SHORT).show();
            }
        }), new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(pageJump);
        LinearLayout bookmarkControls = new LinearLayout(this);
        bookmarkControls.setOrientation(LinearLayout.HORIZONTAL);
        bookmarkControls.addView(action("Bookmark current page", v -> togglePdfBookmark(uri, pageIndex[0])),
                new LinearLayout.LayoutParams(0, -2, 1));
        bookmarkControls.addView(action("Bookmarks", v -> showPdfBookmarks(uri, pageImage, pageLabel, pageInput,
                previousRef[0], nextRef[0], pageIndex, zoomLevel)),
                new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(bookmarkControls);
        content.addView(action("Page note", v -> showPdfPageNote(uri, pageIndex[0])));
        TextView zoomLabel = label("Zoom 100%", 12, false);
        LinearLayout zoomControls = new LinearLayout(this);
        zoomControls.setOrientation(LinearLayout.HORIZONTAL);
        zoomControls.addView(action("−", v -> {
            zoomLevel[0] = Math.max(0.75f, zoomLevel[0] - 0.25f);
            zoomLabel.setText("Zoom " + Math.round(zoomLevel[0] * 100) + "%");
            renderPdfPage(uri, pageIndex[0], pageImage, pageLabel, pageInput, previousRef[0], nextRef[0], pageIndex, zoomLevel);
        }), new LinearLayout.LayoutParams(0, -2, 1));
        zoomControls.addView(action("100%", v -> {
            zoomLevel[0] = 1.0f;
            zoomLabel.setText("Zoom 100%");
            renderPdfPage(uri, pageIndex[0], pageImage, pageLabel, pageInput, previousRef[0], nextRef[0], pageIndex, zoomLevel);
        }), new LinearLayout.LayoutParams(0, -2, 1));
        zoomControls.addView(action("+", v -> {
            zoomLevel[0] = Math.min(2.0f, zoomLevel[0] + 0.25f);
            zoomLabel.setText("Zoom " + Math.round(zoomLevel[0] * 100) + "%");
            renderPdfPage(uri, pageIndex[0], pageImage, pageLabel, pageInput, previousRef[0], nextRef[0], pageIndex, zoomLevel);
        }), new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(zoomControls);
        content.addView(zoomLabel);
        content.addView(action("Rotate page 90°", v -> {
            currentPdfRotationDegrees = (currentPdfRotationDegrees + 90) % 360;
            getSharedPreferences("myoffice_pdf_rotations", MODE_PRIVATE).edit()
                    .putInt(uri.toString(), currentPdfRotationDegrees).apply();
            renderPdfPage(uri, pageIndex[0], pageImage, pageLabel, pageInput, previousRef[0], nextRef[0], pageIndex, zoomLevel);
        }));
        content.addView(action("Reset rotation", v -> {
            currentPdfRotationDegrees = 0;
            getSharedPreferences("myoffice_pdf_rotations", MODE_PRIVATE).edit()
                    .putInt(uri.toString(), 0).apply();
            renderPdfPage(uri, pageIndex[0], pageImage, pageLabel, pageInput, previousRef[0], nextRef[0], pageIndex, zoomLevel);
            Toast.makeText(this, "PDF view reset to 0°.", Toast.LENGTH_SHORT).show();
        }));
        content.addView(action("Export rotated PDF copy", v -> exportEditedPdfCopy(uri, name, currentPdfRotationDegrees, "")));
        content.addView(action("Export current page as PNG", v -> exportPdfPageAsPng(uri, name, pageIndex[0], currentPdfRotationDegrees)));
        content.addView(action("Extract pages to new PDF", v -> {
            LinearLayout rangeFields = new LinearLayout(this);
            rangeFields.setOrientation(LinearLayout.VERTICAL);
            EditText startPageInput = new EditText(this);
            startPageInput.setSingleLine(true);
            startPageInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            startPageInput.setHint("First page (e.g. 1)");
            startPageInput.setText("1");
            rangeFields.addView(startPageInput);
            EditText endPageInput = new EditText(this);
            endPageInput.setSingleLine(true);
            endPageInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            endPageInput.setHint("Last page (inclusive)");
            rangeFields.addView(endPageInput);
            new AlertDialog.Builder(this)
                    .setTitle("Extract page range")
                    .setMessage("Creates a new flattened PDF copy and leaves the original unchanged.")
                    .setView(rangeFields)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Export pages", (dialog, which) -> {
                        try {
                            int first = Integer.parseInt(startPageInput.getText().toString().trim());
                            int last = Integer.parseInt(endPageInput.getText().toString().trim());
                            if (first < 1 || last < first) throw new NumberFormatException();
                            exportEditedPdfCopy(uri, name, currentPdfRotationDegrees, "", first, last);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Enter a valid first and last page number.", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }));
        content.addView(action("Add watermark / stamp", v -> {
            EditText stampInput = new EditText(this);
            stampInput.setSingleLine(true);
            stampInput.setHint("e.g. DRAFT, COPY, APPROVED");
            new AlertDialog.Builder(this)
                    .setTitle("Stamp every page")
                    .setMessage("Creates a new flattened PDF copy; the original remains unchanged.")
                    .setView(stampInput)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Export stamped copy", (dialog, which) -> {
                        String stamp = stampInput.getText().toString().trim();
                        if (stamp.isEmpty()) {
                            Toast.makeText(this, "Enter stamp text first.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        exportEditedPdfCopy(uri, name, currentPdfRotationDegrees, stamp);
                    }).show();
        }));
        content.addView(action("Add text to current page", v -> {
            EditText annotationInput = new EditText(this);
            annotationInput.setSingleLine(false);
            annotationInput.setMinLines(2);
            annotationInput.setHint("e.g. Reviewed on 26 Oct 2026");
            new AlertDialog.Builder(this)
                    .setTitle("Annotate page " + (pageIndex[0] + 1))
                    .setMessage("Text is permanently drawn onto a new flattened PDF copy. The original stays unchanged.")
                    .setView(annotationInput)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Export annotated copy", (dialog, which) -> {
                        String annotation = annotationInput.getText().toString().trim();
                        if (annotation.isEmpty()) {
                            Toast.makeText(this, "Enter annotation text first.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        exportEditedPdfCopy(uri, name, currentPdfRotationDegrees, "NOTE:" + annotation,
                                1, Integer.MAX_VALUE, pageIndex[0]);
                    }).show();
        }));
        content.addView(action("Fill & Sign · Typed signature", v -> {
            EditText signerName = new EditText(this);
            signerName.setSingleLine(true);
            signerName.setHint("Type your name as a signature");
            new AlertDialog.Builder(this)
                    .setTitle("Sign page " + (pageIndex[0] + 1))
                    .setMessage("Adds a typed signature to a new flattened PDF copy. This is not a certificate-based digital signature.")
                    .setView(signerName)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Create signed copy", (dialog, which) -> {
                        String signature = signerName.getText().toString().trim();
                        if (signature.isEmpty()) {
                            Toast.makeText(this, "Enter a signer name first.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        exportEditedPdfCopy(uri, name, currentPdfRotationDegrees, "SIGNATURE:" + signature,
                                1, Integer.MAX_VALUE, pageIndex[0]);
                    }).show();
        }));
        content.addView(action("Fill form · Add checkmark", v -> new AlertDialog.Builder(this)
                .setTitle("Add checkmark to page " + (pageIndex[0] + 1) + "?")
                .setMessage("Adds a green checkmark near the lower-right of the current page in a new flattened PDF copy. The original stays unchanged.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Create checked copy", (dialog, which) ->
                        exportEditedPdfCopy(uri, name, currentPdfRotationDegrees, "CHECKMARK:",
                                1, Integer.MAX_VALUE, pageIndex[0])).show()));
        content.addView(action("Fill form · Add X mark", v -> new AlertDialog.Builder(this)
                .setTitle("Add X mark to page " + (pageIndex[0] + 1) + "?")
                .setMessage("Adds a red X near the lower-right of the current page in a new flattened PDF copy. The original stays unchanged.")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Create marked copy", (dialog, which) ->
                        exportEditedPdfCopy(uri, name, currentPdfRotationDegrees, "XMARK:",
                                1, Integer.MAX_VALUE, pageIndex[0])).show()));
        LinearLayout navigation = new LinearLayout(this);
        navigation.setOrientation(LinearLayout.HORIZONTAL);
        Button previous = action("Previous page", v -> {
            if (pageIndex[0] > 0) {
                pageIndex[0]--;
                renderPdfPage(uri, pageIndex[0], pageImage, pageLabel, pageInput, previousRef[0], nextRef[0], pageIndex, zoomLevel);
            }
        });
        Button next = action("Next page", v -> {
            pageIndex[0]++;
            renderPdfPage(uri, pageIndex[0], pageImage, pageLabel, pageInput, previousRef[0], nextRef[0], pageIndex, zoomLevel);
        });
        previousRef[0] = previous;
        nextRef[0] = next;
        navigation.addView(previous, new LinearLayout.LayoutParams(0, -2, 1));
        navigation.addView(next, new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(navigation);
        content.addView(action("Save a copy", v -> startPdfCopy(uri, name)));
        content.addView(action("Merge with other PDFs", v -> openPdfMergePicker(uri, name)));
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
        setAppContentView(scroll, "pdf-reader");
        renderPdfPage(uri, pageIndex[0], pageImage, pageLabel, pageInput, previous, next, pageIndex, zoomLevel);
    }

    private final Button[] previousRef = new Button[1];
    private final Button[] nextRef = new Button[1];

    private void showPdfPageNote(Uri uri, int pageIndex) {
        SharedPreferences notes = getSharedPreferences("myoffice_pdf_page_notes", MODE_PRIVATE);
        String key = uri.toString() + "#" + pageIndex;
        EditText noteInput = new EditText(this);
        noteInput.setGravity(Gravity.TOP | Gravity.START);
        noteInput.setMinLines(5);
        noteInput.setHint("Write your notes for page " + (pageIndex + 1) + "…");
        noteInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        noteInput.setText(notes.getString(key, ""));
        new AlertDialog.Builder(this)
                .setTitle("Note · Page " + (pageIndex + 1))
                .setView(noteInput)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Delete note", (dialog, which) -> {
                    notes.edit().remove(key).apply();
                    Toast.makeText(this, "Page note deleted.", Toast.LENGTH_SHORT).show();
                })
                .setPositiveButton("Save note", (dialog, which) -> {
                    String value = noteInput.getText().toString();
                    if (value.trim().isEmpty()) notes.edit().remove(key).apply();
                    else notes.edit().putString(key, value).apply();
                    Toast.makeText(this, "Page note saved on this device.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private ArrayList<Integer> readPdfBookmarks(Uri uri) {
        String stored = getSharedPreferences("myoffice_pdf_bookmarks", MODE_PRIVATE)
                .getString(uri.toString(), "");
        ArrayList<Integer> pages = new ArrayList<>();
        if (!stored.isEmpty()) {
            for (String value : stored.split(",")) {
                try {
                    int page = Integer.parseInt(value);
                    if (page >= 0 && !pages.contains(page)) pages.add(page);
                } catch (NumberFormatException ignored) { }
            }
        }
        Collections.sort(pages);
        return pages;
    }

    private void savePdfBookmarks(Uri uri, ArrayList<Integer> pages) {
        StringBuilder stored = new StringBuilder();
        for (int page : pages) {
            if (stored.length() > 0) stored.append(',');
            stored.append(page);
        }
        getSharedPreferences("myoffice_pdf_bookmarks", MODE_PRIVATE)
                .edit().putString(uri.toString(), stored.toString()).apply();
    }

    private void togglePdfBookmark(Uri uri, int pageIndex) {
        ArrayList<Integer> pages = readPdfBookmarks(uri);
        if (pages.contains(pageIndex)) {
            pages.remove(Integer.valueOf(pageIndex));
            savePdfBookmarks(uri, pages);
            Toast.makeText(this, "Bookmark removed for page " + (pageIndex + 1) + ".", Toast.LENGTH_SHORT).show();
        } else {
            pages.add(pageIndex);
            Collections.sort(pages);
            savePdfBookmarks(uri, pages);
            Toast.makeText(this, "Bookmarked page " + (pageIndex + 1) + ".", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPdfBookmarks(Uri uri, ImageView image, TextView pageLabel, EditText pageInput,
                                  Button previous, Button next, int[] pageIndex, float[] zoomLevel) {
        ArrayList<Integer> pages = readPdfBookmarks(uri);
        if (pages.isEmpty()) {
            Toast.makeText(this, "No bookmarks for this PDF yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] labels = new String[pages.size()];
        for (int i = 0; i < pages.size(); i++) labels[i] = "Page " + (pages.get(i) + 1);
        new AlertDialog.Builder(this)
                .setTitle("PDF bookmarks")
                .setItems(labels, (dialog, which) ->
                        renderPdfPage(uri, pages.get(which), image, pageLabel, pageInput, previous, next, pageIndex, zoomLevel))
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void renderPdfPage(Uri uri, int requestedPage, ImageView image, TextView pageLabel, EditText pageInput,
                               Button previous, Button next, int[] pageIndex, float[] zoomLevel) {
        try (ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(uri, "r")) {
            if (descriptor == null) throw new IllegalStateException("PDF source is unavailable");
            try (PdfRenderer renderer = new PdfRenderer(descriptor)) {
                int pageCount = renderer.getPageCount();
                if (pageCount <= 0) throw new IllegalStateException("This PDF has no pages");
                int index = Math.max(0, Math.min(requestedPage, pageCount - 1));
                try (PdfRenderer.Page page = renderer.openPage(index)) {
                    float scale = Math.min(2.0f, Math.min(1400f / page.getWidth(), 1800f / page.getHeight())) * zoomLevel[0];
                    int width = Math.max(1, Math.round(page.getWidth() * scale));
                    int height = Math.max(1, Math.round(page.getHeight() * scale));
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    if (currentPdfRotationDegrees != 0) {
                        Matrix rotation = new Matrix();
                        rotation.postRotate(currentPdfRotationDegrees);
                        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotation, true);
                        if (rotated != bitmap) bitmap.recycle();
                        bitmap = rotated;
                    }
                    Object old = image.getTag();
                    if (old instanceof Bitmap && old != bitmap && !((Bitmap) old).isRecycled()) {
                        ((Bitmap) old).recycle();
                    }
                    image.setImageBitmap(bitmap);
                    image.setTag(bitmap);
                    pageIndex[0] = index;
                    getSharedPreferences("myoffice_pdf_positions", MODE_PRIVATE)
                            .edit().putInt(uri.toString(), index).apply();
                    pageInput.setText(String.valueOf(index + 1));
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
        File[] files = writerDirectory().listFiles((dir, name) -> name.endsWith(".txt") || name.endsWith(".html"));
        if (files == null) return new File[0];
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files;
    }

    private String readWriterRawFile(File file) throws Exception {
        try (InputStream in = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
            return out.toString("UTF-8");
        }
    }

    private String readWriterFile(File file) throws Exception {
        String raw = readWriterRawFile(file);
        if (file.getName().toLowerCase(Locale.ROOT).endsWith(".html")) {
            return Html.fromHtml(raw, Html.FROM_HTML_MODE_LEGACY).toString();
        }
        return raw;
    }

    private void applyWriterListPrefix(EditText body, boolean numbered) {
        Editable editable = body.getText();
        String snapshot = editable.toString();
        int selectionStart = body.getSelectionStart();
        int selectionEnd = body.getSelectionEnd();
        if (selectionStart < 0) selectionStart = 0;
        if (selectionEnd < 0) selectionEnd = selectionStart;
        int low = Math.min(selectionStart, selectionEnd);
        int high = Math.max(selectionStart, selectionEnd);
        int lineStart = snapshot.lastIndexOf('\n', Math.max(-1, low - 1)) + 1;
        int lineEnd = snapshot.indexOf('\n', high);
        if (lineEnd < 0) lineEnd = snapshot.length();
        String block = snapshot.substring(lineStart, lineEnd);
        String[] lines = block.split("\\n", -1);
        boolean hasTextLine = false;
        boolean allAlreadyFormatted = true;
        int[] offsets = new int[lines.length];
        int[] listNumbers = new int[lines.length];
        int cursor = lineStart;
        int nextNumber = 1;
        for (int i = 0; i < lines.length; i++) {
            offsets[i] = cursor;
            cursor += lines[i].length() + 1;
            if (lines[i].trim().isEmpty()) continue;
            hasTextLine = true;
            listNumbers[i] = nextNumber++;
            if (numbered) {
                if (!lines[i].matches("^\\d+\\.\\s+.*$")) allAlreadyFormatted = false;
            } else if (!lines[i].startsWith("• ")) {
                allAlreadyFormatted = false;
            }
        }
        if (!hasTextLine) {
            Toast.makeText(this, "Enter text first, then apply a list.", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i];
            if (line.trim().isEmpty()) continue;
            int offset = offsets[i];
            if (allAlreadyFormatted) {
                int prefixLength = 0;
                if (numbered) {
                    String stripped = line.replaceFirst("^\\d+\\.\\s+", "");
                    prefixLength = line.length() - stripped.length();
                } else if (line.startsWith("• ")) {
                    prefixLength = 2;
                }
                if (prefixLength > 0) editable.delete(offset, offset + prefixLength);
            } else {
                String prefix = numbered ? listNumbers[i] + ". " : "• ";
                editable.insert(offset, prefix);
            }
        }
        Toast.makeText(this, allAlreadyFormatted
                ? (numbered ? "Numbering removed." : "Bullets removed.")
                : (numbered ? "Numbered list applied." : "Bullets applied."),
                Toast.LENGTH_SHORT).show();
    }

    private void showWriterFontSizeDialog(EditText body) {
        final String[] sizes = new String[]{"8 sp", "10 sp", "12 sp", "14 sp", "16 sp", "18 sp", "20 sp", "24 sp", "28 sp", "32 sp"};
        final int[] values = new int[]{8, 10, 12, 14, 16, 18, 20, 24, 28, 32};
        new AlertDialog.Builder(this)
                .setTitle("Font size")
                .setItems(sizes, (dialog, which) -> {
                    int start = body.getSelectionStart();
                    int end = body.getSelectionEnd();
                    if (start < 0 || end < 0 || start == end) {
                        Toast.makeText(this, "Select text first, then choose a font size.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    body.getText().setSpan(new AbsoluteSizeSpan(values[which], true),
                            Math.min(start, end), Math.max(start, end), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }).show();
    }

    private void applyWriterParagraphStyle(EditText body, Layout.Alignment alignment, int headingLevel) {
        Editable editable = body.getText();
        if (editable.length() == 0) {
            Toast.makeText(this, "Enter text before applying a paragraph style.", Toast.LENGTH_SHORT).show();
            return;
        }
        int selectionStart = Math.max(0, body.getSelectionStart());
        int selectionEnd = Math.max(selectionStart, body.getSelectionEnd());
        int low = Math.min(selectionStart, selectionEnd);
        int high = Math.max(selectionStart, selectionEnd);
        int start = editable.toString().lastIndexOf('\n', Math.max(-1, low - 1)) + 1;
        int end = editable.toString().indexOf('\n', high);
        if (end < 0) end = editable.length();
        if (end <= start) end = Math.min(editable.length(), start + 1);
        if (headingLevel >= 0) {
            if (headingLevel == 0) {
                editable.setSpan(new RelativeSizeSpan(1.0f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                editable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                editable.setSpan(new RelativeSizeSpan(headingLevel == 1 ? 1.6f : 1.3f),
                        start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        editable.setSpan(new AlignmentSpan.Standard(alignment), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, headingLevel == 1 ? "Heading 1 applied."
                : headingLevel == 2 ? "Heading 2 applied."
                : headingLevel == 0 ? "Paragraph style applied."
                : "Paragraph alignment applied.", Toast.LENGTH_SHORT).show();
    }

    private void applyWriterSpan(EditText body, Object span) {
        int start = body.getSelectionStart();
        int end = body.getSelectionEnd();
        if (start < 0 || end < 0 || start == end) {
            Toast.makeText(this, "Select text first, then choose formatting.", Toast.LENGTH_SHORT).show();
            return;
        }
        int low = Math.min(start, end);
        int high = Math.max(start, end);
        body.getText().setSpan(span, low, high, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private boolean hasWriterFormatting(Spanned text) {
        int end = text.length();
        return text.getSpans(0, end, StyleSpan.class).length > 0
                || text.getSpans(0, end, UnderlineSpan.class).length > 0
                || text.getSpans(0, end, StrikethroughSpan.class).length > 0
                || text.getSpans(0, end, RelativeSizeSpan.class).length > 0
                || text.getSpans(0, end, ForegroundColorSpan.class).length > 0
                || text.getSpans(0, end, BackgroundColorSpan.class).length > 0;
    }

    private void saveWriterEditorContent(File existing, EditText body) {
        boolean existingHtml = existing != null && existing.getName().toLowerCase(Locale.ROOT).endsWith(".html");
        if (existingHtml || hasWriterFormatting(body.getText())) {
            saveWriterHtml(existingHtml ? existing : null, body);
        } else {
            saveWriterFile(existing, body.getText().toString());
        }
    }

    private void saveWriterHtml(File existing, EditText body) {
        File target = existing != null && existing.getName().toLowerCase(Locale.ROOT).endsWith(".html")
                ? existing : new File(writerDirectory(), "document_" + System.currentTimeMillis() + ".html");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
            writer.write(Html.toHtml(body.getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
            writer.flush();
            Toast.makeText(this, "Formatted document saved as HTML.", Toast.LENGTH_SHORT).show();
            showWriterList();
        } catch (Exception e) {
            Toast.makeText(this, "Could not save formatted document.", Toast.LENGTH_LONG).show();
        }
    }

    private void showWriterList() {
        writerEditorBackHandler = null;
        LinearLayout content = column();
        TextView back = label("‹  Back to MyOffice", 16, true);
        back.setOnClickListener(v -> showHome());
        content.addView(back);
        content.addView(label("Writer", 27, true));
        content.addView(label("Create and edit local documents. Select text to apply formatting; HTML preserves rich text.", 13, false));
        content.addView(action("＋  New document", v -> showWriterEditor(null, "")));
        EditText writerSearch = new EditText(this);
        writerSearch.setSingleLine(true);
        writerSearch.setHint("Search Writer documents");
        content.addView(writerSearch);
        LinearLayout writerResults = new LinearLayout(this);
        writerResults.setOrientation(LinearLayout.VERTICAL);
        Button writerSortButton = action("Sort: Modified newest", v -> {
            writerSortMode = (writerSortMode + 1) % 2;
            writerSortButtonLabel(v, writerSortMode);
            renderWriterResults(writerResults, writerSearch.getText().toString());
        });
        content.addView(writerSortButton);
        content.addView(writerResults);
        writerSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderWriterResults(writerResults, s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });
        renderWriterResults(writerResults, "");

        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setAppContentView(scroll, "writer-list");
    }

    private void writerSortButtonLabel(View view, int mode) {
        if (view instanceof Button) ((Button) view).setText(mode == 0 ? "Sort: Modified newest" : "Sort: Title A–Z");
    }

    private void duplicateWriterDocument(File source) {
        File target = new File(writerDirectory(), "document_" + System.currentTimeMillis() + ".txt");
        try (InputStream input = new FileInputStream(source);
             FileOutputStream output = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) output.write(buffer, 0, read);
            output.flush();
            Toast.makeText(this, "Writer document duplicated.", Toast.LENGTH_SHORT).show();
            showWriterList();
        } catch (Exception e) {
            if (target.exists()) target.delete();
            Toast.makeText(this, "Could not duplicate this document.", Toast.LENGTH_LONG).show();
        }
    }

    private String writerTitleForSort(File file) {
        try {
            String text = readWriterFile(file);
            String firstLine = text.split("\\R", 2)[0].trim();
            return firstLine.isEmpty() ? "Untitled document" : firstLine;
        } catch (Exception e) {
            return file.getName();
        }
    }

    private void renderWriterResults(LinearLayout results, String queryText) {
        results.removeAllViews();
        File[] files = writerFiles();
        if (writerSortMode == 1) {
            Arrays.sort(files, (left, right) -> writerTitleForSort(left).compareToIgnoreCase(writerTitleForSort(right)));
        } else {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        }
        String query = queryText == null ? "" : queryText.trim().toLowerCase(Locale.ROOT);
        int shown = 0;
        for (File file : files) {
            String text;
            try { text = readWriterFile(file); }
            catch (Exception e) { text = ""; }
            String firstLine = text.split("\\R", 2)[0].trim();
            if (firstLine.isEmpty()) firstLine = "Untitled document";
            String preview = text.replace('\n', ' ').trim();
            String searchable = (firstLine + " " + preview + " " + file.getName()).toLowerCase(Locale.ROOT);
            if (!query.isEmpty() && !searchable.contains(query)) continue;
            shown++;
            if (firstLine.length() > 80) firstLine = firstLine.substring(0, 80) + "…";
            if (preview.length() > 140) preview = preview.substring(0, 140) + "…";
            final File selected = file;
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(12), dp(8), dp(12), dp(8));
            card.setBackgroundColor(0xFFFFFFFF);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.setMargins(0, dp(5), 0, dp(5));
            results.addView(card, params);
            card.addView(label(firstLine, 17, true));
            card.addView(label(preview.isEmpty() ? "Empty document" : preview, 13, false));
            card.addView(label("Modified " + DateFormat.getDateTimeInstance().format(new Date(file.lastModified())), 11, false));
            card.addView(action("Duplicate document", v -> duplicateWriterDocument(selected)));
            card.addView(action("Delete document", v -> confirmDeleteFile(selected, "document", () -> showWriterList())));
            card.setOnClickListener(v -> {
                try { showWriterEditor(selected, readWriterFile(selected)); }
                catch (Exception e) { Toast.makeText(this, "Could not open this document.", Toast.LENGTH_LONG).show(); }
            });
        }
        if (shown == 0) {
            results.addView(label(query.isEmpty() ? "No Writer documents yet." : "No documents match your search.", 15, false));
        }
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

    private void showFindReplaceDialog(EditText body) {
        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        fields.setPadding(dp(20), dp(8), dp(20), 0);
        EditText findInput = new EditText(this);
        findInput.setSingleLine(true);
        findInput.setHint("Text to find");
        fields.addView(findInput);
        EditText replaceInput = new EditText(this);
        replaceInput.setSingleLine(true);
        replaceInput.setHint("Replace with");
        fields.addView(replaceInput);
        new AlertDialog.Builder(this)
                .setTitle("Find & Replace")
                .setView(fields)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Replace all", (dialog, which) -> {
                    String find = findInput.getText().toString();
                    if (find.isEmpty()) {
                        Toast.makeText(this, "Enter text to find.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String original = body.getText().toString();
                    int count = 0;
                    int offset = 0;
                    while ((offset = original.indexOf(find, offset)) >= 0) {
                        count++;
                        offset += find.length();
                    }
                    if (count == 0) {
                        Toast.makeText(this, "No matching text found.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String updated = original.replace(find, replaceInput.getText().toString());
                    body.setText(updated);
                    body.setSelection(body.getText().length());
                    Toast.makeText(this, "Replaced " + count + " occurrence(s).", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showWriterEditor(File existing, Spanned initialRichText) {
        showWriterEditor(existing, initialRichText == null ? "" : initialRichText.toString(), initialRichText);
    }

    private void showWriterEditor(File existing, String initialText) {
        showWriterEditor(existing, initialText, null);
    }

    private void showWriterEditor(File existing, String initialText, Spanned initialRichText) {
        LinearLayout content = column();
        TextView back = label("‹  Back to Writer", 16, true);
        content.addView(back);
        content.addView(label("Writer · Document editor", 24, true));
        content.addView(label("Offline editing · Hindi and English · Select text before applying character formatting.", 13, false));
        EditText body = new EditText(this);
        body.setGravity(Gravity.TOP | Gravity.START);
        body.setMinLines(16);
        body.setTextSize(16);
        body.setHint("Start writing…");
        body.setText(initialRichText == null ? initialText : initialRichText);
        if (existing != null && existing.getName().toLowerCase(Locale.ROOT).endsWith(".html")) {
            try { body.setText(Html.fromHtml(readWriterRawFile(existing), Html.FROM_HTML_MODE_LEGACY)); }
            catch (Exception ignored) { }
        }
        body.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        content.addView(body, new LinearLayout.LayoutParams(-1, dp(400)));
        final String[] initialTextSnapshot = new String[]{body.getText().toString()};
        final String[] initialRichSnapshot = new String[]{Html.toHtml(body.getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)};
        content.addView(label("Formatting", 16, true));
        HorizontalScrollView formatScroll = new HorizontalScrollView(this);
        LinearLayout formatBar = new LinearLayout(this);
        formatBar.setOrientation(LinearLayout.HORIZONTAL);
        formatBar.addView(action("Bold", v -> applyWriterSpan(body, new StyleSpan(Typeface.BOLD))));
        formatBar.addView(action("Italic", v -> applyWriterSpan(body, new StyleSpan(Typeface.ITALIC))));
        formatBar.addView(action("Underline", v -> applyWriterSpan(body, new UnderlineSpan())));
        formatBar.addView(action("Strike", v -> applyWriterSpan(body, new StrikethroughSpan())));
        formatBar.addView(action("Bigger", v -> applyWriterSpan(body, new RelativeSizeSpan(1.25f))));
        formatBar.addView(action("Smaller", v -> applyWriterSpan(body, new RelativeSizeSpan(0.85f))));
        formatBar.addView(action("Font size", v -> showWriterFontSizeDialog(body)));
        formatBar.addView(action("Serif", v -> applyWriterSpan(body, new TypefaceSpan("serif"))));
        formatBar.addView(action("Sans", v -> applyWriterSpan(body, new TypefaceSpan("sans-serif"))));
        formatBar.addView(action("Monospace", v -> applyWriterSpan(body, new TypefaceSpan("monospace"))));
        formatBar.addView(action("Red", v -> applyWriterSpan(body, new ForegroundColorSpan(0xFFD32F2F))));
        formatBar.addView(action("Blue", v -> applyWriterSpan(body, new ForegroundColorSpan(0xFF1565C0))));
        formatBar.addView(action("Highlight", v -> applyWriterSpan(body, new BackgroundColorSpan(0xFFFFFF00))));
        formatBar.addView(action("• Bullets", v -> applyWriterListPrefix(body, false)));
        formatBar.addView(action("1. Numbered", v -> applyWriterListPrefix(body, true)));
        formatBar.addView(action("Left", v -> body.setGravity(Gravity.TOP | Gravity.START)));
        formatBar.addView(action("Center", v -> body.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL)));
        formatBar.addView(action("Right", v -> body.setGravity(Gravity.TOP | Gravity.RIGHT)));
        formatScroll.addView(formatBar);
        content.addView(formatScroll);
        content.addView(label("Paragraph & heading styles", 14, true));
        HorizontalScrollView paragraphScroll = new HorizontalScrollView(this);
        LinearLayout paragraphBar = new LinearLayout(this);
        paragraphBar.setOrientation(LinearLayout.HORIZONTAL);
        paragraphBar.addView(action("Heading 1", v -> applyWriterParagraphStyle(body, Layout.Alignment.ALIGN_NORMAL, 1)));
        paragraphBar.addView(action("Heading 2", v -> applyWriterParagraphStyle(body, Layout.Alignment.ALIGN_NORMAL, 2)));
        paragraphBar.addView(action("Paragraph", v -> applyWriterParagraphStyle(body, Layout.Alignment.ALIGN_NORMAL, 0)));
        paragraphBar.addView(action("Align left", v -> applyWriterParagraphStyle(body, Layout.Alignment.ALIGN_NORMAL, -1)));
        paragraphBar.addView(action("Center paragraph", v -> applyWriterParagraphStyle(body, Layout.Alignment.ALIGN_CENTER, -1)));
        paragraphBar.addView(action("Align right", v -> applyWriterParagraphStyle(body, Layout.Alignment.ALIGN_OPPOSITE, -1)));
        paragraphBar.addView(action("Justify", v -> { body.setJustificationMode(android.text.Layout.JUSTIFICATION_MODE_INTER_WORD); body.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL); applyWriterParagraphStyle(body, Layout.Alignment.ALIGN_NORMAL, -1); }));
        paragraphScroll.addView(paragraphBar);
        content.addView(paragraphScroll);
        TextView writerStats = label("", 12, false);
        writerStats.setTextColor(0xFF536078);
        content.addView(writerStats);
        Runnable updateWriterStats = () -> {
            String value = body.getText().toString();
            String trimmed = value.trim();
            int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
            writerStats.setText(words + " words  •  " + value.length() + " characters");
        };
        ArrayList<String> undoHistory = new ArrayList<>();
        ArrayList<String> redoHistory = new ArrayList<>();
        String[] beforeChange = new String[]{initialText};
        boolean[] replayingHistory = new boolean[]{false};
        body.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeChange[0] = s.toString();
            }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String updated = s.toString();
                if (!replayingHistory[0] && !beforeChange[0].equals(updated)) {
                    undoHistory.add(beforeChange[0]);
                    if (undoHistory.size() > 50) undoHistory.remove(0);
                    redoHistory.clear();
                }
                updateWriterStats.run();
            }
            @Override public void afterTextChanged(Editable s) { }
        });
        updateWriterStats.run();
        LinearLayout historyActions = new LinearLayout(this);
        historyActions.setOrientation(LinearLayout.HORIZONTAL);
        historyActions.addView(action("Undo", v -> {
            if (undoHistory.isEmpty()) {
                Toast.makeText(this, "Nothing to undo.", Toast.LENGTH_SHORT).show();
                return;
            }
            redoHistory.add(body.getText().toString());
            String previous = undoHistory.remove(undoHistory.size() - 1);
            replayingHistory[0] = true;
            body.setText(previous);
            body.setSelection(body.getText().length());
            replayingHistory[0] = false;
        }), new LinearLayout.LayoutParams(0, -2, 1));
        historyActions.addView(action("Redo", v -> {
            if (redoHistory.isEmpty()) {
                Toast.makeText(this, "Nothing to redo.", Toast.LENGTH_SHORT).show();
                return;
            }
            undoHistory.add(body.getText().toString());
            if (undoHistory.size() > 50) undoHistory.remove(0);
            String nextText = redoHistory.remove(redoHistory.size() - 1);
            replayingHistory[0] = true;
            body.setText(nextText);
            body.setSelection(body.getText().length());
            replayingHistory[0] = false;
        }), new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(historyActions);
        content.addView(action("Font size…", v -> {
            EditText sizeInput = new EditText(this);
            sizeInput.setSingleLine(true);
            sizeInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            sizeInput.setHint("Size in points (8–48)");
            new AlertDialog.Builder(this).setTitle("Apply font size to selection")
                    .setMessage("Select text first. Without a selection, the editor text size changes.")
                    .setView(sizeInput).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Apply", (dialog, which) -> {
                        try {
                            float points = Float.parseFloat(sizeInput.getText().toString().trim());
                            if (points < 8f || points > 48f) throw new NumberFormatException();
                            int start = Math.min(body.getSelectionStart(), body.getSelectionEnd());
                            int end = Math.max(body.getSelectionStart(), body.getSelectionEnd());
                            if (start >= 0 && end > start) body.getText().setSpan(new AbsoluteSizeSpan((int) points, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            else body.setTextSize(points);
                        } catch (NumberFormatException e) { Toast.makeText(this, "Enter a font size from 8 to 48.", Toast.LENGTH_SHORT).show(); }
                    }).show();
        }));
        content.addView(action("Heading style", v -> {
            String[] styles = {"Title", "Heading 1", "Heading 2", "Normal"};
            new AlertDialog.Builder(this).setTitle("Apply style to selection").setItems(styles, (dialog, which) -> {
                int start = Math.min(body.getSelectionStart(), body.getSelectionEnd());
                int end = Math.max(body.getSelectionStart(), body.getSelectionEnd());
                if (start < 0 || end <= start) { Toast.makeText(this, "Select the text to format first.", Toast.LENGTH_SHORT).show(); return; }
                float factor = which == 0 ? 1.8f : (which == 1 ? 1.5f : (which == 2 ? 1.25f : 1.0f));
                body.getText().setSpan(new RelativeSizeSpan(factor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (which != 3) body.getText().setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }).show();
        }));
        content.addView(action("Font family…", v -> {
            String[] families = {"Sans serif", "Serif", "Monospace", "Sans-serif condensed", "Casual", "Cursive"};
            String[] names = {"sans-serif", "serif", "monospace", "sans-serif-condensed", "casual", "cursive"};
            new AlertDialog.Builder(this).setTitle("Apply font family to selection")
                    .setItems(families, (dialog, which) -> {
                        int start = Math.min(body.getSelectionStart(), body.getSelectionEnd());
                        int end = Math.max(body.getSelectionStart(), body.getSelectionEnd());
                        if (start < 0 || end <= start) {
                            Toast.makeText(this, "Select the text to format first.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        body.getText().setSpan(new TypefaceSpan(names[which]), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }).show();
        }));
        content.addView(action("Font color…", v -> {
            String[] colors = {"Black", "Red", "Blue", "Green", "Purple"};
            int[] values = {0xFF202124, 0xFFD32F2F, 0xFF1565C0, 0xFF2E7D32, 0xFF6A1B9A};
            new AlertDialog.Builder(this).setTitle("Apply font color to selection").setItems(colors, (dialog, which) -> {
                int start = Math.min(body.getSelectionStart(), body.getSelectionEnd());
                int end = Math.max(body.getSelectionStart(), body.getSelectionEnd());
                if (start < 0 || end <= start) { Toast.makeText(this, "Select the text to format first.", Toast.LENGTH_SHORT).show(); return; }
                body.getText().setSpan(new ForegroundColorSpan(values[which]), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }).show();
        }));
        content.addView(action("Clear formatting", v -> {
            Editable editable = body.getText();
            Object[] spans = editable.getSpans(0, editable.length(), Object.class);
            for (Object span : spans) {
                if (span instanceof StyleSpan || span instanceof UnderlineSpan || span instanceof StrikethroughSpan
                        || span instanceof RelativeSizeSpan || span instanceof AbsoluteSizeSpan
                        || span instanceof ForegroundColorSpan || span instanceof BackgroundColorSpan) editable.removeSpan(span);
            }
            Toast.makeText(this, "Character formatting cleared.", Toast.LENGTH_SHORT).show();
        }));
        content.addView(action("Find & Replace", v -> showFindReplaceDialog(body)));
        content.addView(action("Save document", v -> saveWriterEditorContent(existing, body)));
        content.addView(action("Save formatted copy (HTML)", v -> saveWriterHtml(null, body)));
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
        content.addView(action("Export as DOCX", v -> {
            String filename = existing == null ? "MyOffice_Document.docx" : existing.getName().replaceFirst("(?i)\\.(txt|html)$", ".docx");
            exportWriterAsDocx(filename, body.getText(), body.getGravity());
        }));
        content.addView(action("Export as RTF", v -> {
            String filename = existing == null ? "MyOffice_Document.rtf" : existing.getName().replaceFirst("(?i)\\.(txt|html)$", ".rtf");
            startRtfExport(filename, body.getText());
        }));
        content.addView(action("Discard changes", v -> new AlertDialog.Builder(this)
                .setTitle("Discard this edit?")
                .setMessage("Unsaved changes will be lost.")
                .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Discard", (dialog, which) -> showWriterList())
                .show()));
        writerEditorBackHandler = () -> {
            boolean unchangedText = body.getText().toString().equals(initialTextSnapshot[0]);
            boolean unchangedRich = Html.toHtml(body.getText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL).equals(initialRichSnapshot[0]);
            if (unchangedText && unchangedRich) {
                showWriterList();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Unsaved changes")
                        .setMessage("Save this document before leaving?")
                        .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                        .setNeutralButton("Discard", (dialog, which) -> showWriterList())
                        .setPositiveButton("Save", (dialog, which) -> saveWriterEditorContent(existing, body))
                        .show();
            }
        };
        back.setOnClickListener(v -> writerEditorBackHandler.run());
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setAppContentView(scroll, "writer-editor");
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

    private String xlsxAttribute(Node node, String name) {
        if (node == null || node.getAttributes() == null) return "";
        Node attribute = node.getAttributes().getNamedItem(name);
        return attribute == null ? "" : attribute.getNodeValue();
    }

    private int xlsxColumnIndex(String cellReference) {
        int column = 0;
        int i = 0;
        while (i < cellReference.length()) {
            char ch = Character.toUpperCase(cellReference.charAt(i));
            if (ch < 'A' || ch > 'Z') break;
            column = column * 26 + (ch - 'A' + 1);
            i++;
        }
        return i == 0 ? -1 : column - 1;
    }

    private int xlsxRowIndex(String cellReference) {
        int i = 0;
        while (i < cellReference.length() && Character.isLetter(cellReference.charAt(i))) i++;
        if (i >= cellReference.length()) return -1;
        try { return Integer.parseInt(cellReference.substring(i)) - 1; }
        catch (NumberFormatException ignored) { return -1; }
    }

    private String xlsxSharedStringValue(Node item, String namespace) {
        StringBuilder value = new StringBuilder();
        NodeList texts = ((org.w3c.dom.Element) item).getElementsByTagNameNS(namespace, "t");
        for (int i = 0; i < texts.getLength(); i++) value.append(texts.item(i).getTextContent());
        return value.toString();
    }

    private ArrayList<ArrayList<String>> readXlsxSheet(Uri uri) throws Exception {
        if (sizeOf(uri) > 10L * 1024L * 1024L) throw new IllegalArgumentException("XLSX exceeds the 10 MiB import limit");
        byte[] sharedXml = null;
        byte[] sheetXml = null;
        int totalUncompressed = 0;
        InputStream source = getContentResolver().openInputStream(uri);
        if (source == null) throw new IllegalStateException("XLSX source unavailable");
        try (ZipInputStream zip = new ZipInputStream(source)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String entryName = entry.getName();
                if ("xl/sharedStrings.xml".equals(entryName) || "xl/worksheets/sheet1.xml".equals(entryName)) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = zip.read(buffer)) != -1) {
                        totalUncompressed += count;
                        if (totalUncompressed > 8 * 1024 * 1024) throw new IllegalArgumentException("XLSX worksheet content exceeds the import limit");
                        out.write(buffer, 0, count);
                    }
                    if ("xl/sharedStrings.xml".equals(entryName)) sharedXml = out.toByteArray();
                    else sheetXml = out.toByteArray();
                }
            }
        }
        if (sheetXml == null) throw new IllegalArgumentException("XLSX first worksheet was not found");
        String namespace = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";
        ArrayList<String> sharedStrings = new ArrayList<>();
        if (sharedXml != null) {
            DocumentBuilderFactory sharedFactory = DocumentBuilderFactory.newInstance();
            sharedFactory.setNamespaceAware(true);
            sharedFactory.setXIncludeAware(false);
            sharedFactory.setExpandEntityReferences(false);
            sharedFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            sharedFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            sharedFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            org.w3c.dom.Document sharedDocument = sharedFactory.newDocumentBuilder().parse(new ByteArrayInputStream(sharedXml));
            NodeList items = sharedDocument.getElementsByTagNameNS(namespace, "si");
            for (int i = 0; i < items.getLength(); i++) sharedStrings.add(xlsxSharedStringValue(items.item(i), namespace));
        }
        DocumentBuilderFactory sheetFactory = DocumentBuilderFactory.newInstance();
        sheetFactory.setNamespaceAware(true);
        sheetFactory.setXIncludeAware(false);
        sheetFactory.setExpandEntityReferences(false);
        sheetFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        sheetFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        sheetFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        org.w3c.dom.Document sheetDocument = sheetFactory.newDocumentBuilder().parse(new ByteArrayInputStream(sheetXml));
        NodeList rowNodes = sheetDocument.getElementsByTagNameNS(namespace, "row");
        final int maxRows = 100;
        final int maxColumns = 30;
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        int maxRow = -1;
        int maxColumn = -1;
        ArrayList<int[]> positions = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        for (int rowNo = 0; rowNo < rowNodes.getLength(); rowNo++) {
            Node rowNode = rowNodes.item(rowNo);
            NodeList cells = rowNode.getChildNodes();
            int fallbackColumn = 0;
            for (int cellNo = 0; cellNo < cells.getLength(); cellNo++) {
                Node cell = cells.item(cellNo);
                if (cell.getNodeType() != Node.ELEMENT_NODE || !"c".equals(cell.getLocalName())) continue;
                String reference = xlsxAttribute(cell, "r");
                int rowIndex = reference.isEmpty() ? rowNo : xlsxRowIndex(reference);
                int columnIndex = reference.isEmpty() ? fallbackColumn : xlsxColumnIndex(reference);
                fallbackColumn = Math.max(fallbackColumn + 1, columnIndex + 1);
                if (rowIndex < 0 || columnIndex < 0) continue;
                if (rowIndex >= maxRows || columnIndex >= maxColumns) throw new IllegalArgumentException("XLSX exceeds the 100-row by 30-column import limit");
                String type = xlsxAttribute(cell, "t");
                String value = "";
                if ("inlineStr".equals(type)) {
                    value = xlsxSharedStringValue(cell, namespace);
                } else {
                    NodeList valueNodes = ((org.w3c.dom.Element) cell).getElementsByTagNameNS(namespace, "v");
                    if (valueNodes.getLength() > 0) value = valueNodes.item(0).getTextContent();
                    if ("s".equals(type) && !value.isEmpty()) {
                        int index = Integer.parseInt(value);
                        if (index >= 0 && index < sharedStrings.size()) value = sharedStrings.get(index);
                        else value = "";
                    } else if ("b".equals(type)) {
                        value = "1".equals(value) ? "TRUE" : "FALSE";
                    }
                }
                positions.add(new int[]{rowIndex, columnIndex});
                values.add(value);
                maxRow = Math.max(maxRow, rowIndex);
                maxColumn = Math.max(maxColumn, columnIndex);
            }
            String rowNumber = xlsxAttribute(rowNode, "r");
            if (!rowNumber.isEmpty()) {
                try {
                    int rowIndex = Integer.parseInt(rowNumber) - 1;
                    if (rowIndex >= 0 && rowIndex < maxRows) maxRow = Math.max(maxRow, rowIndex);
                } catch (NumberFormatException ignored) { }
            }
        }
        if (maxRow < 0) return parseCsv("");
        if (maxRow >= maxRows || maxColumn >= maxColumns) throw new IllegalArgumentException("XLSX exceeds the import limits");
        for (int r = 0; r <= maxRow; r++) {
            ArrayList<String> row = new ArrayList<>();
            for (int col = 0; col <= Math.max(0, maxColumn); col++) row.add("");
            rows.add(row);
        }
        for (int i = 0; i < positions.size(); i++) {
            int[] position = positions.get(i);
            rows.get(position[0]).set(position[1], values.get(i));
        }
        return rows;
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

    private void requestSpreadsheetExit() {
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
    }

    private void setAppContentView(View screen, String route) {
        boolean replaceTransition =
                ("note-editor".equals(currentScreenRoute) && "notes-list".equals(route)) ||
                ("writer-editor".equals(currentScreenRoute) && "writer-list".equals(route)) ||
                ("spreadsheet-editor".equals(currentScreenRoute) && "spreadsheet-list".equals(route)) ||
                ("presentation-editor".equals(currentScreenRoute) && "presentation-list".equals(route)) ||
                ("slideshow".equals(currentScreenRoute) && "presentation-editor".equals(route));
        if ("home".equals(route)) {
            navigationHistory.clear();
            routeHistory.clear();
            homeHistory.clear();
        } else if (!restoringBackNavigation && currentRootView != null && !replaceTransition) {
            navigationHistory.add(currentRootView);
            routeHistory.add(currentScreenRoute);
            homeHistory.add(homeVisible);
            while (navigationHistory.size() > 20) {
                navigationHistory.remove(0);
                routeHistory.remove(0);
                homeHistory.remove(0);
            }
        }
        currentRootView = screen;
        currentScreenRoute = route;
        homeVisible = "home".equals(route);
        restoringBackNavigation = false;
        setContentView(screen);
    }

    private void navigateBackOrExit() {
        if (!navigationHistory.isEmpty()) {
            int last = navigationHistory.size() - 1;
            currentRootView = navigationHistory.remove(last);
            currentScreenRoute = routeHistory.remove(last);
            homeVisible = homeHistory.remove(last);
            restoringBackNavigation = true;
            setAppContentView(currentRootView, currentScreenRoute);
            return;
        }
        if (!homeVisible) {
            showHome();
            return;
        }
        long now = SystemClock.elapsedRealtime();
        if (now - lastBackPressedAt <= 2000L) {
            finish();
            return;
        }
        lastBackPressedAt = now;
        Toast.makeText(this, "Press Back again to close MyOffice.", Toast.LENGTH_SHORT).show();
    }

    @Override public void onBackPressed() {
        if ("note-editor".equals(currentScreenRoute) && noteEditorBackHandler != null) {
            noteEditorBackHandler.run();
            return;
        }
        if ("writer-editor".equals(currentScreenRoute) && writerEditorBackHandler != null) {
            writerEditorBackHandler.run();
            return;
        }
        if (spreadsheetEditorOpen) {
            requestSpreadsheetExit();
            return;
        }
        if (presentationEditorOpen) {
            requestPresentationExit();
            return;
        }
        if (slideshowOpen) {
            showPresentationEditor(false);
            return;
        }
        navigateBackOrExit();
    }

    private void showSpreadsheetList() {
        spreadsheetEditorOpen = false;
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
        content.addView(action("Import XLSX", v -> openXlsxPicker()));
        EditText sheetSearch = new EditText(this);
        sheetSearch.setSingleLine(true);
        sheetSearch.setHint("Search CSV sheets");
        content.addView(sheetSearch);
        LinearLayout sheetResults = new LinearLayout(this);
        sheetResults.setOrientation(LinearLayout.VERTICAL);
        Button sheetSortButton = action("Sort: Modified newest", v -> {
            spreadsheetSortMode = (spreadsheetSortMode + 1) % 2;
            sheetSortButtonLabel(v, spreadsheetSortMode);
            renderSpreadsheetResults(sheetResults, sheetSearch.getText().toString());
        });
        content.addView(sheetSortButton);
        content.addView(sheetResults);
        sheetSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderSpreadsheetResults(sheetResults, s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });
        renderSpreadsheetResults(sheetResults, "");

        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setAppContentView(scroll, "spreadsheet-list");
    }

    private void sheetSortButtonLabel(View view, int mode) {
        if (view instanceof Button) ((Button) view).setText(mode == 0 ? "Sort: Modified newest" : "Sort: Name A–Z");
    }

    private void duplicateSpreadsheet(File source) {
        File target = new File(spreadsheetDirectory(), "sheet_" + System.currentTimeMillis() + ".csv");
        try (InputStream input = new FileInputStream(source);
             FileOutputStream output = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) output.write(buffer, 0, read);
            output.flush();
            Toast.makeText(this, "CSV sheet duplicated.", Toast.LENGTH_SHORT).show();
            showSpreadsheetList();
        } catch (Exception e) {
            if (target.exists()) target.delete();
            Toast.makeText(this, "Could not duplicate this CSV sheet.", Toast.LENGTH_LONG).show();
        }
    }

    private void renderSpreadsheetResults(LinearLayout results, String queryText) {
        results.removeAllViews();
        File[] files = csvFiles();
        if (spreadsheetSortMode == 1) {
            Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
        } else {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        }
        String query = queryText == null ? "" : queryText.trim().toLowerCase(Locale.ROOT);
        int shown = 0;
        for (File file : files) {
            String preview = "";
            try { preview = readCsvFile(file).split("\\R", 2)[0]; } catch (Exception ignored) { }
            if (!query.isEmpty() && !(file.getName() + " " + preview).toLowerCase(Locale.ROOT).contains(query)) continue;
            shown++;
            final File selected = file;
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(12), dp(8), dp(12), dp(8));
            card.setBackgroundColor(0xFFFFFFFF);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.setMargins(0, dp(5), 0, dp(5));
            results.addView(card, params);
            card.addView(label(file.getName(), 17, true));
            card.addView(label(preview.isEmpty() ? "Empty sheet" : preview, 13, false));
            card.addView(label("Modified " + DateFormat.getDateTimeInstance().format(new Date(file.lastModified())), 11, false));
            card.addView(action("Duplicate sheet", v -> duplicateSpreadsheet(selected)));
            card.addView(action("Delete sheet", v -> confirmDeleteFile(selected, "sheet", () -> showSpreadsheetList())));
            card.setOnClickListener(v -> {
                try { showSpreadsheetEditor(selected, parseCsv(readCsvFile(selected)), true); }
                catch (Exception e) { Toast.makeText(this, "Could not open this CSV file.", Toast.LENGTH_LONG).show(); }
            });
        }
        if (shown == 0) results.addView(label(query.isEmpty() ? "No CSV sheets yet." : "No sheets match your search.", 15, false));
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

    private Double numericCell(ArrayList<ArrayList<String>> data, int[] reference) {
        if (reference == null || reference[0] < 0 || reference[0] >= data.size()
                || reference[1] < 0 || reference[1] >= data.get(reference[0]).size()) return null;
        try { return Double.parseDouble(data.get(reference[0]).get(reference[1]).trim()); }
        catch (Exception e) { return null; }
    }

    private String formatFormulaNumber(double result) {
        if (result == Math.rint(result) && Math.abs(result) < 9e15) return String.valueOf((long) result);
        return String.format(Locale.ROOT, "%.6f", result).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private String evaluateAggregateFormula(String formula, ArrayList<ArrayList<String>> data) {
        String value = formula.trim();
        if (!value.startsWith("=")) return null;
        String expression = value.substring(1).replace(" ", "");
        if (value.indexOf('(') < 0) {
            int operatorIndex = -1;
            for (int i = 1; i < expression.length(); i++) {
                char operator = expression.charAt(i);
                if (operator == '+' || operator == '-' || operator == '*' || operator == '/') {
                    operatorIndex = i;
                    break;
                }
            }
            if (operatorIndex > 0 && operatorIndex < expression.length() - 1) {
                int[] leftRef = parseCellReference(expression.substring(0, operatorIndex));
                int[] rightRef = parseCellReference(expression.substring(operatorIndex + 1));
                if (leftRef != null && rightRef != null) {
                    Double left = numericCell(data, leftRef);
                    Double right = numericCell(data, rightRef);
                    if (left == null || right == null) return "ERROR: both referenced cells must contain numbers";
                    char operator = expression.charAt(operatorIndex);
                    if (operator == '/' && right == 0d) return "ERROR: division by zero";
                    double result;
                    if (operator == '+') result = left + right;
                    else if (operator == '-') result = left - right;
                    else if (operator == '*') result = left * right;
                    else result = left / right;
                    return formatFormulaNumber(result);
                }
            }
        }
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
        return formatFormulaNumber(result);
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
        spreadsheetEditorOpen = true;
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
        content.addView(action("Export XLSX copy", v -> {
            String filename = currentCsvFile == null ? "MyOffice_Sheet.xlsx" : currentCsvFile.getName().replaceFirst("(?i)\\.csv$", ".xlsx");
            startXlsxExport(filename, readSheetCells());
        }));
        content.addView(action("Save CSV sheet", v -> saveSpreadsheet()));
        content.addView(action("Discard changes", v -> new AlertDialog.Builder(this)
                .setTitle("Discard this sheet edit?")
                .setMessage("Unsaved changes will be lost.")
                .setNegativeButton("Keep editing", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Discard", (dialog, which) -> showSpreadsheetList())
                .show()));
        if (resetOriginal) sheetOriginalSnapshot = serializeSheet(data);
        back.setOnClickListener(v -> requestSpreadsheetExit());
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setAppContentView(scroll, "spreadsheet-editor");
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
        presentationEditorOpen = false;
        slideshowOpen = false;
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
        EditText deckSearch = new EditText(this);
        deckSearch.setSingleLine(true);
        deckSearch.setHint("Search slide decks");
        content.addView(deckSearch);
        LinearLayout deckResults = new LinearLayout(this);
        deckResults.setOrientation(LinearLayout.VERTICAL);
        Button deckSortButton = action("Sort: Modified newest", v -> {
            presentationSortMode = (presentationSortMode + 1) % 2;
            deckSortButtonLabel(v, presentationSortMode);
            renderPresentationResults(deckResults, deckSearch.getText().toString());
        });
        content.addView(deckSortButton);
        content.addView(deckResults);
        deckSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderPresentationResults(deckResults, s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });
        renderPresentationResults(deckResults, "");

        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setAppContentView(scroll, "presentation-list");
    }

    private void deckSortButtonLabel(View view, int mode) {
        if (view instanceof Button) ((Button) view).setText(mode == 0 ? "Sort: Modified newest" : "Sort: Title A–Z");
    }

    private String deckTitleForSort(File file) {
        try {
            ArrayList<Slide> slides = readDeck(file);
            if (!slides.isEmpty() && !slides.get(0).title.trim().isEmpty()) return slides.get(0).title.trim();
        } catch (Exception ignored) { }
        return file.getName();
    }

    private void duplicatePresentation(File source) {
        try {
            ArrayList<Slide> slides = readDeck(source);
            if (slides.isEmpty()) slides.add(new Slide("", ""));
            String title = slides.get(0).title.trim();
            slides.get(0).title = (title.isEmpty() ? "Untitled presentation" : title) + " (copy)";
            File target = new File(presentationDirectory(), "presentation_" + System.currentTimeMillis() + ".deck");
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
                writer.write(serializeDeck(slides));
                writer.flush();
            }
            Toast.makeText(this, "Slide deck duplicated.", Toast.LENGTH_SHORT).show();
            showPresentationList();
        } catch (Exception e) {
            Toast.makeText(this, "Could not duplicate this slide deck.", Toast.LENGTH_LONG).show();
        }
    }

    private void renderPresentationResults(LinearLayout results, String queryText) {
        results.removeAllViews();
        File[] files = deckFiles();
        if (presentationSortMode == 1) {
            Arrays.sort(files, (left, right) -> deckTitleForSort(left).compareToIgnoreCase(deckTitleForSort(right)));
        } else {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        }
        String query = queryText == null ? "" : queryText.trim().toLowerCase(Locale.ROOT);
        int shown = 0;
        for (File file : files) {
            String title = file.getName();
            int count = 0;
            String preview = "";
            try {
                ArrayList<Slide> slides = readDeck(file);
                count = slides.size();
                if (!slides.isEmpty()) {
                    if (!slides.get(0).title.trim().isEmpty()) title = slides.get(0).title.trim();
                    preview = slides.get(0).body;
                }
            } catch (Exception ignored) { }
            String searchable = (title + " " + preview + " " + file.getName()).toLowerCase(Locale.ROOT);
            if (!query.isEmpty() && !searchable.contains(query)) continue;
            shown++;
            final File selected = file;
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(12), dp(8), dp(12), dp(8));
            card.setBackgroundColor(0xFFFFFFFF);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.setMargins(0, dp(5), 0, dp(5));
            results.addView(card, params);
            card.addView(label(title, 17, true));
            if (preview.length() > 100) preview = preview.substring(0, 100) + "…";
            card.addView(label(preview.isEmpty() ? count + " slide(s)" : preview, 13, false));
            card.addView(label(count + " slide(s) · Modified " + DateFormat.getDateTimeInstance().format(new Date(file.lastModified())), 12, false));
            card.addView(action("Duplicate deck", v -> duplicatePresentation(selected)));
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
        if (shown == 0) results.addView(label(query.isEmpty() ? "No slide decks yet." : "No slide decks match your search.", 15, false));
    }

    private void requestPresentationExit() {
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
        presentationEditorOpen = true;
        slideshowOpen = false;
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
        content.addView(action("Share entire presentation", v -> {
            syncDeckEditor();
            StringBuilder allSlides = new StringBuilder();
            for (int slideNo = 0; slideNo < deckSlides.size(); slideNo++) {
                Slide item = deckSlides.get(slideNo);
                allSlides.append("Slide ").append(slideNo + 1).append("\\n");
                if (!item.title.trim().isEmpty()) allSlides.append(item.title.trim()).append("\\n");
                allSlides.append(item.body);
                if (slideNo < deckSlides.size() - 1) allSlides.append("\\n\\n--------------------\\n\\n");
            }
            String subject = deckSlides.get(0).title.trim();
            if (subject.isEmpty()) subject = "MyOffice Presentation";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, subject);
            share.putExtra(Intent.EXTRA_TEXT, allSlides.toString());
            try { startActivity(Intent.createChooser(share, "Share entire presentation")); }
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
        back.setOnClickListener(v -> requestPresentationExit());
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setAppContentView(scroll, "presentation-editor");
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
        presentationEditorOpen = false;
        slideshowOpen = true;
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
        setAppContentView(scroll, "slideshow");
    }

    private void updateRecentSortLabel() {
        if (recentSortButton == null) return;
        String label = recentSortMode == 0 ? "Sort: Newest first"
                : recentSortMode == 1 ? "Sort: Name A–Z"
                : recentSortMode == 2 ? "Sort: Largest first" : "Sort: Favorites first";
        recentSortButton.setText(label);
    }

    private void updateRecentFilterButtons() {
        for (int i = 0; i < recentFilterButtons.size(); i++) {
            Button button = recentFilterButtons.get(i);
            button.setBackgroundColor(i == recentFilterMode ? 0xFFD9E8FF : 0xFFFFFFFF);
            button.setTextColor(0xFF202735);
        }
    }

    private boolean matchesRecentFilter(String name, String mime) {
        String lower = name.toLowerCase(Locale.ROOT);
        String type = mime == null ? "" : mime.toLowerCase(Locale.ROOT);
        switch (recentFilterMode) {
            case 1:
                return lower.endsWith(".pdf") || type.equals("application/pdf");
            case 2:
                return (type.startsWith("text/") || lower.endsWith(".txt") || lower.endsWith(".md")
                        || lower.endsWith(".json") || lower.endsWith(".xml") || lower.endsWith(".log"))
                        && !lower.endsWith(".csv");
            case 3:
                return lower.endsWith(".csv") || type.equals("text/csv") || type.contains("comma-separated-values");
            case 4:
                return lower.endsWith(".doc") || lower.endsWith(".docx") || lower.endsWith(".rtf")
                        || lower.endsWith(".xls") || lower.endsWith(".xlsx") || lower.endsWith(".ppt")
                        || lower.endsWith(".pptx") || lower.endsWith(".wps") || lower.endsWith(".et")
                        || lower.endsWith(".dps");
            default:
                return true;
        }
    }

    private void renderRecents() {
        if (recentList == null) return;
        recentList.removeAllViews();
        visibleRecentUris.clear();
        if (recentUris.isEmpty()) {
            recentList.addView(label("No recent files yet. Tap Open a file to get started.", 14, false));
            updateSelectVisibleButton();
            return;
        }
        String query = recentSearchInput == null ? "" : recentSearchInput.getText().toString().trim().toLowerCase(Locale.ROOT);
        ArrayList<String> ordered = new ArrayList<>(recentUris);
        if (recentSortMode == 0) {
            Collections.reverse(ordered);
        } else if (recentSortMode == 1) {
            ordered.sort((left, right) -> nameOf(Uri.parse(left)).compareToIgnoreCase(nameOf(Uri.parse(right))));
        } else if (recentSortMode == 2) {
            ordered.sort((left, right) -> Long.compare(sizeOf(Uri.parse(right)), sizeOf(Uri.parse(left))));
        } else {
            ordered.sort((left, right) -> {
                boolean leftFavorite = favoriteUris.contains(left);
                boolean rightFavorite = favoriteUris.contains(right);
                if (leftFavorite != rightFavorite) return leftFavorite ? -1 : 1;
                return Integer.compare(recentUris.indexOf(right), recentUris.indexOf(left));
            });
        }
        int shown = 0;
        for (String value : ordered) {
            Uri uri = Uri.parse(value);
            String name = nameOf(uri);
            String mime = getContentResolver().getType(uri);
            if (!query.isEmpty() && !name.toLowerCase(Locale.ROOT).contains(query)) continue;
            if (!matchesRecentFilter(name, mime)) continue;
            if (favoritesOnly && !favoriteUris.contains(value)) continue;
            visibleRecentUris.add(value);
            shown++;
            if (mime == null) mime = "Unknown file type";
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(12), dp(8), dp(12), dp(8));
            card.setBackgroundColor(0xFFFFFFFF);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.setMargins(0, dp(4), 0, dp(4));
            recentList.addView(card, params);
            if (recentSelectionMode) {
                CheckBox check = new CheckBox(this);
                check.setText("Select this file");
                check.setChecked(selectedRecentUris.contains(value));
                check.setOnCheckedChangeListener((buttonView, checked) -> {
                    if (checked) {
                        if (!selectedRecentUris.contains(value)) selectedRecentUris.add(value);
                    } else {
                        selectedRecentUris.remove(value);
                    }
                    updateShareSelectedButton();
                });
                card.addView(check);
            }
            card.addView(label(name, 16, true));
            card.addView(label(mime + "  •  " + sizeText(sizeOf(uri)), 12, false));
            card.addView(label("Tap to open", 11, false));
            card.addView(action(favoriteUris.contains(value) ? "★ Remove favorite" : "☆ Add to favorites", v -> {
                if (favoriteUris.contains(value)) favoriteUris.remove(value);
                else favoriteUris.add(value);
                saveFavorites();
                renderRecents();
            }));
            card.addView(action("Share file", v -> {
                String shareMime = getContentResolver().getType(uri);
                if (shareMime == null || shareMime.trim().isEmpty()) shareMime = "*/*";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType(shareMime);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setClipData(ClipData.newUri(getContentResolver(), name, uri));
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    startActivity(Intent.createChooser(share, "Share " + name));
                } catch (Exception e) {
                    Toast.makeText(this, "No compatible sharing app was found.", Toast.LENGTH_LONG).show();
                }
            }));
            card.addView(action("Remove from recent", v -> {
                recentUris.remove(value);
                favoriteUris.remove(value);
                saveFavorites();
                saveRecents();
                renderRecents();
                Toast.makeText(this, "Removed from recent history. The original file was not deleted.", Toast.LENGTH_SHORT).show();
            }));
            card.setOnClickListener(v -> openSelected(Uri.parse(value)));
        }
        if (shown == 0) recentList.addView(label("No recent files match the current search and filter.", 14, false));
        updateSelectVisibleButton();
    }

    private void updateSelectVisibleButton() {
        if (selectVisibleButton == null) return;
        boolean allVisibleSelected = !visibleRecentUris.isEmpty();
        for (String value : visibleRecentUris) {
            if (!selectedRecentUris.contains(value)) {
                allVisibleSelected = false;
                break;
            }
        }
        selectVisibleButton.setText(allVisibleSelected ? "Deselect visible" : "Select visible");
        selectVisibleButton.setEnabled(recentSelectionMode && !visibleRecentUris.isEmpty());
    }

    private void updateShareSelectedButton() {
        if (shareSelectedButton == null) return;
        shareSelectedButton.setText("Share selected (" + selectedRecentUris.size() + ")");
        shareSelectedButton.setEnabled(!selectedRecentUris.isEmpty());
    }

    private void shareSelectedRecentFiles() {
        if (selectedRecentUris.isEmpty()) {
            Toast.makeText(this, "Select at least one file to share.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ArrayList<Uri> uris = new ArrayList<>();
            String commonMime = null;
            boolean mixedMime = false;
            for (String value : selectedRecentUris) {
                Uri uri = Uri.parse(value);
                uris.add(uri);
                String mime = getContentResolver().getType(uri);
                if (mime == null || mime.trim().isEmpty()) mime = "*/*";
                if (commonMime == null) commonMime = mime;
                else if (!commonMime.equals(mime)) mixedMime = true;
            }
            Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
            share.setType(mixedMime ? "*/*" : (commonMime == null ? "*/*" : commonMime));
            share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            ClipData clipData = ClipData.newUri(getContentResolver(), nameOf(uris.get(0)), uris.get(0));
            for (int i = 1; i < uris.size(); i++) clipData.addItem(new ClipData.Item(uris.get(i)));
            share.setClipData(clipData);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, "Share selected files"));
        } catch (Exception e) {
            Toast.makeText(this, "Could not share the selected files. Check that they are still available.", Toast.LENGTH_LONG).show();
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

    private void loadFavorites() {
        favoriteUris.clear();
        SharedPreferences favoritePrefs = getSharedPreferences(FAVORITES_PREFS, MODE_PRIVATE);
        String stored = favoritePrefs.getString(FAVORITES_KEY, "");
        if (stored == null || stored.isEmpty()) return;
        for (String uri : stored.split("\n")) {
            if (!uri.isEmpty() && !favoriteUris.contains(uri)) favoriteUris.add(uri);
        }
    }

    private void saveFavorites() {
        StringBuilder stored = new StringBuilder();
        for (String uri : favoriteUris) stored.append(uri).append("\n");
        getSharedPreferences(FAVORITES_PREFS, MODE_PRIVATE).edit()
                .putString(FAVORITES_KEY, stored.toString()).apply();
    }
}
