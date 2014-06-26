package com.jotabout.zipdownloader.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import android.util.Log;

public class ZipUtil {

	/**
	 * ѹ����
	 * 
	 * @param src
	 *            Դ�ļ�����Ŀ¼
	 * @param dest
	 *            ѹ���ļ�·��,ָ��ѹ��·���µ��ļ�����
	 * @throws IOException
	 */
	public static void zip(String src, String dest) throws IOException {
		ZipOutputStream out = null;
		try {
			File outFile = new File(dest);
			out = new ZipOutputStream(outFile);
			File fileOrDirectory = new File(src);
			// ������ļ�,��ֱ�Ӷ��ļ�����ѹ��
			if (fileOrDirectory.isFile()) {
				zipFileOrDirectory(out, fileOrDirectory, "");
			} else {
				// ѹ���ļ�������ļ�
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// �ݹ�ѹ��������curPaths
					zipFileOrDirectory(out, entries[i], "");
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	/**
	 * �ݹ�ѹ���ļ���Ŀ¼
	 * 
	 * @param out
	 *            ѹ�����������
	 * @param fileOrDirectory
	 *            Ҫѹ�����ļ���Ŀ¼����
	 * @param curPath
	 *            ��ǰѹ����Ŀ��·��������ָ����Ŀ���Ƶ�ǰ׺
	 * @throws IOException
	 */
	private static void zipFileOrDirectory(ZipOutputStream out,
			File fileOrDirectory, String curPath) throws IOException {
		FileInputStream in = null;
		try {
			if (!fileOrDirectory.isDirectory()) {
				// ѹ���ļ�
				byte[] buffer = new byte[4096];
				int bytes_read;
				in = new FileInputStream(fileOrDirectory);

				ZipEntry entry = new ZipEntry(curPath
						+ fileOrDirectory.getName());
				out.putNextEntry(entry);

				while ((bytes_read = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytes_read);
				}
				out.closeEntry();
			} else {
				// ѹ��Ŀ¼
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// �ݹ�ѹ��������curPaths
					zipFileOrDirectory(out, entries[i], curPath
							+ fileOrDirectory.getName() + "/");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	/**
	 * ��ѹ����
	 * 
	 * @param zipFileName
	 *            Դ�ļ�
	 * @param outputDirectory
	 *            ��ѹ�����ļ���ŵ�Ŀ¼
	 * @throws IOException
	 */
	public static void unzip(String zipFileName, String outputDirectory)
			throws IOException {

		ZipFile zipFile = null;

		try {
			long time = System.currentTimeMillis();
			Log.e("wang", "start" + (System.currentTimeMillis()));
			zipFile = new ZipFile(zipFileName);
			Log.e("wang", "end" + (time - System.currentTimeMillis()));
			Enumeration e = zipFile.getEntries();

			ZipEntry zipEntry = null;

			File dest = new File(outputDirectory);
			dest.mkdirs();

			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();

				String entryName = zipEntry.getName();

				InputStream in = null;
				FileOutputStream out = null;

				try {
					if (zipEntry.isDirectory()) {
						String name = zipEntry.getName();
						name = name.substring(0, name.length() - 1);

						File f = new File(outputDirectory + File.separator
								+ name);
						f.mkdirs();
					} else {
						int index = entryName.lastIndexOf("\\");
						if (index != -1) {
							File df = new File(outputDirectory + File.separator
									+ entryName.substring(0, index));
							df.mkdirs();
						}
						index = entryName.lastIndexOf("/");
						if (index != -1) {
							File df = new File(outputDirectory + File.separator
									+ entryName.substring(0, index));
							df.mkdirs();
						}

						File f = new File(outputDirectory + File.separator
								+ zipEntry.getName());
						// f.createNewFile();
						in = zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(f);

						int c;
						byte[] by = new byte[1024];

						while ((c = in.read(by)) != -1) {
							out.write(by, 0, c);
						}
						out.flush();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
					throw new IOException("��ѹʧ�ܣ�" + ex.toString());
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ex) {
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException ex) {
						}
					}
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			throw new IOException("��ѹʧ�ܣ�" + ex.toString());
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ex) {
				}
			}
		}

	}

	public static String replace(String path) {
		return path.replaceAll("\\\\"
				+ path.split("\\\\")[path.split("\\\\").length - 1], "");
	}
}
