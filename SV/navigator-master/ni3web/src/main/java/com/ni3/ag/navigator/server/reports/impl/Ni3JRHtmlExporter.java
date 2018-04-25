/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.reports.impl;

import java.awt.geom.Dimension2D;
import java.io.IOException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRImageMapRenderer;
import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRRenderable;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.JRStringUtil;

import com.ni3.ag.adminconsole.util.Base64;

public class Ni3JRHtmlExporter extends JRHtmlExporter{

	public Ni3JRHtmlExporter(){
		super();
	}

	protected void exportImage(JRPrintImage image, JRExporterGridCell gridCell) throws JRException, IOException{
		writeCellTDStart(gridCell);

		String horizontalAlignment = CSS_TEXT_ALIGN_LEFT;

		switch (image.getHorizontalAlignmentValue()){
			case RIGHT:{
				horizontalAlignment = CSS_TEXT_ALIGN_RIGHT;
				break;
			}
			case CENTER:{
				horizontalAlignment = CSS_TEXT_ALIGN_CENTER;
				break;
			}
			case LEFT:
			default:{
				horizontalAlignment = CSS_TEXT_ALIGN_LEFT;
			}
		}

		if (!horizontalAlignment.equals(CSS_TEXT_ALIGN_LEFT)){
			writer.write(" align=\"");
			writer.write(horizontalAlignment);
			writer.write("\"");
		}

		String verticalAlignment = HTML_VERTICAL_ALIGN_TOP;

		switch (image.getVerticalAlignmentValue()){
			case BOTTOM:{
				verticalAlignment = HTML_VERTICAL_ALIGN_BOTTOM;
				break;
			}
			case MIDDLE:{
				verticalAlignment = HTML_VERTICAL_ALIGN_MIDDLE;
				break;
			}
			case TOP:
			default:{
				verticalAlignment = HTML_VERTICAL_ALIGN_TOP;
			}
		}

		if (!verticalAlignment.equals(HTML_VERTICAL_ALIGN_TOP)){
			writer.write(" valign=\"");
			writer.write(verticalAlignment);
			writer.write("\"");
		}

		StringBuffer styleBuffer = new StringBuffer();
		appendBackcolorStyle(gridCell, styleBuffer);

		boolean addedToStyle = appendBorderStyle(gridCell.getBox(), styleBuffer);
		if (!addedToStyle){
			appendPen(styleBuffer, image.getLinePen(), null);
		}

		if (styleBuffer.length() > 0){
			writer.write(" style=\"");
			writer.write(styleBuffer.toString());
			writer.write("\"");
		}

		writer.write(">");

		if (image.getAnchorName() != null){
			writer.write("<a name=\"");
			writer.write(image.getAnchorName());
			writer.write("\"/>");
		}

		JRRenderable renderer = image.getRenderer();
		boolean imageMapRenderer = renderer != null && renderer instanceof JRImageMapRenderer
		        && ((JRImageMapRenderer) renderer).hasImageAreaHyperlinks();

		boolean hasHyperlinks = false;

		if (renderer != null || isUsingImagesToAlign){
			if (imageMapRenderer){
				hasHyperlinks = true;
				hyperlinkStarted = false;
			} else{
				hasHyperlinks = startHyperlink(image);
			}

			writer.write("<img");

			ScaleImageEnum scaleImage = image.getScaleImageValue();

			writer.write(" src=\"");
			String base64Img = getBase64Image(renderer.getImageData());
			if (base64Img != null){
				writer.write(base64Img);
			}
			writer.write("\"");

			int imageWidth = image.getWidth() - image.getLineBox().getLeftPadding().intValue()
			        - image.getLineBox().getRightPadding().intValue();
			if (imageWidth < 0){
				imageWidth = 0;
			}

			int imageHeight = image.getHeight() - image.getLineBox().getTopPadding().intValue()
			        - image.getLineBox().getBottomPadding().intValue();
			if (imageHeight < 0){
				imageHeight = 0;
			}

			switch (scaleImage){
				case FILL_FRAME:{
					writer.write(" style=\"width: ");
					writer.write(toSizeUnit(imageWidth));
					writer.write("; height: ");
					writer.write(toSizeUnit(imageHeight));
					writer.write("\"");

					break;
				}
				case CLIP: // FIXMEIMAGE image clip could be achieved by cutting the image and preserving the image type
				case RETAIN_SHAPE:
				default:{
					double normalWidth = imageWidth;
					double normalHeight = imageHeight;

					if (!image.isLazy()){
						// Image load might fail.
						JRRenderable tmpRenderer = JRImageRenderer.getOnErrorRendererForDimension(renderer,
						        image.getOnErrorTypeValue());
						Dimension2D dimension = tmpRenderer == null ? null : tmpRenderer.getDimension();
						// If renderer was replaced, ignore image dimension.
						if (tmpRenderer == renderer && dimension != null){
							normalWidth = dimension.getWidth();
							normalHeight = dimension.getHeight();
						}
					}

					if (imageHeight > 0){
						double ratio = normalWidth / normalHeight;

						if (ratio > (double) imageWidth / (double) imageHeight){
							writer.write(" style=\"width: ");
							writer.write(toSizeUnit(imageWidth));
							writer.write("\"");
						} else{
							writer.write(" style=\"height: ");
							writer.write(toSizeUnit(imageHeight));
							writer.write("\"");
						}
					}
				}
			}

			writer.write(" alt=\"\"");

			if (hasHyperlinks){
				writer.write(" border=\"0\"");
			}

			if (image.getHyperlinkTooltip() != null){
				writer.write(" title=\"");
				writer.write(JRStringUtil.xmlEncode(image.getHyperlinkTooltip()));
				writer.write("\"");
			}

			writer.write("/>");

			endHyperlink();

		}
		writer.write("</td>\n");
	}

	boolean appendPen(StringBuffer sb, JRPen pen, String side){
		boolean addedToStyle = false;

		float borderWidth = pen.getLineWidth().floatValue();
		if (0f < borderWidth && borderWidth < 1f){
			borderWidth = 1f;
		}

		String borderStyle = null;
		switch (pen.getLineStyleValue()){
			case DOUBLE:{
				borderStyle = "double";
				break;
			}
			case DOTTED:{
				borderStyle = "dotted";
				break;
			}
			case DASHED:{
				borderStyle = "dashed";
				break;
			}
			case SOLID:
			default:{
				borderStyle = "solid";
				break;
			}
		}

		if (borderWidth > 0f){
			sb.append("border");
			if (side != null){
				sb.append("-");
				sb.append(side);
			}
			sb.append("-style: ");
			sb.append(borderStyle);
			sb.append("; ");

			sb.append("border");
			if (side != null){
				sb.append("-");
				sb.append(side);
			}
			sb.append("-width: ");
			sb.append(toSizeUnit((int) borderWidth));
			sb.append("; ");

			sb.append("border");
			if (side != null){
				sb.append("-");
				sb.append(side);
			}
			sb.append("-color: #");
			sb.append(JRColorUtil.getColorHexa(pen.getLineColor()));
			sb.append("; ");

			addedToStyle = true;
		}

		return addedToStyle;
	}

	private String getBase64Image(byte[] bytes){
		String str = "data:image/png;base64,";
		str += Base64.encodeBytes(bytes);
		return str;
	}

}
