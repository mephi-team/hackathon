package team.mephi.hackathon.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.entity.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public byte[] generatePdfReport(List<Transaction> transactions) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Отчет по транзакциям");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 10);
                float y = 650;
                for (Transaction t : transactions) {
                    String line = String.format(
                            "%s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s",
                            formatField(t.getPersonType()),
                            t.getOperationDate().format(DATE_FORMATTER),
                            formatField(t.getTransactionType()),
                            formatField(t.getComment()),
                            t.getAmount().toPlainString(),
                            formatField(t.getStatus()),
                            formatField(t.getSenderBank()),
                            formatField(t.getAccount()),
                            formatField(t.getReceiverBank()),
                            formatField(t.getReceiverInn()),
                            formatField(t.getReceiverAccount()),
                            formatField(t.getCategory()),
                            formatField(t.getReceiverPhone())
                    );
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText(line);
                    contentStream.endText();
                    y -= 15;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    @Override
    public byte[] generateExcelReport(List<Transaction> transactions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            // Заголовки
            String[] headers = {
                    "Тип лица", "Дата операции", "Тип транзакции", "Комментарий",
                    "Сумма", "Статус", "Банк отправителя", "Счет",
                    "Банк получателя", "ИНН получателя", "Счет получателя",
                    "Категория", "Телефон получателя"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Данные
            int rowNum = 1;
            for (Transaction t : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(formatField(t.getPersonType()));
                row.createCell(1).setCellValue(t.getOperationDate().format(DATE_FORMATTER));
                row.createCell(2).setCellValue(formatField(t.getTransactionType()));
                row.createCell(3).setCellValue(formatField(t.getComment()));
                row.createCell(4).setCellValue(t.getAmount().toPlainString());
                row.createCell(5).setCellValue(formatField(t.getStatus()));
                row.createCell(6).setCellValue(formatField(t.getSenderBank()));
                row.createCell(7).setCellValue(formatField(t.getAccount()));
                row.createCell(8).setCellValue(formatField(t.getReceiverBank()));
                row.createCell(9).setCellValue(formatField(t.getReceiverInn()));
                row.createCell(10).setCellValue(formatField(t.getReceiverAccount()));
                row.createCell(11).setCellValue(formatField(t.getCategory()));
                row.createCell(12).setCellValue(formatField(t.getReceiverPhone()));
            }

            // Авто-размер колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    private String formatField(Object value) {
        return value != null ? value.toString() : "";
    }
}