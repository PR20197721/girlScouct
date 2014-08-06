package org.alex.app.core;

//import com.cognifide.slice.api.provider.ModelProvider;

public class Main {
	
	
	public void test(){
		
		
		System.err.println("test");
		//ModelProvider modelProvider = injector.getInstance(ModelProvider.class);
		//TextModel model = modelProvider.get(TextModel.class, resource);
		
	TextModel me= new TextModel();
	me.setText("hello test");
	}
}
