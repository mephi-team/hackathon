package team.mephi.hackathon.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import team.mephi.hackathon.entity.PersonType;
import team.mephi.hackathon.entity.Transaction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceImplTest {

    private final ReportServiceImpl reportService = new ReportServiceImpl();

    @Test
    void generatePdfReport_withSingleTransaction_returnsValidPdf() throws IOException {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setComment("Test transaction");
        transaction.setOperationDate(LocalDateTime.now());
        List<Transaction> transactions = List.of(transaction);

        byte[] pdfBytes = reportService.generatePdfReport(transactions);

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
        byte[] pdfBytes = reportService.generatePdfReport(Collections.emptyList());

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        assertTrue(new String(pdfBytes, StandardCharsets.US_ASCII).startsWith("%PDF"));
    }

    @Test
    void generateExcelReport_withSingleTransaction_returnsValidExcel() throws IOException {
        Transaction transaction = new Transaction();
        transaction.setPersonType(PersonType.LEGAL);
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setComment("Test transaction");
        transaction.setOperationDate(LocalDateTime.now());
        List<Transaction> transactions = List.of(transaction);

        byte[] excelBytes = reportService.generateExcelReport(transactions);

        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            assertEquals("ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("Operation date", headerRow.getCell(1).getStringCellValue());
            assertEquals("Transaction type", headerRow.getCell(2).getStringCellValue());
            assertEquals("Comment", headerRow.getCell(3).getStringCellValue());
            assertEquals("Amount", headerRow.getCell(4).getStringCellValue());
            assertEquals("Status", headerRow.getCell(5).getStringCellValue());
            assertEquals("Sender bank", headerRow.getCell(6).getStringCellValue());
            assertEquals("Account", headerRow.getCell(7).getStringCellValue());
            assertEquals("Receiver bank", headerRow.getCell(8).getStringCellValue());
            assertEquals("Receiver inn", headerRow.getCell(9).getStringCellValue());
            assertEquals("Receiver account", headerRow.getCell(10).getStringCellValue());
            assertEquals("Category", headerRow.getCell(11).getStringCellValue());
            assertEquals("Receiver phone", headerRow.getCell(12).getStringCellValue());

            Row dataRow = sheet.getRow(1);
            assertEquals(transaction.getPersonType().toString(), dataRow.getCell(0).getStringCellValue());
            assertEquals(transaction.getComment(), dataRow.getCell(3).getStringCellValue());
        }
    }

    @Test
    void generateExcelReport_withEmptyList_returnsValidExcel() throws IOException {
        byte[] excelBytes = reportService.generateExcelReport(Collections.emptyList());

        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            assertEquals("ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("Operation date", headerRow.getCell(1).getStringCellValue());
            assertEquals("Transaction type", headerRow.getCell(2).getStringCellValue());
            assertEquals("Comment", headerRow.getCell(3).getStringCellValue());
            assertEquals("Amount", headerRow.getCell(4).getStringCellValue());
            assertEquals("Status", headerRow.getCell(5).getStringCellValue());
            assertEquals("Sender bank", headerRow.getCell(6).getStringCellValue());
            assertEquals("Account", headerRow.getCell(7).getStringCellValue());
            assertEquals("Receiver bank", headerRow.getCell(8).getStringCellValue());
            assertEquals("Receiver inn", headerRow.getCell(9).getStringCellValue());
            assertEquals("Receiver account", headerRow.getCell(10).getStringCellValue());
            assertEquals("Category", headerRow.getCell(11).getStringCellValue());
            assertEquals("Receiver phone", headerRow.getCell(12).getStringCellValue());

            assertNull(sheet.getRow(1)); // Только заголовки, данных нет
        }
    }
}