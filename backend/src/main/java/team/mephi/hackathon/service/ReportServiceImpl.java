package team.mephi.hackathon.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.controller.ReportService;
import team.mephi.hackathon.entity.Transaction;

@Service
public class ReportServiceImpl implements ReportService {

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  private static final NumberFormat MONEY_FORMAT =
      NumberFormat.getNumberInstance(new Locale("ru", "RU"));

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
          String row =
              String.format(
                  "%s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s",
                  formatField(transaction.getPersonType()),
                  transaction.getOperationDate().format(DATE_FORMATTER),
                  formatField(transaction.getTransactionType()),
                  formatField(transaction.getComment()),
                  MONEY_FORMAT.format(transaction.getAmount()),
                  formatField(transaction.getStatus()),
                  formatField(transaction.getSenderBank()),
                  formatField(transaction.getAccount()),
                  formatField(transaction.getReceiverBank()),
                  formatField(transaction.getReceiverInn()),
                  formatField(transaction.getReceiverAccount()),
                  formatField(transaction.getCategory()),
                  formatField(transaction.getReceiverPhone()));
          contentStream.beginText();
          contentStream.newLineAtOffset(50, y);
          contentStream.showText(row);
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
      String[] columns = {
        "ID",
        "Operation date",
        "Transaction type",
        "Comment",
        "Amount",
        "Status",
        "Sender bank",
        "Account",
        "Receiver bank",
        "Receiver inn",
        "Receiver account",
        "Category",
        "Receiver phone"
      };
      for (int i = 0; i < columns.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(columns[i]);
      }

      DataFormat poiFormat = workbook.createDataFormat();

      CellStyle moneyStyle = workbook.createCellStyle();
      moneyStyle.setDataFormat(poiFormat.getFormat("#,##0.00"));

      CellStyle dateStyle = workbook.createCellStyle();
      dateStyle.setDataFormat(poiFormat.getFormat("dd.mm.yyyy hh:mm"));

      // Данные
      int rowNum = 1;
      for (Transaction transaction : transactions) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(formatField(transaction.getPersonType()));

        Cell dateCell = row.createCell(1);
        dateCell.setCellValue(transaction.getOperationDate().format(DATE_FORMATTER));
        dateCell.setCellStyle(dateStyle);

        row.createCell(2).setCellValue(formatField(transaction.getTransactionType()));
        row.createCell(3).setCellValue(formatField(transaction.getComment()));

        Cell amountCell = row.createCell(4);
        amountCell.setCellValue(transaction.getAmount().doubleValue());
        amountCell.setCellStyle(moneyStyle);

        row.createCell(5).setCellValue(formatField(transaction.getStatus()));
        row.createCell(6).setCellValue(formatField(transaction.getSenderBank()));
        row.createCell(7).setCellValue(formatField(transaction.getAccount()));
        row.createCell(8).setCellValue(formatField(transaction.getReceiverBank()));
        row.createCell(9).setCellValue(formatField(transaction.getReceiverInn()));
        row.createCell(10).setCellValue(formatField(transaction.getReceiverAccount()));
        row.createCell(11).setCellValue(formatField(transaction.getCategory()));
        row.createCell(12).setCellValue(formatField(transaction.getReceiverPhone()));
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

  private static String formatField(Object value) {
    return value != null ? value.toString() : "";
  }
}
