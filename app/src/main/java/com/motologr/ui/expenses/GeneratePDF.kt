package com.motologr.ui.expenses

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.motologr.R
import java.io.File
import java.io.FileOutputStream

class GeneratePDF (val context: Context) {

    fun generatePDF() {
        // creating an object variable for our PDF document.
        val pdfDocument = PdfDocument()

        // two variables for paint "paint" is used for drawing shapes and we will use "title" for adding text in our PDF file.
        val paint = Paint()
        val title = Paint()

        // adding page info to our PDF file in which we will be passing our pageWidth, pageHeight and number of pages and after that we are calling it to create our PDF.
        val mypageInfo = PdfDocument.PageInfo.Builder(792, 1120, 1).create()

        // setting start page for our PDF file.
        val myPage = pdfDocument.startPage(mypageInfo)

        // creating a variable for canvas from our page of PDF.
        val canvas = myPage.canvas

        /*    val scaledBmp = null

    if (scaledbmp != null) {
        // drawing our image on our PDF file.
        canvas.drawBitmap(scaledbmp, 56, 40, paint);
    } else {
        Log.e("generatePDF", "Bitmap is null. Skipping bitmap drawing.");
    }*/

        // adding typeface for our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        // setting text size which we will be displaying in our PDF file.
        title.textSize = 15.0F;

        // setting color of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(context, R.color.black));

        // drawing text in our PDF file.
        canvas.drawText("A portal for IT professionals.", 209F, 100F, title);
        canvas.drawText("Geeks for Geeks", 209F, 80F, title);

        // creating another text and in this we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(context, R.color.black));
        title.setTextSize(15F);

        // setting our text to center of PDF.
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("This is sample document which we have created.", 396F, 560F, title);

        // finishing our page.
        pdfDocument.finishPage(myPage);

        // setting the name of our PDF file and its path.
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "GFG.pdf"
        );

        try {
            // writing our PDF file to that location.
            pdfDocument.writeTo(FileOutputStream(file));

            // printing toast message on completion of PDF generation.
            Toast.makeText(
                context,
                "PDF file generated successfully.",
                Toast.LENGTH_SHORT
            ).show();
        } catch (e : Exception) {
            // handling error
            e.printStackTrace();
            Toast.makeText(context, "Failed to generate PDF file.", Toast.LENGTH_SHORT)
                .show();
        }

        // closing our PDF file.
        pdfDocument.close();

        //MediaStore.createWriteRequest(context.contentResolver, Uri)

    }
}