package com.motologr.ui.expenses

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.motologr.R
import com.motologr.data.logging.Loggable
import java.io.File
import java.io.FileOutputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime

class GeneratePDF (val context: Context, val expensesLogs : List<Loggable>) {

    companion object {
        const val A4_PAGE_WIDTH = 596
        const val A4_PAGE_HEIGHT = 842
    }

    private lateinit var repairLogs : List<Loggable>
    private lateinit var serviceLogs : List<Loggable>
    private lateinit var wofLogs : List<Loggable>
    private lateinit var regLogs : List<Loggable>
    private lateinit var fuelLogs : List<Loggable>
    private lateinit var insuranceLogs : List<Loggable>

    init {
        // Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 100, BillLog = 201
        repairLogs = expensesLogs.filter { loggable -> loggable.classId == 0 }
        serviceLogs = expensesLogs.filter { loggable -> loggable.classId == 1 }
        wofLogs = expensesLogs.filter { loggable -> loggable.classId == 2 }
        regLogs = expensesLogs.filter { loggable -> loggable.classId == 3 }
        fuelLogs = expensesLogs.filter { loggable -> loggable.classId == 100 }
        insuranceLogs = expensesLogs.filter { loggable -> loggable.classId == 201}

        repairLogs.sortedByDescending { loggable -> loggable.sortableDate }
        serviceLogs.sortedByDescending { loggable -> loggable.sortableDate }
        wofLogs.sortedByDescending { loggable -> loggable.sortableDate }
        regLogs.sortedByDescending { loggable -> loggable.sortableDate }
        fuelLogs.sortedByDescending { loggable -> loggable.sortableDate }
        insuranceLogs.sortedByDescending { loggable -> loggable.sortableDate }
    }

    fun writeLoggablesToCanvas(canvas: Canvas, paint: Paint, logGroup: String, logs : List<Loggable>, titleOffset : Float) : Float {
        canvas.drawText(logGroup, 78F, titleOffset, paint)
        var logOffset = titleOffset + 20

        val format = SimpleDateFormat("dd/MM/yyyy")
        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.HALF_EVEN
        for (log in logs) {
            val text = format.format(log.sortableDate) + " - " + "$" + df.format(log.unitPrice)
            canvas.drawText(text, 78F, logOffset, paint)
            logOffset += 20
        }

        // End of block of records so create a gap
        logOffset += 20

        return logOffset
    }

    fun generatePDF() {
        // creating an object variable for our PDF document.
        val pdfDocument = PdfDocument()

        // two variables for paint "paint" is used for drawing shapes and we will use "title" for adding text in our PDF file.
        val title = Paint()

        // adding page info to our PDF file in which we will be passing our pageWidth, pageHeight and number of pages and after that we are calling it to create our PDF.
        val myPageInfo = PdfDocument.PageInfo.Builder(A4_PAGE_WIDTH, A4_PAGE_HEIGHT, 1).create()

        // setting start page for our PDF file.
        val myPage = pdfDocument.startPage(myPageInfo)

        // creating a variable for canvas from our page of PDF.
        val canvas = myPage.canvas

        // adding typeface for our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        title.color = ContextCompat.getColor(context, R.color.black)
        title.textSize = 18F

        // drawing text in our PDF file.
        canvas.drawText("MotoLogr NZ", 78F, 80F, title)
        canvas.drawText("Expenses Report", 78F, 100F, title)

        // creating another text and in this we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        title.color = ContextCompat.getColor(context, R.color.black)
        title.textSize = 15F

        var titleOffset : Float = 140F

        titleOffset = writeLoggablesToCanvas(canvas, title, "Repairs", repairLogs, titleOffset)
        titleOffset = writeLoggablesToCanvas(canvas, title, "Services", serviceLogs, titleOffset)
        titleOffset = writeLoggablesToCanvas(canvas, title, "Fuel", fuelLogs, titleOffset)
        titleOffset = writeLoggablesToCanvas(canvas, title, "Reg", regLogs, titleOffset)
        titleOffset = writeLoggablesToCanvas(canvas, title, "WOF", wofLogs, titleOffset)
        titleOffset = writeLoggablesToCanvas(canvas, title, "Insurance", insuranceLogs, titleOffset)

        // setting our text to center of PDF.
        //title.setTextAlign(Paint.Align.CENTER);
        // 596 w 842 h @ 140f height / 15f = 47 lines

        // finishing our page.
        pdfDocument.finishPage(myPage)

        // setting the name of our PDF file and its path.
        val currentTime = System.currentTimeMillis()

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "MotoLogr NZ Expenses - $currentTime.pdf"
        )

        try {
            // writing our PDF file to that location.
            pdfDocument.writeTo(FileOutputStream(file))

            // printing toast message on completion of PDF generation.
            Toast.makeText(
                context,
                "PDF file generated successfully in Downloads folder.",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e : Exception) {
            // handling error
            e.printStackTrace()
            Toast.makeText(context, "Failed to generate PDF file.", Toast.LENGTH_SHORT)
                .show()
        }

        // closing our PDF file.
        pdfDocument.close()
    }
}