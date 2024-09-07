package com.motologr.ui.expenses

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.motologr.R
import com.motologr.data.logging.Loggable
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar

class GeneratePDF (val context: Context, private val expensesLogs : List<Loggable>) {

    companion object {
        const val A4_PAGE_WIDTH = 596
        const val A4_PAGE_HEIGHT = 842
    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val decimalFormat = DecimalFormat("0.00")
    private val initTime = Calendar.getInstance().time

    // Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 100, BillLog = 201
    private var repairLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 0 }
    private var serviceLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 1 }
    private var wofLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 2 }
    private var regLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 3 }
    private var fuelLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 100 }
    private var insuranceLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 201}

    init {
        repairLogs.sortedByDescending { loggable -> loggable.sortableDate }
        serviceLogs.sortedByDescending { loggable -> loggable.sortableDate }
        wofLogs.sortedByDescending { loggable -> loggable.sortableDate }
        regLogs.sortedByDescending { loggable -> loggable.sortableDate }
        fuelLogs.sortedByDescending { loggable -> loggable.sortableDate }
        insuranceLogs.sortedByDescending { loggable -> loggable.sortableDate }

        decimalFormat.roundingMode = RoundingMode.HALF_EVEN
    }

    private fun returnGSTComponent(amount : BigDecimal) : String {
        return decimalFormat.format(amount.multiply(3.0.toBigDecimal())
            .divide(23.0.toBigDecimal(),2, RoundingMode.HALF_EVEN))
    }

    private fun checkIfNeedNewPage(titleOffset: Float) {
        if (titleOffset + 20 > 820) {
            canvas = startNewPage(paint, expensesReportTitle)
        }
    }

    private fun writeLoggablesToCanvas(paint: Paint, logGroup: String, logs : List<Loggable>) : Float {
        checkIfNeedNewPage(titleOffset)

        val logGroupTile = "$logGroup - Date | Price | GST"

        canvas.drawText(logGroupTile, 78F, titleOffset, paint)
        titleOffset += 20

        for (log in logs) {
            val date = dateFormat.format(log.sortableDate)
            val unitPrice = "$" + decimalFormat.format(log.unitPrice)
            val gstComponent = "$" + returnGSTComponent(log.unitPrice)
            val isProjected = log.sortableDate > initTime

            var text = "$date | $unitPrice | $gstComponent"
            if (isProjected)
                text += " | PROJECTED"

            checkIfNeedNewPage(titleOffset)
            canvas.drawText(text, 78F, titleOffset, paint)
            titleOffset += 20
        }

        // End of block of records so create a gap
        return titleOffset + 20
    }

    private fun writeTotalToCanvas(paint: Paint, total : BigDecimal) : Float {
        checkIfNeedNewPage(titleOffset)

        val totalPrice = "$" + decimalFormat.format(total)
        val gstComponent = "$" + returnGSTComponent(total)

        val totalString = "Total - $totalPrice - $gstComponent"
        canvas.drawText(totalString, 78F, titleOffset, paint)

        // End of block of records so create a gap
        return titleOffset + 40
    }

    private var pageNumber = 1
    private var titleOffset = 140F
    private var paint = Paint()
    private lateinit var currentpage : Page
    private lateinit var canvas : Canvas

    private val pdfDocument = PdfDocument()

    private fun getBoldTypeface(paint: Paint) : Paint {
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.textSize = 18F

        return paint
    }

    private fun getNormalTypeface(paint: Paint) : Paint {
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.textSize = 15F

        return paint
    }

    private fun startNewPage(paint : Paint, expensesReportTitle : String) : Canvas {
        if (pageNumber > 1)
            pdfDocument.finishPage(currentpage)

        val myPageInfo = PdfDocument.PageInfo.Builder(A4_PAGE_WIDTH, A4_PAGE_HEIGHT, pageNumber).create()
        currentpage = pdfDocument.startPage(myPageInfo)
        val canvas = currentpage.canvas

        // Draw header of page
        getBoldTypeface(paint)
        canvas.drawText("MotoLogr NZ", 78F, 80F, paint)
        canvas.drawText("Page $pageNumber", 448F, 80F, paint)
        canvas.drawText(expensesReportTitle, 78F, 100F, paint)

        // Prepare to write further info
        getNormalTypeface(paint)
        titleOffset = 140F

        pageNumber += 1

        return canvas
    }

    private var expensesReportTitle = ""

    fun generatePDF(expensesReportTitle: String) {
        this.expensesReportTitle = expensesReportTitle

        // two variables for paint "paint" is used for drawing shapes and we will use "title" for adding text in our PDF file.
        var paint = Paint()

        canvas = startNewPage(paint, expensesReportTitle)

        titleOffset = writeLoggablesToCanvas(paint, "Repairs", repairLogs)
        titleOffset = writeLoggablesToCanvas(paint, "Services", serviceLogs)
        titleOffset = writeLoggablesToCanvas(paint, "Fuel", fuelLogs)
        titleOffset = writeLoggablesToCanvas(paint, "Reg", regLogs)
        titleOffset = writeLoggablesToCanvas(paint, "WOF", wofLogs)
        titleOffset = writeLoggablesToCanvas(paint, "Insurance", insuranceLogs)

        val total = expensesLogs.sumOf { x -> x.unitPrice}
        titleOffset = writeTotalToCanvas(paint, total)

        // setting our text to center of PDF.
        //title.setTextAlign(Paint.Align.CENTER);
        // 596 w 842 h @ 140f height / 15f = 47 lines

        // finishing our page.
        pdfDocument.finishPage(currentpage)

        // setting the name of our PDF file and its path.
        val currentTime = System.currentTimeMillis()

        val displayName = "MotoLogr NZ Expenses - $currentTime.pdf"
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), displayName)

        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        var uri : Uri? = null

        if (Build.VERSION.SDK_INT >= 29) {
            uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        }

        try {
            if (uri != null) {
                val outputStream = resolver.openOutputStream(uri)
                pdfDocument.writeTo(outputStream)
            } else {
                // writing our PDF file to that location.
                pdfDocument.writeTo(FileOutputStream(file))
            }

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