package com.mk.eap.component.file.impl;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.annotation.Service;
import com.mk.eap.component.file.itf.IFileService;



@Component
@Service
public class FileService implements IFileService {

	private final static Logger logger = LoggerFactory.getLogger(FileService.class);
	
	@Override
	public String uploadFile(String fileName, InputStream is) {
		
		return FileManager.uploadFile(fileName, is);
	}

	@Override
	public String uploadFile(String fileName, byte[] fileContent) {

		return FileManager.uploadFile(fileName, fileContent);
	}

	@Override
	public byte[] downLoadFile(String fileName) {
		return FileManager.downLoadFile(fileName);
	}

	@Override
	public boolean deleteFile(String fileName) {
		return FileManager.deleteFile(fileName);
	}

	@Override
	public Boolean deleteFileList(List<String> fileNameList) {
		return FileManager.deleteFileList(fileNameList);
	}

	@Override
	public List<String> getFileList(String tenantFolder) { 
		return FileManager.getFileList(tenantFolder);
	}

}
