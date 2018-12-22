package com.mk.eap.component.pdf.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.alibaba.dubbo.config.annotation.Service;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.mk.eap.common.domain.DTO;
import com.mk.eap.common.utils.JarResourceUtil;
import com.mk.eap.component.pdf.itf.IPdfService;
import com.mk.eap.component.template.itf.ITemplateService;

@Component
@Service
public class PdfServiceImpl implements IPdfService {

	public ITextRenderer getRender() throws DocumentException, IOException {
		String ftlPath = JarResourceUtil.JarUtil.getResourseFolder();
		ftlPath = ftlPath + "../fonts/";
		ITextRenderer render = new ITextRenderer();
		ITextFontResolver fontResolver = render.getFontResolver();
		fontResolver.addFont(ftlPath + "simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		return render;
	}

	@Autowired
	ITemplateService tmplateService;

	@Override
	public byte[] genPdfByHtml(String tmplName, String htmlTemplContent, Object data) {
		String html = tmplateService.bindData(tmplName, htmlTemplContent, data);
		if (html == null) {
			return null;
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ITextRenderer render = null;
		try {
			render = getRender();
			render.setDocumentFromString(html);
			render.layout();
			render.createPDF(outputStream);
			render.finishPDF();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] pdf = outputStream.toByteArray();
		return pdf;
	}

	@Override
	public byte[] genPdfByDto(String tmplName, byte[] pdfTempl, DTO data) {
		byte[] pdf = null;

		try {
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

			PdfReader template = new PdfReader(pdfTempl);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			PdfStamper stamp = new PdfStamper(template, out);
			AcroFields form = stamp.getAcroFields();

			Map<String, Item> fields = form.getFields();

			for (Iterator<String> it = fields.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String fieldValue = form.getField(key);
				Object value = null;
				if (fieldValue != null && (fieldValue.indexOf("$") != -1 || fieldValue.indexOf("#") != -1)) {
					String fieldTmplName = tmplName + "-" + fieldValue;
					value = tmplateService.bindData(fieldTmplName, fieldValue, data);
				} else {
					value = data.getFieldValueByPath(key);
				}
				if (value != null) {
					form.setFieldProperty(key, "textfont", bfChinese, null);
					form.setField(key, value.toString());
				}
			}

			stamp.setFormFlattening(true);
			stamp.close();
			template.close();

			pdf = out.toByteArray();

		}

		catch (Exception e) {

			e.printStackTrace();

		}

		return pdf;
	}

}
