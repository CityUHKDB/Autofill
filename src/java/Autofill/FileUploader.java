package Autofill;

import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploader {

    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
    
    private static FileUploader instance;
    
    private FileUploader() {
        
    }
    
    public static FileUploader getInstance() {
        if (instance == null) {
            instance = new FileUploader();
        }
        return instance;
    }
    
    public String upload(HttpServletRequest request, String directory, String fileName) throws FileUploadException, Exception {
        // checks if the request actually contains upload file
        if (ServletFileUpload.isMultipartContent(request)) {
            // Configure upload setting
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(MEMORY_THRESHOLD);
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(MAX_FILE_SIZE);
            upload.setSizeMax(MAX_REQUEST_SIZE);

            // Creates the directory if it does not exist
            File uploadDir = new File(directory);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            // Extract file data
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
                for (FileItem item : formItems) {
                    if (!item.isFormField()) {
                        if (fileName == null) {
                            fileName = new File(item.getName()).getName();
                        }
                        String filepath = directory + File.separator + fileName;
                        File storeFile = new File(filepath);
                        item.write(storeFile);
                    }
                }
                FormManager formManager = FormManager.getInstance();
                formManager.addForm(fileName);
                return fileName;
            }
        }
        return null;
    }
}
