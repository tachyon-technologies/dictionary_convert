import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an example on how to get the x/y coordinates and size of each character in PDF
 */

public class GetCharLocationAndSize extends PDFTextStripper {

    private static CTRPr sinhalaUnicodeCTRPr = CTRPr.Factory.newInstance();                        // Set All types of fonts for sinhala types
    private static CTRPr fmAbhayaCTRPr = CTRPr.Factory.newInstance();
    private static ArrayList<Paragraph> pageParagraphs = new ArrayList<Paragraph>();
    private static ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();

    private static Paragraph leftParagraph;
    private static Paragraph rightParagraph;
    private static Paragraph currentParagraph;

    public GetCharLocationAndSize() throws IOException {

        CTFonts sinfonts = CTFonts.Factory.newInstance();
        sinfonts.setAscii("Iskoola Pota");
        sinfonts.setHAnsi("Iskoola Pota");
        sinfonts.setCs("Iskoola Pota");
        sinhalaUnicodeCTRPr.setRFonts(sinfonts);

        CTFonts abhayaFont = CTFonts.Factory.newInstance();
        abhayaFont.setAscii("FMAbhaya");
        abhayaFont.setHAnsi("FMAbhaya");
        abhayaFont.setCs("FMAbhaya");
        fmAbhayaCTRPr.setRFonts(abhayaFont);
    }

    /**
     * @throws IOException If there is an error parsing the document.
     */
    public static void main(String[] args) throws IOException {
        PDDocument document = null;
        String fileName = "dict.pdf";
        XWPFDocument doc = new XWPFDocument(); //a document for
        rightParagraph = new Paragraph();         //TODO should be removed if starting from page 1

        try {
            document = PDDocument.load(new File(fileName));
            PDFTextStripper stripper = new GetCharLocationAndSize();
            stripper.setSortByPosition(true);
            stripper.setStartPage(18);
//            stripper.setEndPage( document.getNumberOfPages() );
            stripper.setEndPage(document.getNumberOfPages());
            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            stripper.writeText(document, dummy);


            for (Paragraph para : paragraphs) {
                XWPFParagraph xwpfParagraph = doc.createParagraph();
                boolean pageBreak = false;
                for (Run run : para.textRuns) {
                    XWPFRun xwpfRun = xwpfParagraph.createRun();
                    String convText;
                    if (run.font.equals("FMABHAYA")) {
                        convText = FMAbhaya_UCSC.convert(run.getText());
                    } else {
                        convText = run.getText();
                    }
                    xwpfRun.setText(convText);
                    xwpfRun.getCTR().setRPr(sinhalaUnicodeCTRPr);
                    xwpfRun.setFontSize(run.fontSize);

                    if (run.fontWeight.equals("BOLD")) {
                        xwpfRun.setBold(true);
//                        System.out.println("Bold text");
                    }
                    if (run.pageBreak) {
                        pageBreak = true;           // page break moved out from paragraph to avoid scenarios simlar to page 26-27
                    }
//                    System.out.println(convText);
//                    System.out.println("new Run");
                }
                if (pageBreak){
                    XWPFRun xwpfRun = xwpfParagraph.createRun();
                    xwpfRun.addBreak(BreakType.PAGE);
                    pageBreak=false;
                }


//                System.out.println("new paragraph");
            }
            FileOutputStream out = new FileOutputStream("dict.docx");
            doc.write(out);
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    /**
     * Override the default functionality of PDFTextStripper.writeString()
     */
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for (TextPosition text : textPositions) {
//            String convText = FMAbhaya_UCSC.convert(text.getUnicode());
//            System.out.println(
//                    text.getUnicode() + " conv="+convText+" [(X=" + text.getXDirAdj() + ",Y=" +
//                    text.getYDirAdj() + " [(X'=" + text.getEndX() + ",Y'=" +
//                            text.getEndY() + ") height=" + text.getHeightDir() + " width=" +
//                    text.getWidthDirAdj() +  " font="+text.getFont()+ " font Size="+text.getFontSizeInPt()+"]");

            String rawText = text.getUnicode();
            String fontFamily = getFontFamily(text.getFont().toString());
            int fontSize = getFontSize(text.getHeightDir());
            String fontWeight = getFontWeight(text.getFont().toString());

            float startX = text.getXDirAdj();
            float startY = text.getYDirAdj();
            float endX = text.getEndX();
            float endY = text.getEndY();

            Paragraph returnedParagraph = getParagraph(startX, startY);
            if (returnedParagraph != null) {
                currentParagraph = returnedParagraph;
                Run textRun = currentParagraph.getRun(startX, startY, endX, endY, fontFamily, fontSize, fontWeight, rawText);
//                textRun.text += rawText;
            }


        }
    }

    @Override
    public void processPage(PDPage page) throws IOException {

//        System.out.println("start Page................ "+count);
//        PDRectangle pdRectangle = new PDRectangle(67,57,293,474); //316,531
//        page.setCropBox(pdRectangle);
        super.processPage(page);

        rightParagraph.getLastRun().pageBreak = true;
        leftParagraph = rightParagraph;  // To  get the remaining text of the previous page, if next doesn't start with a paragraph
                                            // Example in page 26-27

        // Sorting the paragraphs by column in each page
        for (Paragraph pagePara: pageParagraphs) {
            if (pagePara.column.equals("LEFT")){
                paragraphs.add(pagePara);
            }
        }
        for (Paragraph pagePara: pageParagraphs) {
            if (pagePara.column.equals("RIGHT")){
                paragraphs.add(pagePara);
            }
        }
        pageParagraphs = new ArrayList<Paragraph>();

    }


    private Paragraph getParagraph(float x, float y) {
        Paragraph returnParagraph = null;
        if (x < 203 && y > 70) {
            if (x > 67 && x < 70) {
                leftParagraph = new Paragraph();
                leftParagraph.column = "LEFT";
                pageParagraphs.add(leftParagraph);
            }
            returnParagraph = leftParagraph;
        } else if (x > 216 && x < 360 && y>70) {
            if (x > 216 && x < 219) {
                rightParagraph = new Paragraph();
                rightParagraph.column = "RIGHT";
                pageParagraphs.add(rightParagraph);

            }
            returnParagraph = rightParagraph;
        }
        else {
            return returnParagraph = currentParagraph;
        }

        return returnParagraph;
    }

    private String getFontFamily(String fontText) {
        if (fontText.contains("FMAbabld") || fontText.contains("FMAbhaya"))
            return "FMABHAYA";
        else
            return "UNICODE";
    }

    private String getFontWeight(String fontText) {
        if (fontText.contains("Bold")) {
            return "BOLD";
        } else
            return "LIGHT";
    }

    private int getFontSize(float height) {

        if (height > 5.0 && height < 5.1)
            return 12;
        else
            return 16;
    }
}
