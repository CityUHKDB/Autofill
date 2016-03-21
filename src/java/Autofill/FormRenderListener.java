package Autofill;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.LineSegment;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import java.util.ArrayList;

public class FormRenderListener implements RenderListener {
    
    private ArrayList<FormText> formTexts;
    
    public FormRenderListener() {
        formTexts = new ArrayList<>();
    }
    
    public ArrayList<FormText> getFormText() {
        return this.formTexts;
    }
    
    public void reset() {
        formTexts.clear();
    }

    @Override
    public void beginTextBlock() {}

    @Override
    public void endTextBlock() {}
    
    @Override
    public void renderText(TextRenderInfo renderInfo) {
        LineSegment ascentLine = renderInfo.getAscentLine();
        LineSegment descentLine = renderInfo.getDescentLine();
        Rectangle position = new Rectangle(
            descentLine.getStartPoint().get(descentLine.getStartPoint().I1),
            descentLine.getStartPoint().get(descentLine.getStartPoint().I2),
            ascentLine.getEndPoint().get(ascentLine.getEndPoint().I1),
            ascentLine.getEndPoint().get(ascentLine.getEndPoint().I2)
        );
        FormText formText = new FormText(renderInfo.getText().trim());
        formText.setPosition(position);
        formTexts.add(formText);
    }
    
    @Override
    public void renderImage(ImageRenderInfo renderInfo) {}
    
}
