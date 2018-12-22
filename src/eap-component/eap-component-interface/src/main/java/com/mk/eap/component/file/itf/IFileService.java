package com.mk.eap.component.file.itf;

import java.io.InputStream;
import java.util.List;

public interface IFileService {
	public String uploadFile(String fileName, InputStream is);
	public String uploadFile(String fileName, byte fileContent[]);
	public byte[] downLoadFile( String fileName);
	public boolean deleteFile(String fileName);
	public Boolean deleteFileList(List<String> fileNameList);
	public List<String> getFileList(String tenantFolder);
}
