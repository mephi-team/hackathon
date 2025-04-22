package team.mephi.hackathon.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.exceptions.NoTransactionsFoundException;
import team.mephi.hackathon.model.Transaction;
import team.mephi.hackathon.entity.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    public byte[] generatePdfReport(List<Transaction> transactions) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Transaction Report");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 10);
                float y = 650;
                for (Transaction transaction : transactions) {
                    String line = String.format("ID: %s | Amount: %.2f %s | Description: %s",
                            transaction.getId(), transaction.getAmount(),
                            transaction.getCurrency(), transaction.getDescription());
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText(line);
                    contentStream.endText();
                    y -= 20;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    public byte[] generateExcelReport(List<Transaction> transactions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            // Заголовки
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Amount", "Currency", "Description"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Данные
            int rowNum = 1;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(transaction.getId().toString());
                row.createCell(1).setCellValue(transaction.getAmount());
                row.createCell(2).setCellValue(transaction.getCurrency());
                row.createCell(3).setCellValue(transaction.getDescription());
            }

            // Авто-размер колонок
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }
}