package org.myoffice.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public final class MainActivity extends Activity {
    private static final int PICK_FILE = 4101;
    private static final String PREFS = "myoffice_recent";
    private static final String KEY = "uris";
    private static final int MAX_RECENTS = 12;
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
