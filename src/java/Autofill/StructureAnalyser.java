package Autofill;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class StructureAnalyser {
  
    private static StructureAnalyser instance;
    
    private StructureAnalyser() {
        
    }
    
    public static StructureAnalyser getInstance() {
        if (instance == null) {
            instance = new StructureAnalyser();
        }
        return instance;
    }
    
    public ArrayList<AcroFormField> analyse(PdfReader reader) throws IOException {
        ArrayList<AcroFormField> fieldList = new ArrayList<>();
        
        AcroFields form = reader.getAcroFields();
        ArrayList<String> fields = new ArrayList<>(form.getFields().keySet());
                
        for (String key : fields) {
            AcroFormField acroField = new AcroFormField();
            acroField.setFieldName(key);
            acroField.setPage(form.getFieldPositions(key).get(0).page);
            acroField.setPosition(form.getFieldPositions(key).get(0).position);
            fieldList.add(acroField);
        }
        
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
                fieldLabel = fieldLabel.replace("\n", " ");
                fieldLabel = fieldLabel.trim();
                currentField.setFieldLabel(fieldLabel); 
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
                
        return fieldList;
    }
            
    private boolean isSameLinePreviousField(Rectangle curFieldPosition, Rectangle preFieldPosition) {
        return isOnRight(curFieldPosition, preFieldPosition) && isOnSameLine(curFieldPosition, preFieldPosition);
    }
    
    private boolean isOnRight(Rectangle curFieldPosition, Rectangle preFieldPosition) {
        return curFieldPosition.getLeft() > preFieldPosition.getRight();
    }
    
    private boolean isOnSameLine(Rectangle curFieldPosition, Rectangle preFieldPosition) {
        double dy = curFieldPosition.getTop()-preFieldPosition.getTop();
        return  dy < curFieldPosition.getHeight();
    }
    
    private boolean isNewSection(Rectangle curFieldPosition, Rectangle preFieldPosition) {
        return !isOnRight(curFieldPosition, preFieldPosition) && isSeperated(curFieldPosition, preFieldPosition);         
    }
    
    private boolean isSeperated(Rectangle curFieldPosition, Rectangle preFieldPosition) {
        double dy = preFieldPosition.getBottom()-curFieldPosition.getTop();
        return dy > curFieldPosition.getHeight();
    }
}
