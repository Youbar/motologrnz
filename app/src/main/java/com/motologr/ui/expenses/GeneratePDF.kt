package com.motologr.ui.expenses

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
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
import com.motologr.data.EnumConstants
import com.motologr.data.logging.Loggable
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class GeneratePDF (val context: Context, private val expensesLogs : List<Loggable>) {

    companion object {
        const val A4_PAGE_WIDTH = 596
        const val A4_PAGE_HEIGHT = 842
        val decimalFormat = DecimalFormat("0.00")

        fun returnGSTComponent(amount : BigDecimal) : String {
            return decimalFormat.format(amount.multiply(3.0.toBigDecimal())
                .divide(23.0.toBigDecimal(),2, RoundingMode.HALF_UP))
        }

        fun returnExcGst(amount : BigDecimal) : String {
            val gst = amount.multiply(3.0.toBigDecimal())
                .divide(23.0.toBigDecimal(),2, RoundingMode.HALF_UP)

            return decimalFormat.format(amount.subtract(gst))
        }
    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val initTime = Calendar.getInstance().time

    // Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 100, BillLog = 201
    private var repairLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 0 }
    private var serviceLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 1 }
    private var wofLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 2 }
    private var regLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 3 }
    private var rucLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 4 }
    private var fuelLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 100 }
    private var insuranceLogs : List<Loggable> = expensesLogs.filter { loggable -> loggable.classId == 201}

    init {
        repairLogs.sortedByDescending { loggable -> loggable.sortableDate }
        serviceLogs.sortedByDescending { loggable -> loggable.sortableDate }
        wofLogs.sortedByDescending { loggable -> loggable.sortableDate }
        regLogs.sortedByDescending { loggable -> loggable.sortableDate }
        rucLogs.sortedByDescending { loggable -> loggable.sortableDate }
        fuelLogs.sortedByDescending { loggable -> loggable.sortableDate }
        insuranceLogs.sortedByDescending { loggable -> loggable.sortableDate }

        decimalFormat.roundingMode = RoundingMode.HALF_UP
    }

    private fun checkIfNeedNewPage(titleOffset: Float, expensesCategory: String) {
        if (titleOffset + 25 > 820) {
            canvas = startNewPage(paint, expensesReportTitle, expensesCategory)
        }
    }

    private fun writeLoggablesToCanvas(paint: Paint, logs : List<Loggable>, expensesCategory : String) : Float {
        checkIfNeedNewPage(titleOffset, "$expensesCategory - Cont.")

        val dateColumnX = 3F
        val itemColumnX = 153F
        val excGstX = 302F
        val gstX = 398F
        val incGstX = 499F
        canvas.drawText("Date", dateColumnX, 152F, paint)
        canvas.drawText("Item", itemColumnX, 152F, paint)
        canvas.drawText("Exc. GST", excGstX, 152F, paint)
        canvas.drawText("GST", gstX, 152F, paint)
        canvas.drawText("Inc. GST", incGstX, 152F, paint)
        titleOffset += 25

        for (log in logs) {
            val isProjected = log.sortableDate > initTime
            if (isProjected)
                continue

            val date = dateFormat.format(log.sortableDate)
            val item = log.returnNameByClassId()
            val lessGst = "$" + returnExcGst(log.unitPrice)
            val unitPrice = "$" + decimalFormat.format(log.unitPrice)
            val gstComponent = "$" + returnGSTComponent(log.unitPrice)

            canvas.drawText(date, dateColumnX, titleOffset, paint)
            canvas.drawText(item, itemColumnX, titleOffset, paint)
            canvas.drawText(lessGst, excGstX, titleOffset, paint)
            canvas.drawText(gstComponent, gstX, titleOffset, paint)
            canvas.drawText(unitPrice, incGstX, titleOffset, paint)

            checkIfNeedNewPage(titleOffset, expensesCategory)
            titleOffset += 25
        }

        return titleOffset
    }

    private fun writeTotalsToCanvas(paint: Paint, logs : List<Loggable>, expensesCategory : String) : Float {
        checkIfNeedNewPage(titleOffset, expensesCategory)

        val dateColumnX = 3F
        val itemColumnX = 153F
        val excGstX = 302F
        val gstX = 398F
        val incGstX = 499F
        canvas.drawText("Category", dateColumnX, 152F, paint)
        canvas.drawText("Percentage", itemColumnX, 152F, paint)
        canvas.drawText("Exc. GST", excGstX, 152F, paint)
        canvas.drawText("GST", gstX, 152F, paint)
        canvas.drawText("Inc. GST", incGstX, 152F, paint)
        titleOffset += 25

        val expenseCategories : List<TotalExpense> = listOf(
            TotalExpense(logs, TotalExpense.Companion.ExpenseCategory.Fuel),
            TotalExpense(logs, TotalExpense.Companion.ExpenseCategory.Compliance),
            TotalExpense(logs, TotalExpense.Companion.ExpenseCategory.Mechanical),
            TotalExpense(logs, TotalExpense.Companion.ExpenseCategory.Insurance),
            TotalExpense(logs, TotalExpense.Companion.ExpenseCategory.Total))

        for (expense in expenseCategories) {
            val category = expense.expenseCategory
            val percentage = expense.percentage + "%"
            val lessGst = "$" + expense.excGst
            val gstComponent = "$" + expense.gst
            val unitPrice = "$" + expense.incGst

            canvas.drawText(category, dateColumnX, titleOffset, paint)
            canvas.drawText(percentage, itemColumnX, titleOffset, paint)
            canvas.drawText(lessGst, excGstX, titleOffset, paint)
            canvas.drawText(gstComponent, gstX, titleOffset, paint)
            canvas.drawText(unitPrice, incGstX, titleOffset, paint)

            checkIfNeedNewPage(titleOffset, expensesCategory)
            titleOffset += 25

            // Add an extra space for totals at the end
            if (expense.expenseCategory == "Insurance")
                titleOffset += 25
        }

        titleOffset += 25

        // Total - 100% - Exc. GST - GST - Inc. GST

        return titleOffset
    }

    private var pageNumber = 1
    private var titleOffset = 152F
    private var paint = Paint()
    private lateinit var currentPage : Page
    private lateinit var canvas : Canvas

    private val pdfDocument = PdfDocument()

    private fun getBoldTypeface(paint: Paint, textSize : Float = 18F) : Paint {
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.textSize = textSize

        return paint
    }

    private fun getNormalTypeface(paint: Paint, textSize : Float = 15F) : Paint {
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.textSize = textSize

        return paint
    }

    private fun startNewPage(paint : Paint, expensesReportTitle : String, expensesCategory : String) : Canvas {
        if (pageNumber > 1)
            pdfDocument.finishPage(currentPage)

        val myPageInfo = PdfDocument.PageInfo.Builder(A4_PAGE_WIDTH, A4_PAGE_HEIGHT, pageNumber).create()
        currentPage = pdfDocument.startPage(myPageInfo)
        val canvas = currentPage.canvas

        val options = BitmapFactory.Options()
        options.inScaled = false
        val baseBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.expenses_report_base, options)
        canvas.drawBitmap(baseBitmap, null, RectF(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat()), null)

        // Draw header of page
        getBoldTypeface(paint, 48F)
        canvas.drawText(expensesReportTitle, 132F, 60F, paint)
        getBoldTypeface(paint, 36F)
        canvas.drawText(expensesCategory, 12F, 120F, paint)

        // Draw page number
        getBoldTypeface(paint, 24F)
        canvas.drawText("Page $pageNumber", 312F, 832F, paint)

        // Prepare to write further info
        getNormalTypeface(paint, 20F)
        titleOffset = 152F

        pageNumber += 1

        return canvas
    }

    private var expensesReportTitle = ""

    fun generatePDF(expensesReportTitle: String) {
        this.expensesReportTitle = expensesReportTitle

        // two variables for paint "paint" is used for drawing shapes and we will use "title" for adding text in our PDF file.
        val paint = Paint()

        var expensesCategory = "Fuel Expenses"
        canvas = startNewPage(paint, expensesReportTitle, expensesCategory)
        titleOffset = writeLoggablesToCanvas(paint, fuelLogs, expensesCategory)

        expensesCategory = "Compliance Expenses"
        canvas = startNewPage(paint, expensesReportTitle, expensesCategory)
        val complianceLogs = (regLogs + rucLogs + wofLogs).sortedByDescending { x -> x.sortableDate.time }
        titleOffset = writeLoggablesToCanvas(paint, complianceLogs, expensesCategory)

        expensesCategory = "Mechanical Expenses"
        canvas = startNewPage(paint, expensesReportTitle, "Mechanical Expenses")
        val mechanicalLogs = (repairLogs + serviceLogs).sortedByDescending { x -> x.sortableDate.time }
        titleOffset = writeLoggablesToCanvas(paint, mechanicalLogs, expensesCategory)

        expensesCategory = "Insurance Expenses"
        canvas = startNewPage(paint, expensesReportTitle, "Insurance Expenses")
        titleOffset = writeLoggablesToCanvas(paint, insuranceLogs, expensesCategory)

        expensesCategory = "Total Expenses By Category"
        canvas = startNewPage(paint, expensesReportTitle, "Total Expenses")
        titleOffset = writeTotalsToCanvas(paint, expensesLogs, expensesCategory)

        // finishing our page.
        pdfDocument.finishPage(currentPage)

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
                outputStream?.flush()
                outputStream?.close()
            } else {
                // writing our PDF file to that location.
                val fileOutputStream = FileOutputStream(file)
                val bufferedOutputStream = BufferedOutputStream(fileOutputStream)
                pdfDocument.writeTo(bufferedOutputStream)
                bufferedOutputStream.flush()
                bufferedOutputStream.close()
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

class TotalExpense(expenseLogs : List<Loggable>, category : ExpenseCategory) {
    companion object {
        enum class ExpenseCategory {
            Fuel,
            Compliance,
            Mechanical,
            Insurance,
            Total
        }
    }

    private lateinit var expenseCategoryLogs : List<Loggable>
    var expenseCategory : String = ""
    var percentage : String = ""
    var excGst : String = ""
    var gst : String = ""
    var incGst : String = ""

    init {
        val sumTotal : BigDecimal = expenseLogs.sumOf { x -> x.unitPrice }

        if (category == ExpenseCategory.Fuel) {
            expenseCategoryLogs = expenseLogs.filter { x -> x.classId == EnumConstants.LoggableType.Fuel.id }
            expenseCategory = "Fuel"
        } else if (category == ExpenseCategory.Compliance) {
            expenseCategoryLogs = expenseLogs.filter { x -> x.classId == EnumConstants.LoggableType.WOF.id
                    || x.classId == EnumConstants.LoggableType.Reg.id
                    || x.classId == EnumConstants.LoggableType.Ruc.id }
            expenseCategory = "Compliance"
        } else if (category == ExpenseCategory.Mechanical) {
            expenseCategoryLogs = expenseLogs.filter { x -> x.classId == EnumConstants.LoggableType.Repair.id
                    || x.classId == EnumConstants.LoggableType.Service.id }
            expenseCategory = "Mechanical"
        } else if (category == ExpenseCategory.Insurance) {
            expenseCategoryLogs = expenseLogs.filter { x -> x.classId == EnumConstants.LoggableType.InsuranceBill.id }
            expenseCategory = "Insurance"
        } else if (category == ExpenseCategory.Total) {
            expenseCategoryLogs = expenseLogs
            expenseCategory = "Total"
        }

        val categoryTotal : BigDecimal = expenseCategoryLogs.sumOf { x -> x.unitPrice }
        percentage = GeneratePDF.decimalFormat.format(categoryTotal.divide(sumTotal, 4, RoundingMode.HALF_UP)
            .multiply(100.0.toBigDecimal()))
        excGst = GeneratePDF.returnExcGst(categoryTotal)
        gst = GeneratePDF.returnGSTComponent(categoryTotal)
        incGst = GeneratePDF.decimalFormat.format(categoryTotal)
    }
}