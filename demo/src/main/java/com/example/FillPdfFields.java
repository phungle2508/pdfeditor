package com.example;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import java.io.File;

public class FillPdfFields {

    public static void fillFields(File file, File output) throws Exception {
   

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

            // ✅ Fill text fields
            setFieldValue(form, "name", "asd");
            setFieldValue(form, "address", "123 Nguyen Trai, Hanoi");
            setFieldValue(form, "tax_code", "0123456789");
            setFieldValue(form, "phone", "0901234567");

            // ✅ Set checkboxes - Checked
            setFieldValue(form, "chkRes", "Yes"); // mark Resident
            setFieldValue(form, "chk1", "Yes"); // check box 1
            setFieldValue(form, "chkCCY2", "Yes"); // check CCY2
            setFieldValue(form, "chkMethod3", "Yes"); // pick Method3

            // ✅ Set checkboxes - Unchecked
            setFieldValue(form, "chkNonRes", "Off");
            setFieldValue(form, "chk2", "Off");
            setFieldValue(form, "chkCCY1", "Off");
            setFieldValue(form, "chkMethod1", "Off");

            // ✅ Save
            doc.save(output);
            System.out.println("✅ Filled PDF saved to: " + output.getAbsolutePath());
        }
    }



    /**
     * Helper method to safely set field values
     */
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