package com.caventilator.domain.usecase

import android.content.Context
import android.net.Uri
import com.caventilator.R
import com.caventilator.domain.model.VentilationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream
import javax.inject.Inject

class ExportExcelUseCase @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    /**
     * Génère et écrit un fichier .xlsx dans le Uri fourni par le SAF.
     * @param uri  Uri obtenu via ActivityResultContracts.CreateDocument
     * @param result  Résultat de la ventilation à exporter
     */
    fun exportToUri(uri: Uri, result: VentilationResult) {
        val stream: OutputStream = context.contentResolver.openOutputStream(uri)
            ?: error("Impossible d'ouvrir le flux pour l'URI : $uri")

        stream.use { writeWorkbook(it, result) }
    }

    private fun writeWorkbook(stream: OutputStream, result: VentilationResult) {
        val wb: Workbook = XSSFWorkbook()
        val sheet = wb.createSheet(context.getString(R.string.excel_sheet_title))

        // ── Styles ───────────────────────────────────────────────────────────
        val titleStyle = wb.createCellStyle().apply {
            fillForegroundColor = IndexedColors.DARK_BLUE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            setFont(wb.createFont().also {
                it.bold = true
                it.color = IndexedColors.WHITE.index
                it.fontHeightInPoints = 14
            })
        }

        val headerStyle = wb.createCellStyle().apply {
            fillForegroundColor = IndexedColors.CORNFLOWER_BLUE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            setFont(wb.createFont().also {
                it.bold = true
                it.color = IndexedColors.WHITE.index
            })
            borderBottom = BorderStyle.THIN
        }

        val dataStyle = wb.createCellStyle().apply {
            alignment = HorizontalAlignment.LEFT
            borderBottom = BorderStyle.HAIR
        }

        val currencyStyle = wb.createCellStyle().apply {
            dataFormat = wb.createDataFormat().getFormat("#,##0.00 \"€\"")
            borderBottom = BorderStyle.HAIR
        }

        val totalStyle = wb.createCellStyle().apply {
            fillForegroundColor = IndexedColors.LIGHT_TURQUOISE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            dataFormat = wb.createDataFormat().getFormat("#,##0.00 \"€\"")
            setFont(wb.createFont().also { it.bold = true })
            borderTop = BorderStyle.MEDIUM
            borderBottom = BorderStyle.MEDIUM
        }

        val residualStyle = wb.createCellStyle().apply {
            fillForegroundColor = IndexedColors.ROSE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            dataFormat = wb.createDataFormat().getFormat("#,##0.00 \"€\"")
            setFont(wb.createFont().also { it.bold = true })
        }

        var rowIdx = 0

        // ── Titre principal ───────────────────────────────────────────────────
        val titleRow = sheet.createRow(rowIdx++)
        titleRow.heightInPoints = 28f
        titleRow.createCell(0).apply {
            val semesterLabel = context.getString(result.semester.labelRes)
            setCellValue(context.getString(R.string.excel_title_format, result.department.name, semesterLabel))
            cellStyle = titleStyle
        }
        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 4))

        rowIdx++ // ligne vide

        // ── Informations département ──────────────────────────────────────────
        fun infoRow(label: String, value: String) {
            val r = sheet.createRow(rowIdx++)
            r.createCell(0).apply { setCellValue(label); cellStyle = headerStyle }
            r.createCell(1).apply { setCellValue(value); cellStyle = dataStyle }
        }

        infoRow(context.getString(R.string.label_department), result.department.name)
        infoRow(context.getString(R.string.label_semester), context.getString(result.semester.labelRes))
        infoRow(context.getString(R.string.label_target_ca), "%.2f €".format(result.targetCA))

        rowIdx++ // ligne vide

        // ── En-têtes tableau ──────────────────────────────────────────────────
        val headers = listOf(
            context.getString(R.string.excel_header_product),
            context.getString(R.string.excel_header_unit_price),
            context.getString(R.string.excel_header_quantity),
            context.getString(R.string.excel_header_amount)
        )
        val headerRow = sheet.createRow(rowIdx++)
        headers.forEachIndexed { col, text ->
            headerRow.createCell(col).apply {
                setCellValue(text)
                cellStyle = headerStyle
            }
        }

        // ── Lignes produits ───────────────────────────────────────────────────
        result.items.forEach { item ->
            val row = sheet.createRow(rowIdx++)
            row.createCell(0).apply { setCellValue(item.product.name); cellStyle = dataStyle }
            row.createCell(1).apply { setCellValue(item.product.unitPrice); cellStyle = currencyStyle }
            row.createCell(2).apply { setCellValue(item.quantity.toDouble()); cellStyle = dataStyle }
            row.createCell(3).apply { setCellValue(item.totalAmount); cellStyle = currencyStyle }
        }

        rowIdx++ // ligne vide

        // ── Synthèse ──────────────────────────────────────────────────────────
        val totalRow = sheet.createRow(rowIdx++)
        totalRow.createCell(0).apply { setCellValue(context.getString(R.string.excel_total_ventilated)); cellStyle = totalStyle }
        totalRow.createCell(1).apply { cellStyle = totalStyle }
        totalRow.createCell(2).apply { cellStyle = totalStyle }
        totalRow.createCell(3).apply { setCellValue(result.totalVentilated); cellStyle = totalStyle }

        val residRow = sheet.createRow(rowIdx)
        residRow.createCell(0).apply { setCellValue(context.getString(R.string.excel_residual)); cellStyle = residualStyle }
        residRow.createCell(1).apply { cellStyle = residualStyle }
        residRow.createCell(2).apply { cellStyle = residualStyle }
        residRow.createCell(3).apply { setCellValue(result.residual); cellStyle = residualStyle }

        // ── Largeurs colonnes ─────────────────────────────────────────────────
        sheet.setColumnWidth(0, 36 * 256)
        sheet.setColumnWidth(1, 18 * 256)
        sheet.setColumnWidth(2, 14 * 256)
        sheet.setColumnWidth(3, 22 * 256)

        wb.write(stream)
        wb.close()
    }
}
