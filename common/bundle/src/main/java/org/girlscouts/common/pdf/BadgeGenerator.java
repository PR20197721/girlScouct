package org.girlscouts.common.pdf;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.attach.ITagWorkerFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BadgeGenerator {

	private String badgeHtml;
	private OutputStream outputStream;

	public BadgeGenerator(String html, OutputStream output){
		this.badgeHtml = html;
		this.outputStream = output;
	}

	public Document generatePdf() throws IOException {

		WriterProperties writerProperties = new WriterProperties();

		PdfWriter pdfWriter = new PdfWriter(outputStream, writerProperties);

		PdfDocument pdfDoc = new PdfDocument(pdfWriter);

		//Set meta tags
		PdfDocumentInfo pdfMetaData = pdfDoc.getDocumentInfo();
		pdfMetaData.setAuthor("Girlscouts America");
		pdfMetaData.addCreationDate();
		pdfMetaData.setKeywords("Girlscouts badges");
		pdfMetaData.setSubject("Badge Report");
		pdfMetaData.setTitle("Badge Report");

		// pdf conversion
		ConverterProperties props = new ConverterProperties();

		// Setup custom tagworker factory for pulling images straight from the DAM.
		ITagWorkerFactory tagWorkerFactory = new GSTagWorkerFactory();
		props.setTagWorkerFactory(tagWorkerFactory);
		props.setImmediateFlush(false);
		Document doc = HtmlConverter.convertToDocument(new ByteArrayInputStream(badgeHtml.getBytes(StandardCharsets.UTF_8)) , pdfDoc, props);

		doc.close();


		return doc;
	}

}
