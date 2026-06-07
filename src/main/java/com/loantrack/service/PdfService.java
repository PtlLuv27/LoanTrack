package com.loantrack.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.loantrack.model.EMISchedule;
import com.loantrack.model.Loan;
import com.loantrack.repository.EMIScheduleRepository;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    private final EMIScheduleRepository emiRepository;

    public PdfService(EMIScheduleRepository emiRepository) {
        this.emiRepository = emiRepository;
    }

    public byte[] generateLoanStatement(Loan loan) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Create a new PDF document (A4 size with margins)
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 1. Add Bank Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Color.DARK_GRAY);
            Paragraph title = new Paragraph("🏦 LoanTrack Bank", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            document.add(new Paragraph("Official Loan Statement", FontFactory.getFont(FontFactory.HELVETICA, 14, Color.GRAY)));
            document.add(Chunk.NEWLINE);

            // 2. Add Borrower & Loan Summary
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph("Borrower Name: " + loan.getUser().getFullName(), boldFont));
            document.add(new Paragraph("Loan ID: #" + loan.getLoanId()));
            document.add(new Paragraph("Principal Amount: Rs. " + loan.getPrincipalAmount()));
            document.add(new Paragraph("Interest Rate: " + loan.getInterestRate() + "%"));
            document.add(new Paragraph("Outstanding Balance: Rs. " + loan.getOutstandingBalance()));
            document.add(Chunk.NEWLINE);

            // 3. Create Amortization Table
            PdfPTable table = new PdfPTable(6); // 6 columns
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            
            // Set Column Widths
            table.setWidths(new float[]{1f, 2.5f, 2f, 2f, 2f, 2f});

            // Table Header
            String[] headers = {"Mth", "Due Date", "EMI (Rs)", "Principal", "Interest", "Status"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE)));
                cell.setBackgroundColor(Color.DARK_GRAY);
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // 4. Fill Table with EMI Data
            List<EMISchedule> schedule = emiRepository.findByLoan_LoanIdOrderByEmiNumberAsc(loan.getLoanId());
            Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            for (EMISchedule emi : schedule) {
                table.addCell(new Phrase(String.valueOf(emi.getEmiNumber()), tableFont));
                table.addCell(new Phrase(emi.getDueDate().toString(), tableFont));
                table.addCell(new Phrase(String.valueOf(emi.getEmiAmount()), tableFont));
                table.addCell(new Phrase(String.valueOf(emi.getPrincipalComponent()), tableFont));
                table.addCell(new Phrase(String.valueOf(emi.getInterestComponent()), tableFont));
                table.addCell(new Phrase(emi.getPaymentStatus().name(), tableFont));
            }

            document.add(table);
            
            // 5. Footer
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("This is a computer-generated document. No signature is required.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}