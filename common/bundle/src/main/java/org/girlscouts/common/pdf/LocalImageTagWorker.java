package org.girlscouts.common.pdf;

import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;
import com.itextpdf.html2pdf.attach.ITagWorker;
import com.itextpdf.html2pdf.attach.ProcessorContext;
import com.itextpdf.html2pdf.html.AttributeConstants;
import com.itextpdf.html2pdf.html.node.IElementNode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.Image;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.common.servlets.BadgePDFGeneratorServlet;
import java.io.IOException;

public class LocalImageTagWorker implements ITagWorker {

	/** The image. */
	private HtmlImage image = null;

	public LocalImageTagWorker(IElementNode element, ProcessorContext context) {
		ResourceResolver resourceResolver = BadgePDFGeneratorServlet.resolverLocal.get();

		// TODO@MK : Find a better way to reference the rendition when we decide which ones to use.
		Rendition rendition = DamUtil.resolveToAsset(resourceResolver.resolve(element.getAttribute(AttributeConstants.SRC))).getRendition("cq5dam.thumbnail.319.319.png");

		byte[] imageData;
		try {
			imageData = IOUtils.toByteArray(rendition.getStream());
		} catch (IOException e) {
			imageData = new byte[0];
		}

		ImageData imgData = ImageDataFactory.create(imageData);
		PdfImageXObject imageXObject = new PdfImageXObject(imgData);
		if (imageXObject != null) {
			image = new HtmlImage(imageXObject);
		}

	}

	@Override
	public IPropertyContainer getElementResult() {
		return image;
	}

	@Override
	public void processEnd(IElementNode iElementNode, ProcessorContext processorContext) { /* Nothing to do - no body. */ }

	@Override
	public boolean processContent(String s, ProcessorContext processorContext) {
		return false;
	}

	@Override
	public boolean processTagChild(ITagWorker iTagWorker, ProcessorContext processorContext) {
		return false;
	}

	private class HtmlImage extends Image {

		// Conversion ratio from pixels to points.  // TODO@MK make this repsonsive to other size measurements.
		private double pxToPt = 0.75;

		public HtmlImage(PdfImageXObject xObject) {
			super(xObject);
		}

		@Override
		public float getImageWidth() {
			return (float) (xObject.getWidth() * pxToPt);
		}

		@Override
		public float getImageHeight() {
			return (float) (xObject.getHeight() * pxToPt);
		}

	}
}
