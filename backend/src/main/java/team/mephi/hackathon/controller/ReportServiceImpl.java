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
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ReportServiceImpl implements ReportService {

    /* ----------  общие константы ---------- */

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final NumberFormat MONEY_FORMAT =
            NumberFormat.getNumberInstance(new Locale("ru", "RU"));

    /** Заголовки колонок (одно место — и для PDF, и для Excel) */
    private static final String[] HEADERS = {
            "Тип лица", "Дата операции", "Тип транзакции", "Комментарий",
            "Сумма", "Статус", "Банк отправителя", "Счёт",
            "Банк получателя", "ИНН получателя", "Счёт получателя",
            "Категория", "Телефон получателя"
    };

    /* ----------  PDF ---------- */

    @Override
    public byte[] generatePdfReport(List<Transaction> transactions) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            float marginLeft = 50;
            float cursorY   = 750;          // старт сверху
            final float lineHeight = 14;
            final float bottomMargin = 50;

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {

                // ----- заголовок отчёта -----
                cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
                writeLine(cs, marginLeft, cursorY, "Отчёт по транзакциям");
                cursorY -= (lineHeight * 2);

                // ----- строка-шапка -----
                cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
                writeLine(cs, marginLeft, cursorY, String.join(" | ", HEADERS));
                cursorY -= lineHeight;

                // ----- данные -----
                cs.setFont(PDType1Font.HELVETICA, 9);

                for (Transaction t : transactions) {

                    // перенос на новую страницу при нехватке места
                    if (cursorY < bottomMargin) {
                        cs.close();                        // закрываем текущий stream
                        page = new PDPage();
                        document.addPage(page);
                        cursorY = 750;

                        try (PDPageContentStream next = new PDPageContentStream(document, page)) {
                            cs = next;   // переключаем ссылку

                            // повторяем шапку таблицы на новой странице
                            cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
                            writeLine(cs, marginLeft, cursorY, String.join(" | ", HEADERS));
                            cursorY -= lineHeight;

                            cs.setFont(PDType1Font.HELVETICA, 9);
                        }
                    }

                    String row = String.format(
                            "%s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s",
                            formatField(t.getPersonType()),
                            t.getOperationDate().format(DATE_FORMATTER),
                            formatField(t.getTransactionType()),
                            formatField(t.getComment()),
                            MONEY_FORMAT.format(t.getAmount()),
                            formatField(t.getStatus()),
                            formatField(t.getSenderBank()),
                            formatField(t.getAccount()),
                            formatField(t.getReceiverBank()),
                            formatField(t.getReceiverInn()),
                            formatField(t.getReceiverAccount()),
                            formatField(t.getCategory()),
                            formatField(t.getReceiverPhone())
                    );

                    writeLine(cs, marginLeft, cursorY, row);
                    cursorY -= lineHeight;
                }
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                document.save(baos);
                return baos.toByteArray();
            }
        }
    }

    /* ----------  Excel ---------- */

    @Override
    public byte[] generateExcelReport(List<Transaction> transactions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            // ----- стили -----
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            DataFormat poiFormat = workbook.createDataFormat();

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(poiFormat.getFormat("#,##0.00"));

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(poiFormat.getFormat("dd.mm.yyyy hh:mm"));

            // ----- заголовок (строка 0) -----
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // ----- данные -----
            int rowNum = 1;
            for (Transaction t : transactions) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(formatField(t.getPersonType()));

                Cell dateCell = row.createCell(1);
                dateCell.setCellValue(t.getOperationDate().format(DATE_FORMATTER));
                dateCell.setCellStyle(dateStyle);

                row.createCell(2).setCellValue(formatField(t.getTransactionType()));
                row.createCell(3).setCellValue(formatField(t.getComment()));

                Cell amountCell = row.createCell(4);
                amountCell.setCellValue(t.getAmount().doubleValue());
                amountCell.setCellStyle(moneyStyle);

                row.createCell(5).setCellValue(formatField(t.getStatus()));
                row.createCell(6).setCellValue(formatField(t.getSenderBank()));
                row.createCell(7).setCellValue(formatField(t.getAccount()));
                row.createCell(8).setCellValue(formatField(t.getReceiverBank()));
                row.createCell(9).setCellValue(formatField(t.getReceiverInn()));
                row.createCell(10).setCellValue(formatField(t.getReceiverAccount()));
                row.createCell(11).setCellValue(formatField(t.getCategory()));
                row.createCell(12).setCellValue(formatField(t.getReceiverPhone()));
            }

            // авто-ширина
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                workbook.write(baos);
                return baos.toByteArray();
            }
        }
    }

    /* ----------  helpers ---------- */

    private static void writeLine(PDPageContentStream cs, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private static String formatField(Object value) {
        return value != null ? value.toString() : "";
    }
}
