package team.mephi.hackathon.controller;

import team.mephi.hackathon.entity.Transaction;

import java.io.IOException;
import java.util.List;

public interface ReportService {
    byte[] generatePdfReport(List<Transaction> transactions) throws IOException;
    byte[] generateExcelReport(List<Transaction> transactions) throws IOException;
}
