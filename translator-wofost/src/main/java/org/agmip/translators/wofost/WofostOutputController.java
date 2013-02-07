package org.agmip.translators.wofost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.agmip.util.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WofostOutputController {
	
		File[] listOfFiles;
		private List<File> fileList = new ArrayList<File>();

		private void zipFiles(String zipFile) {
			byte[] buf = new byte[1024];
			try {
			    // Create the ZIP file
			    String outFilename = zipFile;
			    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

			    // Compress the files
			    for (File f: fileList) {
			    	
			    	FileInputStream in = new FileInputStream(f.getAbsolutePath());

			        // Add ZIP entry to output stream
			        out.putNextEntry(new ZipEntry(f.getAbsolutePath()));

			        // Transfer bytes from the file to the ZIP file
			        int len;
			        while ((len = in.read(buf)) > 0) {
			            out.write(buf, 0, len);
			        }

			        // Complete the entry
			        out.closeEntry();
			        in.close();
			        f.delete();
			    }

			    // Complete the ZIP file
			    out.close();
			} catch (IOException e) {
			}
       
		}
		 
	
		private void createDir(String dirName)
		{
	    	File theTempPath = new File(dirName);
	    	theTempPath.mkdir();
			
		}
		
		private void addFiles(File f)
		{
			if (f.isDirectory())
			{
				for (File ff: f.listFiles())
					addFiles(ff);
			}
			else
				fileList.add(f);	
		}
		
		private void deleteFiles(File f)
		{
			if (f.isDirectory())
			{
				for (File ff: f.listFiles())
					deleteFiles(ff);
			}
			f.delete();	
		}
		
	    public void writeFiles(String filePath, Map input) {
	    	
	        Logger Log = LoggerFactory.getLogger(WofostOutputController.class);
	        //Log.error("ERROR !");
	        
	    	WofostOutput.expName = MapUtil.getValueOr(input, "exname", "default");
	    	String outputFileName = filePath + WofostOutput.expName + "_wofost.zip";
	    	
	    	File thePath = new File(filePath);
	    	if (!thePath.exists())
	    		thePath.mkdir();
	    	   
	    	String FileWeatherPath = filePath + "\\meteo\\cabowe\\";
	    	createDir(filePath + "\\meteo\\");
	    	createDir(FileWeatherPath);
	    
	    	String FileSoilPath = filePath + "\\soild\\";
	    	createDir(FileSoilPath);
	    		    	
	    	String FileRunPath = filePath + "\\runs\\";
	    	createDir(FileRunPath);

	    	new WofostOutputWeather().writeFile(FileWeatherPath, input);
	    	new WofostOutputSoil().writeFile(FileSoilPath, input);
	    	
	    	new WofostOutputExperiments().writeFile(FileRunPath, input);
	    		    	
//	    	new WofostOutputSite().writeFile(tempFilePath, input);
//	    	new WofostOutputTimer().writeFile(tempFilePath, input);
//	    	new WofostOutputRerun().writeFile(tempFilePath, input);
//	    	new WofostOutputRunopt().writeFile(tempFilePath, input);
	    	
	    	List<String> errList = WofostOutput.errorList();
	    	for (int i = 0; i < errList.size(); i++)
	    		System.out.println(errList.get(i));
	    	
	    	// zip all files
	    	File theTempPath = new File(filePath);
	    	File[] _files = theTempPath.listFiles(); 
	    	for (File f: _files)
	    		addFiles(f);
	    		    	
//	    	zipFiles(outputFileName);
	    	
	    	//remove files
//	    	for (File f : _files)
//	    		deleteFiles(f);
//	    		    	
//	    	new File(tempFileWeatherPath).delete();
//	    	new File(tempFileSoilPath).delete();
//	    	theTempPath.delete();
	    	
	    	
	    }
}
