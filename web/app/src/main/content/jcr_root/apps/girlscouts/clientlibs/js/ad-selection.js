girlscouts.functions.toggleField = function(field) {

	// Get the advanced page selection field 
    var pages = field.nextSibling();
    if(pages){
    	// Get value of selected option in our select box
    	var customized = field.getValue();
        if (customized == "true"){
            pages.show();        
        } else {
            pages.hide();
        }
    }

};
girlscouts.functions.getTitle=function(dialog) { 

  var selection = dialog.getField('./pages'); 
  var pathfield = dialog.getField('./path');

  if(selection){
      if(pathfield && pathfield.getValue()){
          selection.setTitle("Path: "+pathfield.getValue());
      }else{
          var adsPath = girlscouts.functions.getAdsPath();
          if(adsPath!=null && adsPath.length!=0){
              selection.setTitle("Path: "+adsPath);
          }else{
              selection.setTitle("Path not configured.");
      	}
      }
      
  } 
  return true;
};
girlscouts.functions.AdOptions=function() {
        var ads = [];
    	var currentPath = CQ.shared.HTTP.getPath();
    	var adData = CQ.shared.HTTP.eval(CQ.shared.HTTP.noCaching(currentPath+'/jcr:content/content/right/advertisement.json'));
		var adsPath = adData ? CQ.Util.formatData(adData)['path'] : null;
    	if(!adsPath){
        	adsPath=girlscouts.functions.getAdsPath();
        }
        if(adsPath){
            // get the URL to retrieve the ad-pages
			var url = CQ.shared.HTTP.noCaching(adsPath+".2.json");//two-level deep
            var childPages = CQ.shared.HTTP.eval(url);
            if(childPages){
                // loop through the child pages and create the list of ad pages
                for(var name in childPages){
                    if(childPages[name]['jcr:content'] && childPages[name]['jcr:content']['sling:resourceType']=='girlscouts/components/ad-page' ){
                        var ad = {};
                        ad['text'] = childPages[name]['jcr:content']['jcr:title'];
                        ad['value'] = adsPath + '/' + name;
                        ads.push(ad);
                    }
                }
            }
        }
        return ads;
};
//get council's adspath configuration
girlscouts.functions.getAdsPath=function (){
	var PARENT_LEVEL = 3;	
    var currentPath = CQ.shared.HTTP.getPath();
        var slashIndex = 0;
        for (var i = 0; i < PARENT_LEVEL; i++) {
			slashIndex = currentPath.indexOf("/", slashIndex + 1);
        }
        //home page path
        var enPath = slashIndex == -1 ? currentPath : currentPath.substring(0, slashIndex);
		//retrieve adsPath from homepage
        var pageData = CQ.shared.HTTP.noCaching(enPath + "/jcr:content.json");
        var adsPath = pageData ? CQ.Util.formatData(CQ.shared.HTTP.eval(pageData))['adsPath'] : null;
    return adsPath;
};




