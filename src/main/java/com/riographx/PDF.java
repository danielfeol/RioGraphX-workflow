/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riographx;


import java.util.Date;
import java.util.List;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import static com.riographx.WorkFlow.ImageDir;
import static com.riographx.WorkFlow.ReportDir;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.spark.sql.Row;


public class PDF {
    
    private static final Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static final Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);
    private static final Font small = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL);    
    private static final Font verysmallBold = new Font(Font.FontFamily.TIMES_ROMAN, 10,
            Font.BOLD); 
    private static final Font verysmall = new Font(Font.FontFamily.TIMES_ROMAN, 10,
            Font.NORMAL); 

    static void PDF(Document document, int id_submit) throws FileNotFoundException{
            FileOutputStream f = new FileOutputStream(ReportDir + id_submit +".pdf");
            try {PdfWriter.getInstance(document, f);
        } catch (DocumentException ex) {
            Logger.getLogger(WorkFlow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    static void addMetaData(Document document) throws BadElementException, IOException, DocumentException {
        float scale = (float) 75.00;
        Image figure = Image.getInstance(ImageDir + "logo.png");
        figure.scalePercent(scale);
        document.add(figure);

    }

    static void addTitlePage(Document document, int id_submit,
            String latex,           
            boolean lookformax,
            boolean triangle_free,
            boolean connected,
            boolean bipartite)
            throws DocumentException, BadElementException, IOException {
String min_max, triangulo, conexo, bipartido;
        
        if(lookformax){min_max = "Maximum";}else{min_max="Minimum";}
        if(triangle_free){triangulo = "Yes";}else{triangulo="No";}
        if(connected){conexo = "Yes";}else{conexo="No";}
        if(bipartite){bipartido = "Yes";}else{bipartido="No";}
        
//        // Second parameter is the number of the chapter
        Paragraph preface = new Paragraph();
        Image figure = Image.getInstance(ImageDir + "logo.png");
        figure.scalePercent(75);

        Paragraph paragraph = new Paragraph();
        
        preface.add(figure);
        preface.add(new Paragraph("Report #id: " + id_submit, catFont));
        preface.add(
                new Paragraph("------------------------------------------------------------------------"
                        + "----------------------------------------------------------                  "));
        preface.add(new Phrase("Objective function: ", smallBold));
        preface.add(new Phrase(latex, small));
        preface.add(new Chunk("\n"));
        preface.add(new Phrase("Optimization: ", smallBold));
        preface.add(new Phrase(min_max, small));
        preface.add(new Chunk("\n"));
        preface.add(new Phrase("Only generate triangle free? ", smallBold));
        preface.add(new Phrase(triangulo, small));
        preface.add(new Chunk("\n"));
        preface.add(new Phrase("Only generate connected graphs? ", smallBold));
        preface.add(new Phrase(conexo, small));
        preface.add(new Chunk("\n"));
        preface.add(new Phrase("Only generate bipartite graphs? ", smallBold));
        preface.add(new Phrase(bipartido, small));
        preface.add(new Paragraph(" "));
        preface.add(new Paragraph(
        "Report generated in " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        smallBold));
        preface.add(new Paragraph(" "));
        document.add(preface);
    }

    static void addContent(Document document, List<Row> graphList, int id_submit,
            String latex, 
            int max_results) throws DocumentException, BadElementException, IOException {
        Row s1 = null;    
        String[] tempArray;
        String a =  null;
        int index = 0;
        Paragraph paragraph = new Paragraph();
        Chapter subCatPart = new Chapter(new Paragraph(), 1);
                
        // add a table
        for (int i = 0 ; i < max_results; i++){           
            s1 = graphList.get(i);
            a = s1.toString();        
            tempArray = a.split(",");
            if (index != Integer.parseInt(tempArray[1])){
                subCatPart.add(new Paragraph("Order: #" + tempArray[1], subFont));
                subCatPart.add(new Paragraph(" "));
                index = Integer.parseInt(tempArray[1]);
            }
            tempArray[0] = tempArray[0].substring(1, tempArray[0].length());
            tempArray[7] = tempArray[7].substring(0, tempArray[7].length()-1);
            
            createTable(subCatPart, tempArray, latex);
            addEmptyLine(paragraph, 1);            
        }

        document.add(subCatPart);


    }

    static void createTable(Section subCatPart, String[] values, String latex)
            throws BadElementException, DocumentException, IOException {
        PdfPTable table_out = new PdfPTable(2);
        PdfPTable table = new PdfPTable(2);
        table.setTotalWidth(new float[]{ 72, 50 });
        Image grafo = Image.getInstance(ImageDir + values[0] +".png");
        grafo.scaleAbsolute(50,50);
        
        Graph6 g = new Graph6(values[0],0);
        
        PdfPCell c1_out = new PdfPCell();
        c1_out.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_out.addCell(c1_out);
        c1_out.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table_out.addCell(c1_out);

        
        PdfPCell c1 = new PdfPCell(new Phrase("Index",smallBold));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setCalculatedHeight(10);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Value",smallBold));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(c1);

        table.setHeaderRows(1);
        table.addCell(new Phrase("G6 code of the graph",small));
        table.addCell(new Phrase(values[0],small));
        table.addCell(new Phrase("Order",small));
        table.addCell(new Phrase(values[1],small));
        table.addCell(new Phrase("Size",small));
        table.addCell(new Phrase(Integer.toString(g.m()) ,small));
        table.addCell(new Phrase("Objective function",small));
        table.addCell(new Phrase(values[7],small));
        table.addCell("");
        table.addCell("");
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        
        table_out.addCell(table);
        table_out.addCell(grafo);
        
        subCatPart.add(table_out);

    }


    static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}
