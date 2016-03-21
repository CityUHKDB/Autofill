package Autofill;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.TextField;
import com.itextpdf.text.pdf.parser.ContentByteUtils;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FormFiller {

    private ArrayList<AcroFormField> fieldList;
    private String sourceFilePath;
    private HashMap groupMap;
    
    public FormFiller(String filepath) {
        fieldList = new ArrayList<>();
        sourceFilePath = filepath;
        groupMap = new HashMap();
    }
    
//    public ArrayList<AcroFormField> fillPdf(String dest, String data, Connection con, PreparedStatement pstmt, ResultSet rs) throws IOException, DocumentException, JSONException, SQLException {
    public ArrayList<AcroFormField> fillPdf(String dest, String data) throws IOException, DocumentException, JSONException, SQLException {
        Dictionary dictionary = Dictionary.getInstance();
        PdfReader reader = new PdfReader(sourceFilePath); 
        //PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        PdfStamper stamper = null;
        AcroFields form = reader.getAcroFields();
        
        // Add AcroFields if no any AcroField in the form
        if (form.getFields().isEmpty()) {
            String tempPlace = dest.replace(".", "_converted.");
            stamper = new PdfStamper(reader, new FileOutputStream(tempPlace));
            convertPDF(reader, stamper);
            stamper.close();
            reader.close();
            reader = new PdfReader(tempPlace); 
        }
        stamper = new PdfStamper(reader, new FileOutputStream(dest));
        form = stamper.getAcroFields();

        ArrayList<String> fields = new ArrayList<>(form.getFields().keySet());
        Collections.sort(fields);
        
        //AcroFormField temp;
        
        HashMap objectMap = new HashMap();
        HashMap fieldMap = new HashMap();
        
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        fieldList = structureAnalyser.analyse(reader);

        for (AcroFormField field : fieldList) {
            String key = field.getFieldName();
            objectMap.put(key, field);
            ArrayList<String> tempKeys;
            
            // Add field name to map
            if (fieldMap.get(key.toLowerCase()) != null) {
                tempKeys = (ArrayList<String>)fieldMap.get(key.toLowerCase());
                tempKeys.add(key);
            } else {
                tempKeys = new ArrayList<>();
                tempKeys.add(key);
                fieldMap.put(key.toLowerCase(), tempKeys);
            }

            // Add same prefix words to map
            ArrayList<String> samePrefixWords = dictionary.getSamePrefixWords(key);
            for (String samePrefixWord: samePrefixWords) {
                if (fieldMap.get(samePrefixWord) != null) {
                    tempKeys = (ArrayList<String>)fieldMap.get(samePrefixWord);
                    tempKeys.add(key);
                } else {
                    tempKeys = new ArrayList<>();
                    tempKeys.add(key);
                    fieldMap.put(samePrefixWord, tempKeys);
                }
            }
            
            // Add field label to map
            if (form.getFieldType(field.getFieldName()) == AcroFields.FIELD_TYPE_TEXT) {
                String lowerLabel = field.getFieldLabel().toLowerCase();

                if (fieldMap.get(lowerLabel) != null) {
                    tempKeys = (ArrayList<String>)fieldMap.get(lowerLabel);
                    tempKeys.add(field.getFieldName());
                } else {
                    tempKeys = new ArrayList<>();
                    tempKeys.add(field.getFieldName());
                    fieldMap.put(lowerLabel, tempKeys);
                }
            }
        }                

        //for (String key : fields) { System.out.println(key);}
/*       
        for (String key : fields) {
            temp = new AcroFormField();
            temp.setFieldName(key);
            temp.setPage(form.getFieldPositions(key).get(0).page);
            temp.setPosition(form.getFieldPositions(key).get(0).position);
            fieldList.add(temp);
            objectMap.put(key, temp);
            //fieldMap.put(key.toLowerCase(), key);
            ArrayList<String> tempKeys;
            if (fieldMap.get(key.toLowerCase()) != null) {
                tempKeys = (ArrayList<String>)fieldMap.get(key.toLowerCase());
                tempKeys.add(key);
            } else {
                tempKeys = new ArrayList<>();
                tempKeys.add(key);
                fieldMap.put(key.toLowerCase(), tempKeys);
            }

            //System.out.println(key);
            //ArrayList<String> samePrefixWords = getSamePrefixWords(key, con);
            ArrayList<String> samePrefixWords = dictionary.getSamePrefixWords(key);
            for (String samePrefixWord: samePrefixWords) {
                if (fieldMap.get(samePrefixWord) != null) {
                    tempKeys = (ArrayList<String>)fieldMap.get(samePrefixWord);
                    tempKeys.add(key);
                } else {
                    tempKeys = new ArrayList<>();
                    tempKeys.add(key);
                    fieldMap.put(samePrefixWord, tempKeys);
                }
            }
        }
        System.out.println(fieldMap.get("email"));
        // Get field label and group
        RenderFilter filter;
        TextExtractionStrategy strategy;
        Rectangle targetArea;
        Collections.sort(fieldList);
        AcroFormField currentField = null;
        AcroFormField previousField = null;
        String group = null;
        String preGroup = null;
        int groupCount = 1;
        for (int i=0; i<fieldList.size(); i++) {
            currentField = fieldList.get(i);
            if (form.getFieldType(currentField.getFieldName()) == AcroFields.FIELD_TYPE_TEXT) {
                if (i>0 && isSameLinePreviousField(currentField.getPosition(), previousField.getPosition())) {
                    targetArea = new Rectangle(
                        previousField.getPosition().getRight(), // Lower left x
                        currentField.getPosition().getBottom(), // Lower left y
                        currentField.getPosition().getLeft(),   // Upper right x
                        currentField.getPosition().getTop()     // Upper right y
                    );
                } else {
                    targetArea = new Rectangle(
                        0,                                      // lower left x
                        currentField.getPosition().getBottom(), // Lower left y
                        currentField.getPosition().getLeft(),   // Upper right x
                        currentField.getPosition().getTop()     // Upper right y
                    );                    
                }
                filter = new RegionTextRenderFilter(targetArea);
                strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter);
                currentField.setFieldValue(form.getField(currentField.getFieldName()));
                String fieldLabel = PdfTextExtractor.getTextFromPage(reader, currentField.getPage(), strategy);
                fieldLabel = fieldLabel.replace(":", "");
                fieldLabel = fieldLabel.replace("_", "");
                fieldLabel = fieldLabel.trim();
                currentField.setFieldLabel(fieldLabel); 

                
                // Match field label to field key
                String lowerLabel = fieldLabel.toLowerCase();
                ArrayList<String> tempKeys;
                if (fieldMap.get(lowerLabel) != null) {
                    tempKeys = (ArrayList<String>)fieldMap.get(lowerLabel);
                    tempKeys.add(currentField.getFieldName());
                } else {
                    tempKeys = new ArrayList<>();
                    tempKeys.add(currentField.getFieldName());
                    fieldMap.put(lowerLabel, tempKeys);
                }
                System.out.println(fieldLabel);
                System.out.println(fieldMap.get(lowerLabel));
                
            }   
            
            if (i>0 && (isNewSection(currentField.getPosition(), previousField.getPosition()) || currentField.getPage() != previousField.getPage())) {
                targetArea = new Rectangle(
                    0,                                          // lower left x
                    currentField.getPosition().getTop(),        // Lower left y
                    currentField.getPosition().getRight(),      // Upper right x
                    currentField.getPosition().getTop()+currentField.getPosition().getHeight()      // Upper right y
                );
                filter = new RegionTextRenderFilter(targetArea);
                strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter);
                String tempGroup = PdfTextExtractor.getTextFromPage(reader, currentField.getPage(), strategy);
                tempGroup = tempGroup.replace("_", "");
                
                if (!tempGroup.equals("")) {
                    group = tempGroup.split("\\(")[0].trim();
                    if (group.split(" ").length > 4) {
                        group = "Other";
                        preGroup = group;
                    } else if (!group.equals(preGroup)) {
                        // New group name, reset counter
                        groupCount = 1;
                        preGroup = group;
                    } else {
                        // Group name same as previous group, adjust group name and increase counter
                        groupCount++;
                        group = group + " " + groupCount;
                    }
                }
            }
            currentField.setGroup(group);
            previousField = fieldList.get(i);
        }
*/
        
        JSONArray dataArray = new JSONArray(data);
        for (int i=0; i<dataArray.length(); i++) {
            boolean matchFound = false;
            JSONObject dataObject = dataArray.getJSONObject(i);
            System.out.println(dataArray.get(i));
            
            if (fieldMap.get(dataObject.getString("name")) != null) {
                // Exact match
                ArrayList<String> keyList = (ArrayList<String>)fieldMap.get(dataObject.getString("name"));
                for (String key : keyList) {
                    AcroFormField field = (AcroFormField)objectMap.get(key);
                    if (form.getField(key).equals("")) {
                        // Fill and process next if group match
                        if (isGroupMatch(field, dataObject.getString("group"))) {
                            form.setField(key, dataObject.getString("value"));
                            field.setPersonalFieldName(dataObject.getString("name"));
                            matchFound = true;
                            break;
                        }   
                    }
                }
            }
            
            if (fieldMap.get(dataObject.getString("name")) == null && !matchFound) {    // Search dictionary
                // Get standard field name for synonym
                //String standard = getStandardWord(dataObject.getString("name"), con);
                String standard = dictionary.getStandardWord(dataObject.getString("name"));
                if (fieldMap.get(standard) != null) {
                    // standard is the key
                    ArrayList<String> keyList = (ArrayList<String>)fieldMap.get(standard);
                    for (String key : keyList) {
                        AcroFormField field = (AcroFormField)objectMap.get(key);
                        if (form.getField(key).equals("")) {
                            // Fill and process next if group match
                            if (isGroupMatch(field, dataObject.getString("group"))) {
                                form.setField(key, dataObject.getString("value"));
                                field.setPersonalFieldName(dataObject.getString("name"));
                                break;
                            } 
                        }
                    }
                } else {
                    // standard is not the key
                    ArrayList<String> synonyms = dictionary.getSynonyms(dataObject.getString("name"));
                    for (String fieldName: synonyms) {
                        if (fieldMap.get(fieldName) != null) {
                            ArrayList<String> keyList = (ArrayList<String>)fieldMap.get(fieldName);
                            for (String key : keyList) {
                                AcroFormField field = (AcroFormField)objectMap.get(key);
                                // Fill and process next if group match
                                //System.out.println(field.getFieldLabel() + " " + field.getGroup());
                                if (form.getField(key).equals("")) {
                                    if (isGroupMatch(field, dataObject.getString("group"))) {
                                        form.setField(key, dataObject.getString("value"));
                                        field.setPersonalFieldName(dataObject.getString("name"));
                                        //System.out.println(fieldName);
                                        matchFound = true;
                                        break;
                                    }
                                }
                            }
                            // Process next if filled
                            if (matchFound) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (int i=0; i<fieldList.size(); i++) {
            AcroFormField currentField = fieldList.get(i);
            if (form.getFieldType(currentField.getFieldName()) == AcroFields.FIELD_TYPE_TEXT) {
                currentField.setFieldValue(form.getField(currentField.getFieldName()));
            }   
        }

        stamper.close();
        reader.close();

        return fieldList;
    }
    
    private boolean isGroupMatch(AcroFormField field, String group) throws SQLException {
        Dictionary dictionary = Dictionary.getInstance();
        String fieldGroup;
        if (field.getGroup() == null) {
            fieldGroup = "personal information";   
        } else {
            fieldGroup = field.getGroup().toLowerCase();
            fieldGroup = decodeGroupName(fieldGroup);
        }

        String dataGroup = group.toLowerCase();
        dataGroup = decodeGroupName(dataGroup);

        ArrayList<String> groupSynonyms = dictionary.getSynonyms(fieldGroup);

        // Default group is Perosnal Information
        if (dataGroup.equals("")) {
            dataGroup = "personal information";
        }
        
        if (groupMap.get(group) != null) {
            // Group matching already exist, decide according to the record
            String matchingGroup = (String)groupMap.get(group);
            if (matchingGroup.equals(field.getGroup())) {
                return true;
            }
        } else if (fieldGroup.equals(dataGroup)) {  // Group matching not existing, try to create new matching
            // Exact match of Group name 
            System.out.println("1st time");
            groupMap.put(group, field.getGroup());
            return true;
        } else {
            for (String groupSynonym : groupSynonyms) {
                if (fieldGroup.equals(groupSynonym)) {
                    // Match of synonym of the group name
                    groupMap.put(group, field.getGroup());
                    return true;
                }
            }
        }

        return false;
    }
    

    private boolean isOnSameLine(Rectangle curFieldPosition, Rectangle preFieldPosition) {
        double dy = curFieldPosition.getTop()-preFieldPosition.getTop();
        return  dy < curFieldPosition.getHeight();
    }
    
    private String decodeGroupName(String originGroupName) {
        String[] tempGroupName = originGroupName.split(" ");
        try {
            Integer.parseInt(tempGroupName[tempGroupName.length-1]);
            String decodedName = tempGroupName[0];
            for (int i=1; i<tempGroupName.length-1; i++) {
                decodedName = decodedName + " " + tempGroupName[i];
            }
            return decodedName;
        } catch (NumberFormatException e) {
            return originGroupName;
        }
    }
    
    // Test Add Acrobat Form Field
    private void convertPDF(PdfReader reader, PdfStamper stamper) throws IOException, DocumentException {
        ArrayList<FormText> fieldLabels = extractLabel(reader);
        Collections.sort(fieldLabels);
        HashMap nameMap = new HashMap();
        
        Rectangle pageSize = reader.getPageSize(1);
        float margin = pageSize.getWidth()/20;
        float right = pageSize.getRight(margin);
        
        for (int i=0; i<fieldLabels.size()-1; i++) {
            FormText curLabel = fieldLabels.get(i);
            FormText nextLabel = fieldLabels.get(i+1);
            Rectangle position;
            if (isOnSameLine(curLabel.getPosition(), nextLabel.getPosition())) {
                position = new Rectangle(
                    curLabel.getPosition().getRight() + margin/5,
                    curLabel.getPosition().getBottom(),
                    nextLabel.getPosition().getLeft() - margin/5,
                    curLabel.getPosition().getTop()
                );
            } else {
                position = new Rectangle(
                    curLabel.getPosition().getRight() + margin/5,
                    curLabel.getPosition().getBottom(),
                    right - margin/5,
                    curLabel.getPosition().getTop()
                );                
            }
            
            String fieldLabel;
            if (nameMap.get(curLabel.getName()) != null) {
                int num = (int)nameMap.get(curLabel.getName());
                num++;
                fieldLabel = curLabel.getName() + " " + num;
                nameMap.put(curLabel.getName(), num);
            } else {
                fieldLabel = curLabel.getName();
                nameMap.put(curLabel.getName(), 1);
            }
        
            TextField textfield = new TextField(
                stamper.getWriter(),
                position,
                fieldLabel
            );

            PdfFormField field = textfield.getTextField();
            field.setFieldName(fieldLabel);            
            field.setPage(curLabel.getPage());
            field.setPlaceInPage(curLabel.getPage());
            stamper.addAnnotation(field, curLabel.getPage());
        }
    }
    
    private ArrayList<FormText> extractLabel(PdfReader reader) throws IOException {
        ArrayList<FormText> fieldLabels = new ArrayList<>();
        
        FormRenderListener listener = new FormRenderListener();
        PdfContentStreamProcessor processor = new PdfContentStreamProcessor((RenderListener)listener);

        for (int i=1; i<=reader.getNumberOfPages(); i++) {
            PdfDictionary pageDictionary = reader.getPageN(i);
            PdfDictionary resourcesDictionary = pageDictionary.getAsDict(PdfName.RESOURCES);
            processor.processContent(ContentByteUtils.getContentBytesForPage(reader, i), resourcesDictionary);
            
            ArrayList<FormText> formTexts = listener.getFormText();
            
            HashMap count = new HashMap();
            for (FormText text : formTexts) {
                //System.out.println("TEXT: " + text.getName() + " " + text.getPosition().getLeft() + " " + text.getPosition().getRight() + " " + text.getPosition().getTop() + " " + text.getPosition().getBottom());
                float size = text.getPosition().getHeight();
                if (count.get(size) != null) {
                    count.put(size, (int)count.get(size) + 1);
                } else {
                    count.put(size, 1);
                }
            }

            ArrayList<Float> keys = new ArrayList<>(count.keySet());
            float generalSize = 0;
            int maxCount = 0;
            for (Float key : keys) {
                if (maxCount < (int)count.get(key)) {
                    generalSize = key;
                    maxCount = (int)count.get(key);
                }
            }
            
            // Group individual text to be readable word
            ArrayList<FormText> combinedTexts = new ArrayList<>();
            String word = "";
            Rectangle startRect = null;
            FormText preText = null;
            for (FormText text : formTexts) {
                String str = text.getName();
                str = str.replace("_", "");
                if (!str.equals("")) {
                    if (preText != null) {
                        boolean sameLine = text.getPosition().getTop() == preText.getPosition().getTop() && text.getPosition().getBottom() == preText.getPosition().getBottom();
                        boolean nearTo = text.getPosition().getLeft() - preText.getPosition().getRight() < text.getPosition().getWidth()/text.getName().length()*3;
                        if (sameLine && nearTo) {
                            word = word + text.getName().replace("_", "");
                        } else {
                            Rectangle position = new Rectangle(
                                startRect.getLeft(),
                                startRect.getBottom(),
                                preText.getPosition().getRight(),
                                startRect.getTop()
                            );
                            FormText combinedText = new FormText(word);
                            combinedText.setPosition(position);
                            combinedTexts.add(combinedText);

                            // Reset word and startRect
                            word = text.getName();
                            startRect = text.getPosition();
                        }
                    } else {
                        startRect = text.getPosition();
                        word = "";
                    }
                    preText = text;
                }
            }
            // Handle last cycle
            if (!word.equals("")) {
                Rectangle position = new Rectangle(
                    startRect.getLeft(),
                    startRect.getBottom(),
                    preText.getPosition().getRight(),
                    startRect.getTop()
                );
                FormText combinedText = new FormText(word);
                combinedText.setPosition(position);
                combinedTexts.add(combinedText);
            }
                     
            for (FormText text : combinedTexts) {
                if (text.getName().split(" ").length <= 3 && text.getPosition().getHeight() < generalSize*1.2) {
                    text.setPage(i);
                    fieldLabels.add(text);
                }
            }
            
            listener.reset();
            processor.reset();
        }
        
        return fieldLabels;
    }
}