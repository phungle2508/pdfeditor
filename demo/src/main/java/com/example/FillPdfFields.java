package com.example;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;

public class FillPdfFields {

    // Backward compatible method (no flatten)
    public static void fillFields(File file, File output) throws Exception {
        fillFields(file, output, false);
    }

    // New overload: choose to flatten (disable) after filling
    public static void fillFields(File file, File output, boolean flattenAfterFill) throws Exception {
        if (!file.exists()) {
            System.err.println("Error: form3.pdf not found in current directory");
            return;
        }

        try (PDDocument doc = Loader.loadPDF(file)) {
            PDAcroForm form = doc.getDocumentCatalog().getAcroForm();
            if (form == null) {
                System.out.println("No AcroForm found.");
                return;
            }

            System.out.println("=== Filling PDF Fields ===");

            // Ensure appearances get generated/updated
            form.setNeedAppearances(true);

            // ✅ Fill text fields
            setFieldValue(form, "name", "asd");
            setFieldValue(form, "address", "123 Nguyen Trai, Hanoi");
            setFieldValue(form, "tax_code", "0123456789");
            setFieldValue(form, "phone", "0901234567");

            // ✅ Set checkboxes
            setFieldValue(form, "chkRes", "Yes"); // checked
            setFieldValue(form, "chk1", "Yes");
            setFieldValue(form, "chkCCY2", "Yes");
            setFieldValue(form, "chkMethod3", "Yes");

            // Uncheck others
            setFieldValue(form, "chkNonRes", "Off");
            setFieldValue(form, "chk2", "Off");
            setFieldValue(form, "chkCCY1", "Off");
            setFieldValue(form, "chkMethod1", "Off");

            // Refresh appearances so text & ticks render correctly in viewers
            try {
                form.refreshAppearances();
            } catch (Throwable ignored) {
                // Older/newer PDFBox variations: safe to ignore if not available
            }

            // 🔒 Disable the form
            if (flattenAfterFill) {
                // Option A (strongest): flatten — removes interactivity entirely
                form.flatten();
                System.out.println("🔒 Form has been flattened (no longer editable).");
            } else {
                // Option B (soft): keep fields but make them read-only
                for (PDField f : form.getFieldTree()) {
                    try {
                        f.setReadOnly(true);
                    } catch (Exception ignored) {
                    }
                }
                System.out.println("🔒 All fields set to read-only (still form fields, but not editable).");
            }

            doc.save(output);
            System.out.println("✅ Filled PDF saved to: " + output.getAbsolutePath());
        }
    }

    /** Helper method to safely set field values */
    private static void setFieldValue(PDAcroForm form, String fieldName, String value) {
        try {
            PDField field = form.getField(fieldName);
            if (field != null) {
                field.setValue(value);
                System.out.println("✅ Set field '" + fieldName + "' = '" + value + "'");
            } else {
                System.out.println("⚠️  Field '" + fieldName + "' not found");
            }
        } catch (Exception e) {
            System.out.println("❌ Error setting field '" + fieldName + "': " + e.getMessage());
        }
    }
}
