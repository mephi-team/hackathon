package team.mephi.hackathon.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import team.mephi.hackathon.entity.Transaction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceImplTest {

    private final ReportServiceImpl reportService = new ReportServiceImpl();

    @Test
    void generatePdfReport_withSingleTransaction_returnsValidPdf() throws IOException {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setComment("Test transaction");
        List<Transaction> transactions = List.of(transaction);

        // Act
        byte[] pdfBytes = reportService.generatePdfReport(transactions);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        assertTrue(new String(pdfBytes, StandardCharsets.US_ASCII).startsWith("%PDF"));

        try (PDDocument document = PDDocument.load(pdfBytes)) {
            String text = new PDFTextStripper().getText(document);
            assertTrue(text.contains("Transaction Report"));
            assertTrue(text.contains("Test transaction"));
        }
    }

    @Test
    void generatePdfReport_withEmptyList_returnsValidPdf() throws IOException {
        // Act
        byte[] pdfBytes = reportService.generatePdfReport(Collections.emptyList());

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        assertTrue(new String(pdfBytes, StandardCharsets.US_ASCII).startsWith("%PDF"));
    }

    @Test
    void generateExcelReport_withSingleTransaction_returnsValidExcel() throws IOException {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setComment("Test transaction");
        List<Transaction> transactions = List.of(transaction);

        // Act
        byte[] excelBytes = reportService.generateExcelReport(transactions);

        // Assert
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            assertEquals("ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("Amount", headerRow.getCell(1).getStringCellValue());
            assertEquals("Currency", headerRow.getCell(2).getStringCellValue());
            assertEquals("Description", headerRow.getCell(3).getStringCellValue());

            Row dataRow = sheet.getRow(1);
            assertEquals(transaction.getId().toString(), dataRow.getCell(0).getStringCellValue());
            assertEquals(transaction.getAmount().toString(), dataRow.getCell(1).getStringCellValue());
            assertEquals("Test transaction", dataRow.getCell(2).getStringCellValue());
        }
    }

    @Test
    void generateExcelReport_withEmptyList_returnsValidExcel() throws IOException {
        // Act
        byte[] excelBytes = reportService.generateExcelReport(Collections.emptyList());

        // Assert
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            assertEquals("ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("Amount", headerRow.getCell(1).getStringCellValue());
            assertEquals("Currency", headerRow.getCell(2).getStringCellValue());
            assertEquals("Description", headerRow.getCell(3).getStringCellValue());

            assertNull(sheet.getRow(1)); // Нет данных, только заголовок
        }
    }
}
