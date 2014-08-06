package org.alex.app.core;

/*

import com.cognifide.slice.api.model.InitializableModel;
import com.cognifide.slice.mapper.annotation.JcrProperty;
import com.cognifide.slice.mapper.annotation.SliceResource;
 
@SliceResource
*/
public class TextModel {//implements InitializableModel{
 
   // @JcrProperty
    private String text;
 
    public String getText() {
        return text;
    }
    
    public void setText(String text){this.text=text;}
    
    
    public void afterCreated() {
        if (text == null) {
            System.err.println("There is no text property in the resource");
        }
    }
 
}
