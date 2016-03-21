package Autofill;

import com.itextpdf.text.Rectangle;

public abstract class FormObject implements Comparable<FormObject> {
    
    private Rectangle position;
    private int page;

    public Rectangle getPosition() {
        return position;
    }

    public void setPosition(Rectangle position) {
        this.position = position;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
    
    @Override
    public int compareTo(FormObject another) {
        if (this.page > another.getPage()) {
            return 1;
        } else if (this.page < another.getPage()) {
            return -1;
        } else {    // In same page
            if (Math.round(this.position.getTop()) < Math.round(another.getPosition().getTop()) &&
                    Math.round(this.position.getBottom()) < Math.round(another.getPosition().getBottom()) &&
                    another.getPosition().getTop() - this.position.getTop() > this.position.getHeight()) {
                return 1;
            } else if (Math.round(this.position.getTop()) > Math.round(another.getPosition().getTop()) &&
                    Math.round(this.position.getBottom()) > Math.round(another.getPosition().getBottom()) &&
                    this.position.getTop() - another.getPosition().getTop() > this.position.getHeight()) {
                return -1;
            } else {    // In same row
                if (this.position.getLeft() > another.getPosition().getLeft()) {
                    return 1;
                } else if (this.position.getLeft() < another.getPosition().getLeft()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }
}
