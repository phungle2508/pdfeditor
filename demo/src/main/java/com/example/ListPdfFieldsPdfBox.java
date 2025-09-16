package com.example;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.interactive.form.*;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.io.File;
import java.util.List;

public class ListPdfFieldsPdfBox {

    /**
     * Lists all fields in the PDF form
     */
    public static void listFields(File file) throws Exception {
      

        if (!file.exists()) {
            System.err.println("Error: form3.pdf not found in current directory");
            return;
        }

        try (PDDocument doc = Loader.loadPDF(file)) {
            PDAcroForm form = doc.getDocumentCatalog().getAcroForm();

            if (form == null) {
                System.out.println("No AcroForm found (this PDF may be flat or XFA-only).");
                return;
            }

            // Optional: detect XFA
            if (form.getXFA() != null) {
                System.out.println("Warning: PDF contains XFA; AcroForm fields may be empty/unsupported.");
            }

            System.out.println("=== PDF Fields Analysis ===");
            PDFieldTree fieldTree = form.getFieldTree();
            int fieldCount = 0;

            for (PDField field : fieldTree) {
                fieldCount++;
                dumpField(field, doc);
                System.out.println(); // Add spacing between fields
            }

            System.out.println("Total fields found: " + fieldCount);
        }
    }



    private static void dumpField(PDField field, PDDocument doc) {
        String fqName = field.getFullyQualifiedName();
        String type = field.getClass().getSimpleName(); // PDTextField, PDCheckBox, PDRadioButton, PDComboBox, etc.
        String value = "";

        try {
            value = field.getValueAsString();
        } catch (Exception ignored) {
            // Handle cases where value cannot be retrieved
        }

        System.out.printf("📋 Name: %s | Type: %s | Value: %s%n", fqName, type, value);

        // Show widget locations & pages
        List<PDAnnotationWidget> widgets = field.getWidgets();
        for (int i = 0; i < widgets.size(); i++) {
            PDAnnotationWidget w = widgets.get(i);
            PDRectangle r = w.getRectangle();
            Integer pageIndex = pageIndexOf(w, doc);
            System.out.printf("   📍 Widget #%d: page=%s rect=[%.2f, %.2f, %.2f, %.2f]%n",
                    i + 1, pageIndex == null ? "?" : pageIndex + 1,
                    r.getLowerLeftX(), r.getLowerLeftY(), r.getUpperRightX(), r.getUpperRightY());
        }

        // List options for choice fields
        if (field instanceof PDChoice) {
            PDChoice choice = (PDChoice) field;
            try {
                List<String> opts = choice.getOptionsDisplayValues();
                if (opts != null && !opts.isEmpty()) {
                    System.out.println("   🔽 Options: " + opts);
                }
            } catch (Exception e) {
                System.out.println("   🔽 Options: [Could not retrieve]");
            }
        }

        // Handle radio button fields
        if (field instanceof PDRadioButton) {
            PDRadioButton radio = (PDRadioButton) field;
            try {
                List<String> exportValues = radio.getExportValues();
                if (exportValues != null && !exportValues.isEmpty()) {
                    System.out.println("   🔘 Radio export values: " + exportValues);
                }
            } catch (Exception e) {
                System.out.println("   🔘 Radio export values: [Could not retrieve]");
            }
        }

        // Handle checkbox fields
        if (field instanceof PDCheckBox) {
            PDCheckBox cb = (PDCheckBox) field;
            try {
                String onValue = cb.getOnValue();
                System.out.println("   ☑️  Checkbox onValue: " + onValue);
            } catch (Exception e) {
                System.out.println("   ☑️  Checkbox onValue: [Could not retrieve]");
            }
        }
    }

    // Best-effort page lookup for PDFBox 3.x
    private static Integer pageIndexOf(PDAnnotationWidget widget, PDDocument doc) {
        try {
            PDPage widgetPage = widget.getPage();
            if (widgetPage == null) {
                return null;
            }

            PDPageTree pages = doc.getPages();
            int i = 0;
            for (PDPage p : pages) {
                if (p.equals(widgetPage)) {
                    return i;
                }
                i++;
            }
        } catch (Exception ignored) {
            // Return null if page lookup fails
        }
        return null;
    }
}