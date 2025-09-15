package com.example;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

import java.io.File;

public class FillPdfFields {
    public static void main(String[] args) throws Exception {
        File file = new File("form3.pdf"); // original
        File output = new File("form3_filled.pdf");

        try (PDDocument doc = Loader.loadPDF(file)) {
            PDAcroForm form = doc.getDocumentCatalog().getAcroForm();

            if (form == null) {
                System.out.println("No AcroForm found.");
                return;
            }

            // ✅ Fill text fields
            form.getField("name").setValue("asd");
            form.getField("address").setValue("123 Nguyen Trai, Hanoi");
            form.getField("tax_code").setValue("0123456789");
            form.getField("phone").setValue("0901234567");

            // ✅ Set checkboxes
            // Checked
            form.getField("chkRes").setValue("Yes"); // mark Resident
            form.getField("chk1").setValue("Yes"); // check box 1
            form.getField("chkCCY2").setValue("Yes"); // check CCY2
            form.getField("chkMethod3").setValue("Yes"); // pick Method3

            // Unchecked
            form.getField("chkNonRes").setValue("Off");
            form.getField("chk2").setValue("Off");
            form.getField("chkCCY1").setValue("Off");
            form.getField("chkMethod1").setValue("Off");

            // ✅ Save
            doc.save(output);
            System.out.println("Filled PDF saved to: " + output.getAbsolutePath());
        }
    }
}
