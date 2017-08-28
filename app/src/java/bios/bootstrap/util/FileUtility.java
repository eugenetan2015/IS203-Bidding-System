package bios.bootstrap.util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import is203.JWTException;
import is203.JWTUtility;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.FileUtils;

/**
 *
 * @author Wilson
 */
public class FileUtility {

    private final int BUFFER_SIZE = 4096;
    private byte[] bytesIn;
    private String filePathWithFolder;
    private String output_path;
    private String firstFolderPath;
    private int fileFolderCount;

    // upload settings - TEST
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; 	// 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
    private String token = null;

    /**
     * Specific Constructor
     */
    public FileUtility() {
        bytesIn = new byte[BUFFER_SIZE];
    }

    // This is the upload path after uploading a zip or rar file to the server

    /**
     * Upload file used for JSON Bootstrap
     * @param request the HTTPServletRequest
     * @param folderToSave the folder name to save the zip file
     * @return a JsonArray to store the error message
     * @throws IOException if there is an error
     */
    public JsonArray uploadFile(HttpServletRequest request, String folderToSave) throws IOException {
        // C:\Users\<USERNAME>\Desktop\is203_g6t3\app\build\web
        String rootPath = request.getServletContext().getRealPath("/");
        String hostname = request.getRequestURL().toString();
        JsonArray messageArray = new JsonArray();
        if (hostname.contains("localhost")) {
            output_path = rootPath + folderToSave + "\\";
        } else {
            output_path = rootPath;
        }

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            try {
                // step 1. create folder (this mainly use for localhost, deployment not working)
                createDirectory(output_path);

                // configures upload settings
                DiskFileItemFactory factory = new DiskFileItemFactory();
                // sets memory threshold - beyond which files are stored in disk 
                factory.setSizeThreshold(MEMORY_THRESHOLD);
                // sets temporary location to store files
                factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

                ServletFileUpload upload = new ServletFileUpload(factory);

                // sets maximum size of upload file
                upload.setFileSizeMax(MAX_FILE_SIZE);

                // sets maximum size of request (include file + form data)
                upload.setSizeMax(MAX_REQUEST_SIZE);

                // parses the request's content to extract file data
                List<FileItem> formItems = upload.parseRequest(request);

                boolean validUpload = false;
                boolean validToken = false;

                if (formItems != null && formItems.size() > 0) {
                    // iterates over form's fields
                    String fileName = "";
                    for (FileItem item : formItems) {
                        // processes only fields that are not form fields
                        if (!item.isFormField()) {
                            fileName = new File(item.getName()).getName();
                            String filePath = output_path + File.separator + fileName;
                            File storeFile = new File(filePath);

                            try {
                                item.write(storeFile);
                                validUpload = true;
                            } catch (Exception e) {

                            }
                        } else // saves the file on disk
                        {
                            token = item.getString();
                            if (token == null) {
                                JsonPrimitive element = new JsonPrimitive("missing token");
                                messageArray.add(element);
                            } else if (token.equals("")) {
                                JsonPrimitive element = new JsonPrimitive("blank token");
                                messageArray.add(element);
                            } else {
                                try {
                                    String tok = JWTUtility.verify(token, AdminUtility.getSecretKey());
                                    validToken = true;
                                } catch (JWTException e) {
                                    JsonPrimitive element = new JsonPrimitive("invalid token");
                                    messageArray.add(element);
                                }
                            }
                        }
                    }
                    processUpload(fileName, validUpload, validToken);
                }
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
        return messageArray;
    }

    // Process upload when the upload is valid and token is valid
    private void processUpload(String fileName, boolean validUpload, boolean validToken) throws IOException {
        String test = "";
        if (validUpload && validToken) {
            String zipFileUploadedPath = "";
            String zipFileName = fileName;
            if (zipFileName != null) {
                zipFileUploadedPath = output_path + zipFileName;
                boolean validFileFormat = checkFileFormat(zipFileName);
                // check if file format is .zip
                if (validFileFormat) {
                    // process unzip
                    unzipFile(zipFileUploadedPath);
                }
            }
        }
    }

    /**
     * Upload file used for Bootstrap UI
     * @param request the HTTPServletRequest
     * @param folderToSave the folder name to save the zip file
     * @return a String that check the fileFormat is valid
     * @throws IOException if there is an error
     */
    public String uploadFileForUI(HttpServletRequest request, String folderToSave) throws IOException {
        // C:\Users\<USERNAME>\Desktop\is203_g6t3\app\build\web
        String rootPath = request.getServletContext().getRealPath("/");
        String hostname = request.getRequestURL().toString();
        if (hostname.contains("localhost")) {
            output_path = rootPath + folderToSave + "\\";
        } else {
            output_path = rootPath;
        }

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            try {
                // step 1. create folder (this mainly use for localhost, deployment not working)
                createDirectory(output_path);

                // configures upload settings
                DiskFileItemFactory factory = new DiskFileItemFactory();
                // sets memory threshold - beyond which files are stored in disk 
                factory.setSizeThreshold(MEMORY_THRESHOLD);
                // sets temporary location to store files
                factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

                ServletFileUpload upload = new ServletFileUpload(factory);

                // sets maximum size of upload file
                upload.setFileSizeMax(MAX_FILE_SIZE);

                // sets maximum size of request (include file + form data)
                upload.setSizeMax(MAX_REQUEST_SIZE);

                // parses the request's content to extract file data
                List<FileItem> formItems = upload.parseRequest(request);

                if (formItems != null && formItems.size() > 0) {
                    // iterates over form's fields
                    for (FileItem item : formItems) {
                        // processes only fields that are not form fields
                        if (!item.isFormField()) {
                            String fileName = new File(item.getName()).getName();
                            String filePath = output_path + File.separator + fileName;
                            File storeFile = new File(filePath);
                            String zipFileUploadedPath = "";
                            item.write(storeFile);

                            String zipFileName = fileName;
                            if (zipFileName != null) {
                                zipFileUploadedPath = output_path + zipFileName;
                                boolean validFileFormat = checkFileFormat(zipFileName);
                                // check if file format is .zip
                                if (validFileFormat) {
                                    // process unzip
                                    unzipFile(zipFileUploadedPath);
                                    return "valid";
                                } else {
                                    return "invalid";
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
        return null;
    }

    // create directory

    /**
     * Create a new directory
     * @param uploadDir - the uploadDir path
     */
    public void createDirectory(String uploadDir) {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

    }

    // check the file format of the file
    private boolean checkFileFormat(String fileName) {
        if (fileName.contains("zip")) {
            return true;
        }
        return false;
    }

    // unzip the zip file
    private void unzipFile(String zipFileUploadedPath) throws IOException {
        ZipInputStream zipInput = new ZipInputStream(new FileInputStream(zipFileUploadedPath));
        ZipEntry zipE = zipInput.getNextEntry();

        // reset count to 0.
        fileFolderCount = 0;

        // only one folder contained in the zip file
        firstFolderPath = zipE.getName();

        File dir = null;

        while (zipE != null) {
            String extractFolderPath = output_path + zipE.getName();

            if (!zipE.isDirectory()) {
                // Extract file with the zip file uploaded path and the place to extract the folder
                extractFile(zipInput, extractFolderPath);
            } else {
                /// create the directory if the zip file contain a directory
                dir = new File(extractFolderPath);
                dir.mkdir();

                // the idea of having this here is that there might be
                // multiple folders in a zip file. This always
                // capture the latest level folders.
                // e.g. Data/Test folder
                filePathWithFolder = output_path + zipE.getName();
                fileFolderCount++;
            }

            zipInput.closeEntry();
            zipE = zipInput.getNextEntry();

        }
        zipInput.close();

        // do a clean up, delete the zip file
        cleanUpAfterUnzip(zipFileUploadedPath);
    }

    // remove the zip file after unzipping
    private void cleanUpAfterUnzip(String zipFileUploadedPath) {
        File f = new File(zipFileUploadedPath);
        f.delete();
    }

    // remove all the files after bootstrapping

    /**
     * Clean up all the files (e.g. all the csv files and folder) after bootstrapping
     * @param folder the folderName to remove the file
     * @param extension the extension.  E.g. .csv
     * @throws IOException if there is an error
     */
    public void cleanUpAfterBootstrap(String folder, String extension) throws IOException {
        // if the total file Folder is more than 1, get the root folder level path
        if (fileFolderCount > 1) {
            folder = output_path + firstFolderPath;
            FileUtils.deleteDirectory(new File(folder));
            return;
        }

        // perform this when file folder is less than or equal to 1
        File dir = new File(folder);
        String[] list = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String extension) {
                return extension.endsWith(".csv");
            }
        });

        File fileToDelete = null;

        if (list != null) {
            if (list.length == 0) {
                return;
            }

            for (String file : list) {
                String fileName = folder + file;
                fileToDelete = new File(fileName);
                fileToDelete.delete();
            }

            dir.delete();
        }
    }

    // extract the zip file
    private void extractFile(ZipInputStream zipIn, String extractFolderPath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(extractFolderPath))) {
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    /**
     * Get the file path
     * @return the filePathWithFolder if the fileFolderCount is more than 0 else return the default path with no folder inside the zip directory
     */
    public String getFilePath() {
        // this is the path (zip file with multiple folder inside, folder after folder)
        if (fileFolderCount > 0) {
            return filePathWithFolder;
        }

        // this is the path (zip file with no folder inside)
        return output_path;
    }
}
