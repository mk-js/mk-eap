package com.mk.eap.component.file.impl;

import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalClient
{
    public static final Logger logger = LoggerFactory.getLogger(LocalClient.class);
    private static volatile LocalClient fsclient;

    private LocalClient()
    {
    }

    public static LocalClient getInstance()
    {
        if(fsclient == null)
            synchronized(LocalClient.class)
            {
                if(fsclient == null)
                    fsclient = new LocalClient();
            }
        return fsclient;
    }

	public String upload(String savePath, String fileName, byte fileContent[]) {
		File file;
		FileOutputStream fos;
		file = new File(savePath);
		file.setReadable(true);
		fos = null;
		String result;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			logger.error("文件没有找到！",e1);
		}
		try {
			fos.write(fileContent);
			fos.flush();
		} catch (IOException e1) {
			logger.error("LocalClient/LocalClient 调用中发生异常：",e1);
		} finally {
			result = fileName;
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					result = null;
					logger.error("本地文件系统关闭io异常", e);
				}
		}
		return result;
	}

	public byte[] download(String downLoadPath) throws IOException {
		File file;
		FileInputStream in;
		file = new File(downLoadPath);
		in = null;
		byte bytes[] = null;
		try {
			in = new FileInputStream(downLoadPath);
			long length = file.length();
			if (length > 0x7fffffffL)
				throw new IOException(
						(new StringBuilder()).append("File is to large ").append(file.getName()).toString());
			bytes = new byte[(int) length];
			int offset = 0;
			for (int numRead = 0; offset < bytes.length
					&& (numRead = in.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead);
			
			if (offset < bytes.length)
				throw new IOException((new StringBuilder()).append("Could not completely read file ")
						.append(file.getName()).toString());
		} catch (FileNotFoundException e1) {
			logger.error("LocalClient/download 调用中发生异常：",e1);
		} finally {
			if (in != null)
				try {
					in.close();
				}
				catch (IOException e) {
					bytes = null;
					logger.error("本地文件系统关闭io异常!", e);
				}
		}
		return bytes;
	}

    public boolean deleteFile(String delPath)
    {
        boolean flag = false;
        File file = new File(delPath);
        if(file.isFile() && file.exists())
        {
            file.delete();
            flag = true;
        }
        return flag;
    }

}

