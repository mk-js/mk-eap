package com.mk.eap.component.pdf.itf;

import com.mk.eap.common.domain.DTO;

public interface IPdfService {

	byte[] genPdfByHtml(String tmplName, String templContent, Object data);

	byte[] genPdfByDto(String tmplName, byte[] pdfTempl, DTO data);
}
