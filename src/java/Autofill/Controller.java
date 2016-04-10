package Autofill;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileUploadException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Controller extends HttpServlet {
    
    // upload settings
    private static final String FORM_DIRECTORY = "form";
    private static final String IMPORT_DIRECTORY = "import";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String addr;
        HttpSession session = request.getSession(true);
        
        try {
            String page = request.getParameter("page");
            User user = (User)session.getAttribute("user");
            if (page == null) {
                addr = "home.jsp";
            } else {
                if (user == null) {
                    switch (page) {
                        case "register":
                            register(request);
                            addr = "home.jsp";
                            break;
                        case "login":
                            login(request);
                            addr = "home.jsp";
                            break;
                        default:
                            addr = "home.jsp";
                            break;
                    }                   
                } else if (user.getRole().equals("member")) {
                    switch (page) {
                       case "logout":
                           session.setAttribute("user", null);
                           addr = "home.jsp";
                           break;
                        case "record":
                            addr = "record.jsp";
                            break;
                        case "import":
                            importData(request);
                            addr = "record.jsp";
                            break;
                        case "save":
                            addr = "record.jsp";
                            break;
                        case "fill":
                            session.setAttribute("formList", FormManager.getInstance().getFormList());
                            addr = "fill.jsp";
                            break;
                        case "process":
                            toProcessPage(request);
                            addr = "process.jsp";
                            break;
                        case "result":
                            toResultPage(request);
                            addr = "result.jsp";
                            break; 
                        default:
                            addr = "home.jsp";
                            break;
                    }
                } else {
                    switch (page) {
                        case "logout":
                            session.setAttribute("user", null);
                            addr = "home.jsp";
                            break;
                        case "manage":
                            toManagePage(request);
                            addr = "manage.jsp";
                            break;
                        case "statistics":
                            session.setAttribute("statistics", Dictionary.getInstance().getHistory());
                            addr = "statistics.jsp";
                            break;
                        default:
                            addr = "home.jsp";
                            break;
                    }                               
                }
 
            }
        } catch (Exception e) {
            e.printStackTrace();
            addr = "error.jsp";
        }
        
        RequestDispatcher dispatcher = request.getRequestDispatcher(addr);
        dispatcher.forward(request, response); 
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    // User registration
    // Return true if success, otherwise return false
    private boolean register(HttpServletRequest request) throws SQLException {
        AccountManager authentication = AccountManager.getInstance();
        String username = (String)request.getParameter("username");
        String password = (String)request.getParameter("password");
        boolean successful = authentication.register(username, password);
        return successful;
    }
    
    // User login
    // Return true if success, otherwise return false
    private boolean login(HttpServletRequest request) throws SQLException {
        AccountManager authentication = AccountManager.getInstance();
        String username = (String)request.getParameter("username");
        String password = (String)request.getParameter("password");
        User user = authentication.authenticate(username, password);
        if (user != null) {
            request.getSession().setAttribute("user", user);
            return true;
        } else {
            return false;
        }

    }

    private void toProcessPage(HttpServletRequest request) throws IOException, FileUploadException, DocumentException, JSONException, Exception {
        String filepath = request.getParameter("formPath");
        if (filepath == null) {
            // Upload form
            FileUploader uploader = FileUploader.getInstance();
            String directory = getServletContext().getRealPath(FORM_DIRECTORY);
            filepath = uploader.upload(request, directory, null);
            // Add form to database
            FormManager formManager = FormManager.getInstance();
            formManager.addForm(filepath);
        } 
        User user = ((User)request.getSession().getAttribute("user"));
        FormFiller filler = new FormFiller(getServletContext().getRealPath(FORM_DIRECTORY + File.separator + filepath));

        ArrayList<AcroFormField> fields = filler.fillPdf(
            getServletContext().getRealPath(FORM_DIRECTORY + File.separator + user.getUsername() + ".pdf"), 
            user.getData()
        );
        request.getSession().setAttribute("fields", fields);

    }
    
    private void toResultPage(HttpServletRequest request) throws IOException, DocumentException, SQLException {
        Dictionary dictionary = Dictionary.getInstance();
        HttpSession session = request.getSession();
        ArrayList<AcroFormField> fields = (ArrayList<AcroFormField>)session.getAttribute("fields");
        User user = (User)session.getAttribute("user");
        String filepath = getServletContext().getRealPath(FORM_DIRECTORY + File.separator + user.getUsername() + ".pdf");

        PdfReader reader = new PdfReader(filepath); 
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(getServletContext().getRealPath(FORM_DIRECTORY + File.separator + "result.pdf")));
        AcroFields form = stamper.getAcroFields();

        for (AcroFormField field : fields) {
            if (field.getFieldLabel() != null && field.getFieldLabel().length() != 0) {
                String option = field.getFieldName() + "_option";
                if (request.getParameter(option) != null) {
                    if (!request.getParameter(option).equals("")) {
                        if (form.getField(field.getFieldName()).equals("")) {
                            // Add synonym to dictionary
                            String personalFieldName = request.getParameter(field.getFieldName() + "_personalFieldName");
                            dictionary.addSynonym(personalFieldName, field.getFieldName().toLowerCase());
                        } else if (!form.getField(field.getFieldName()).equals(request.getParameter(field.getFieldName()))) {
                            // Reduce synonym's probability in dictionary
                            dictionary.reduceProbability(field.getPersonalFieldName(), field.getFieldName().toLowerCase());
                        }

                        if (!form.getField(field.getFieldName()).equals(request.getParameter(field.getFieldName()))) {
                            // Refill that field if it is changed by user
                            form.setField(
                                field.getFieldName(), 
                                request.getParameter(field.getFieldName())
                            );
                        }
                    }

                    // Matching correct or user perform matching
                    if ((request.getParameter(field.getFieldName() + "_option").equals("") && !request.getParameter(field.getFieldName()).equals("")) ||
                        (!request.getParameter(field.getFieldName() + "_option").equals("") && !form.getField(field.getFieldName()).equals("") && !form.getField(field.getFieldName()).equals(request.getParameter(field.getFieldName())))) {
                        // Increase probability
                        String personalFieldName = request.getParameter(field.getFieldName() + "_personalFieldName");
                        dictionary.addProbability(personalFieldName, field.getFieldName().toLowerCase());
                    }
                }
            }
        }

        stamper.close();
        reader.close();

        session.setAttribute("filepath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getRequestURI() + "/" + FORM_DIRECTORY + File.separator + "result.pdf");
    }
    
    private void toManagePage(HttpServletRequest request) throws SQLException {
        AccountManager accountManager = AccountManager.getInstance();
        FormManager formManager = FormManager.getInstance();
        
        String action = request.getParameter("action");
        if (action != null) {
            if (action.equals("deleteAccount")) {
                accountManager.deleteAccount(request.getParameter("userName"));
            } else if (action.equals("deleteForm")) {
                formManager.deleteForm(request.getParameter("formName"));
            }
        }
        
        request.getSession().setAttribute("userList", accountManager.getUserList());
        request.getSession().setAttribute("formList", formManager.getFormList());
    }
    
    private void importData(HttpServletRequest request) {
        Connection con = null;
        PreparedStatement pstmt = null;
        
        HttpSession session = request.getSession();
        
        DBUtil dbUtil = DBUtil.getInstance();
        try {
        // if user have logined
            if (session.getAttribute("user") != null) {
                User user = (User)session.getAttribute("user");

                // Upload file to server
                FileUploader uploader = FileUploader.getInstance();
                String directory = getServletContext().getRealPath(IMPORT_DIRECTORY);
                String fileName = user.getUsername() + ".pdf";
                fileName = uploader.upload(request, directory, fileName);

                // Retrieve data and group information from PDF
                StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
                PdfReader reader = new PdfReader(directory + File.separator + fileName); 
                ArrayList<AcroFormField> fieldList = structureAnalyser.analyse(reader);
                ArrayList<String> groupList = new ArrayList<>();
                JSONArray data = new JSONArray();
                JSONArray group = new JSONArray();
                for (AcroFormField field : fieldList) {
                    if (field.getFieldValue() != null && !field.getFieldValue().equals("")) {
                        Map map = new HashMap();
                        map.put("name", field.getFieldLabel().toLowerCase());
                        map.put("group", field.getGroup());
                        map.put("value", field.getFieldValue());
                        map.put("dataType", "Text");
                        data.put(new JSONObject(map));
                        
                        if (!groupList.contains(field.getGroup())) {
                            groupList.add(field.getGroup());
                            map.clear();
                            map.put("name", field.getGroup());
                            group.put(new JSONObject(map));
                        }
                    }
                }
                
                // Store data and group to database
                con = dbUtil.getDBConnection();
                pstmt = con.prepareStatement(
                    "UPDATE User SET data = ?, fieldGroup = ? WHERE username = ?"
                );
                pstmt.setString(1, data.toString());
                pstmt.setString(2, group.toString()); 
                pstmt.setString(3, user.getUsername());
                pstmt.executeUpdate();
                
                // Store data to user object
                user.setGroup(group.toString());
                user.setData(data.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUtil.closeDBObjects(con, pstmt);
        }
    }
}
