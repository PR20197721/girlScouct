
<%
if( session.getValue("VTK_ADMIN") ==null ){ out.println("Invalid user"); return;}
%>
 <script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.jeditable.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/ckeditor/ckeditor.js"></script>

<script src="http://www.appelsiini.net/projects/jeditable/jquery.jeditable.js" type="text/javascript"></script>
<script src="http://www.appelsiini.net/projects/jeditable/jquery.jeditable.autogrow.js" type="text/javascript" ></script>
<script src="http://www.appelsiini.net/projects/jeditable/jquery.jeditable.ajaxupload.js" type="text/javascript" ></script>
<script src="http://www.appelsiini.net/projects/jeditable/jquery.jeditable.masked.js" type="text/javascript" ></script>
<script src="http://www.appelsiini.net/projects/jeditable/jquery.jeditable.time.js" type="text/javascript" ></script>
<script src="http://www.appelsiini.net/projects/jeditable/jquery.jeditable.timepicker.js" type="text/javascript" ></script>
<script src="http://www.appelsiini.net/projects/jeditable/jquery.jeditable.datepicker.js" type="text/javascript" ></script>
<script src="http://www.appelsiini.net/projects/jeditable/jquery.jeditable.charcounter.js" type="text/javascript" ></script>
<script type="text/javascript" src="http://www.appelsiini.net/projects/jeditable/js/jquery.maskedinput.js"></script>
<script type="text/javascript" src="http://www.appelsiini.net/projects/jeditable/js/jquery.timepicker.js"></script>
<script type="text/javascript" src="http://www.appelsiini.net/projects/jeditable/js/jquery.autogrow.js"></script>
<script type="text/javascript" src="http://www.appelsiini.net/projects/jeditable/js/jquery.charcounter.js"></script>
<script type="text/javascript" src="http://www.appelsiini.net/projects/jeditable/js/date.js"></script>
<script type="text/javascript" src="http://www.appelsiini.net/projects/jeditable/js/jquery.datePicker.js"></script>
<script type="text/javascript" src="http://www.appelsiini.net/projects/jeditable/js/jquery.ajaxfileupload.js"></script>
<script type="text/javascript" charset="utf-8">
// <![CDATA[
$(document).ready(function() {
  $(".charcounter").editable("http://www.appelsiini.net/projects/jeditable/php/save.php", { 
      indicator : "<img src='img/indicator.gif'>",
      type      : "charcounter",
      submit    : 'OK',
      tooltip   : "Click to edit...",
      onblur    : "ignore",
      charcounter : {
         characters : 60
      }
  });
    $(".autogrow").editable("http://www.appelsiini.net/projects/jeditable/php/save.php", { 
        indicator : "<img src='img/indicator.gif'>",
        type      : "autogrow",
        submit    : 'OK',
        cancel    : 'cancel',
        tooltip   : "Click to edit...",
        onblur    : "ignore",
        autogrow : {
           lineHeight : 16,
           minHeight  : 32
        }
    });
    $(".masked").editable("http://www.appelsiini.net/projects/jeditable/php/save.php", { 
        indicator : "<img src='img/indicator.gif'>",
        type      : "masked",
        mask      : "99/99/9999",
        submit    : 'OK',
        tooltip   : "Click to edit..."
    });
    $(".datepicker").editable("http://www.appelsiini.net/projects/jeditable/php/save.php", { 
        indicator : "<img src='img/indicator.gif'>",
        type      : 'datepicker',
        tooltip   : "Click to edit..."
    });
    $(".timepicker").editable("http://www.appelsiini.net/projects/jeditable/php/save.php", { 
        indicator : "<img src='img/indicator.gif'>",
        type      : 'timepicker',
        submit    : 'OK',
        tooltip   : "Click to edit..."
    });
    $(".time").editable("http://www.appelsiini.net/projects/jeditable/php/save.php", { 
        indicator : "<img src='img/indicator.gif'>",
        type      : 'time',
        submit    : 'OK',
        tooltip   : "Click to edit..."
    });
    
    
    
   
    $(".ajaxupload").editable("/content/girlscouts-vtk/controllers/auth.asset.html?id=(meeting.getRefId().substring( meeting.getRefId().lastIndexOf("/")+1).toUpperCase())", { 
        indicator : "<img src='img/indicator.gif'>",
        type      : 'ajaxupload',
        submit    : 'Upload',
        cancel    : 'Cancel',
        tooltip   : "Click to upload...",
        
        id   : 'editMtLogo',
        name : 'newvalue',
        
        onsubmit    : function(value, settings) {},
        "submitdata": function (value, settings) {
           
           
       },
        callback : function(value, settings) {
        
        	
        
        }
    });
   
});
    
    
    
    
    
// ]]>
</script>  






<script type="text/javascript" charset="utf-8">
(function($) {
	$.generateId = function() {
		return arguments.callee.prefix + arguments.callee.count++;
	};
	$.generateId.prefix = 'jq$';
	$.generateId.count = 0;

	$.fn.generateId = function() {
		return this.each(function() {
			this.id = $.generateId();
		});
	};
})(jQuery);


(function($) {
$.editable.addInputType('ckeditor', {
    /* Use default textarea instead of writing code here again. */
    //element : $.editable.types.textarea.element,
    element : function(settings, original) {
        /* Hide textarea to avoid flicker. */
        var textarea = $('<textarea>').css("opacity", "0").generateId();
        if (settings.rows) {
            textarea.attr('rows', settings.rows);
        } else {
            textarea.height(settings.height);
        }
        if (settings.cols) {
            textarea.attr('cols', settings.cols);
        } else {
            textarea.width(settings.width);
        }
        $(this).append(textarea);
        return(textarea);
    },
    content : function(string, settings, original) { 
        /* jWYSIWYG plugin uses .text() instead of .val()        */
        /* For some reason it did not work work with generated   */
        /* textareas so I am forcing the value here with .text() */
        $('textarea', this).text(string);
    },
    plugin : function(settings, original) {
        var self = this;
        if (settings.ckeditor) {
            setTimeout(function() { CKEDITOR.replace($('textarea', self).attr('id'), settings.ckeditor); }, 0);
        } else {
            setTimeout(function() { CKEDITOR.replace($('textarea', self).attr('id')); }, 0);
        }
    },
    submit : function(settings, original) {
        $('textarea', this).val(CKEDITOR.instances[$('textarea', this).attr('id')].getData());
	CKEDITOR.instances[$('textarea', this).attr('id')].destroy();
    }
});
})(jQuery);


$(function() {
        
  $(".editable_select").editable("<?php print $url ?>save.php", { 
    indicator : '<img src="img/indicator.gif">',
    data   : "{'Lorem ipsum':'Lorem ipsum','Ipsum dolor':'Ipsum dolor','Dolor sit':'Dolor sit'}",
    type   : "select",
    submit : "OK",
    style  : "inherit",
    submitdata : function() {
      return {id : 2};
    }
  });
  $(".editable_select_json").editable("<?php print $url ?>save.php", { 
    indicator : '<img src="img/indicator.gif">',
    loadurl : "<?php print $url ?>json.php",
    type   : "select",
    submit : "OK",
    style  : "inherit"
  });
  $(".editable_textarea").editable("/content/girlscouts-vtk/controllers/vtk.controller.html", { 
      indicator : "Saving....",
      type   : 'ckeditor',
      submitdata: { _method: "put" ,mid: "meeting.getUid()"},
      select : true,
      submit : 'OK',
      cancel : 'cancel',
      cssclass : "editable",
      onblur: 'ignore',
      tooltip: "Click to edit...",
      
      name : 'newvalue',
      
      ckeditor : {
    	  toolbar:
    	  [
['Bold','Italic','Underline','Strike','-','Superscript','Format'],
['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
['Link','Unlink']
    	  ],
    	  height: 260,
    	  startupFocus: true
    	  } 
      
      
      
  });
  $(".editable_textile").editable("<?php print $url ?>save.php?renderer=textile", { 
      indicator : "<img src='img/indicator.gif'>",
      loadurl   : "<?php print $url ?>load.php",
      type      : "textarea",
      submit    : "OK",
      cancel    : "Cancel",
      tooltip   : "Click to edit..."
  });
  
  $(".click").editable("<?php print $url ?>echo.php", { 
      indicator : "<img src='img/indicator.gif'>",
      tooltip   : "Click to edit...",
      style  : "inherit"
  });
  $(".dblclick").editable("<?php print $url ?>echo.php", { 
      indicator : "<img src='img/indicator.gif'>",
      tooltip   : "Doubleclick to edit...",
      event     : "dblclick",
      style  : "inherit"
  });
  $(".mouseover").editable("<?php print $url ?>echo.php", { 
      indicator : "<img src='img/indicator.gif'>",
      tooltip   : "Move mouseover to edit...",
      event     : "mouseover",
      style  : "inherit"
  });
  
  /* Should not cause error. */
  $("#nosuch").editable("<?php print $url ?>echo.php", { 
      indicator : "<img src='img/indicator.gif'>",
      type   : 'textarea',
      submit : 'OK'
  });

});


/*

$.editable.addInputType('ajaxupload', {
    
    element : function(settings) {
        settings.onblur = 'ignore';
        var input = $('<input type="file" id="upload" name="upload" />');
        $(this).append(input);
        return(input);
    },
    content : function(string, settings, original) {
        
    },
    plugin : function(settings, original) {
        var form = this;
        form.attr("enctype", "multipart/form-data");
        $("button:submit", form).bind('click', function() {
            //$(".message").show();
            $.ajaxFileUpload({
                url: settings.target,
                secureuri:false,
                fileElementId: 'upload',
                dataType: 'html',
                success: function (data, status) {
                    $(original).html(data);
                    original.editing = false;
                },
                error: function (data, status, e) {
                    alert(e);
                }
            });
            return(false);
        });
    }
});




jQuery.extend({

    createUploadIframe: function(id, uri)
	{
			//create frame
            var frameId = 'jUploadFrame' + id;
            
            if(window.ActiveXObject) {
                var io = document.createElement('<iframe id="' + frameId + '" name="' + frameId + '" />');
                if(typeof uri== 'boolean'){
                    io.src = 'javascript:false';
                }
                else if(typeof uri== 'string'){
                    io.src = uri;
                }
            }
            else {
                var io = document.createElement('iframe');
                io.id = frameId;
                io.name = frameId;
            }
            io.style.position = 'absolute';
            io.style.top = '-1000px';
            io.style.left = '-1000px';

            document.body.appendChild(io);

            return io			
    },
    createUploadForm: function(id, fileElementId)
	{
		//create form	
		var formId = 'jUploadForm' + id;
		var fileId = 'jUploadFile' + id;
		var form = $('<form  action="" method="POST" name="' + formId + '" id="' + formId + '" enctype="multipart/form-data"></form>');	
		var oldElement = $('#' + fileElementId);
		var newElement = $(oldElement).clone();
		$(oldElement).attr('id', fileId);
		$(oldElement).before(newElement);
		$(oldElement).appendTo(form);
		//set attributes
		$(form).css('position', 'absolute');
		$(form).css('top', '-1200px');
		$(form).css('left', '-1200px');
		$(form).appendTo('body');		
		return form;
    },

    ajaxFileUpload: function(s) {
        // TODO introduce global settings, allowing the client to modify them for all requests, not only timeout		
        s = jQuery.extend({}, jQuery.ajaxSettings, s);
        var id = new Date().getTime()        
		var form = jQuery.createUploadForm(id, s.fileElementId);
		var io = jQuery.createUploadIframe(id, s.secureuri);
		var frameId = 'jUploadFrame' + id;
		var formId = 'jUploadForm' + id;		
        // Watch for a new set of requests
        if ( s.global && ! jQuery.active++ )
		{
			jQuery.event.trigger( "ajaxStart" );
		}            
        var requestDone = false;
        // Create the request object
        var xml = {}   
        if ( s.global )
            jQuery.event.trigger("ajaxSend", [xml, s]);
        // Wait for a response to come back
        var uploadCallback = function(isTimeout)
		{			
			var io = document.getElementById(frameId);
            try 
			{				
				if(io.contentWindow)
				{
					 xml.responseText = io.contentWindow.document.body?io.contentWindow.document.body.innerHTML:null;
                	 xml.responseXML = io.contentWindow.document.XMLDocument?io.contentWindow.document.XMLDocument:io.contentWindow.document;
					 
				}else if(io.contentDocument)
				{
					 xml.responseText = io.contentDocument.document.body?io.contentDocument.document.body.innerHTML:null;
                	xml.responseXML = io.contentDocument.document.XMLDocument?io.contentDocument.document.XMLDocument:io.contentDocument.document;
				}						
            }catch(e)
			{
				jQuery.handleError(s, xml, null, e);
			}
            if ( xml || isTimeout == "timeout") 
			{				
                requestDone = true;
                var status;
                try {
                    status = isTimeout != "timeout" ? "success" : "error";
                    // Make sure that the request was successful or notmodified
                    if ( status != "error" )
					{
                        // process the data (runs the xml through httpData regardless of callback)
                        var data = jQuery.uploadHttpData( xml, s.dataType );    
                        // If a local callback was specified, fire it and pass it the data
                        if ( s.success )
                            s.success( data, status );
    
                        // Fire the global callback
                        if( s.global )
                            jQuery.event.trigger( "ajaxSuccess", [xml, s] );
                    } else
                        jQuery.handleError(s, xml, status);
                } catch(e) 
				{
                    status = "error";
                    jQuery.handleError(s, xml, status, e);
                }

                // The request was completed
                if( s.global )
                    jQuery.event.trigger( "ajaxComplete", [xml, s] );

                // Handle the global AJAX counter
                if ( s.global && ! --jQuery.active )
                    jQuery.event.trigger( "ajaxStop" );

                // Process result
                if ( s.complete )
                    s.complete(xml, status);

                jQuery(io).unbind()

                setTimeout(function()
									{	try 
										{
											$(io).remove();
											$(form).remove();	
											
										} catch(e) 
										{
											jQuery.handleError(s, xml, null, e);
										}									

									}, 100)

                xml = null

            }
        }
        // Timeout checker
        if ( s.timeout > 0 ) 
		{
            setTimeout(function(){
                // Check to see if the request is still happening
                if( !requestDone ) uploadCallback( "timeout" );
            }, s.timeout);
        }
        try 
		{
           // var io = $('#' + frameId);
			var form = $('#' + formId);
			$(form).attr('action', s.url);
			$(form).attr('method', 'POST');
			$(form).attr('target', frameId);
            if(form.encoding)
			{
                form.encoding = 'multipart/form-data';				
            }
            else
			{				
                form.enctype = 'multipart/form-data';
            }			
            $(form).submit();

        } catch(e) 
		{			
            jQuery.handleError(s, xml, null, e);
        }
        if(window.attachEvent){
            document.getElementById(frameId).attachEvent('onload', uploadCallback);
        }
        else{
            document.getElementById(frameId).addEventListener('load', uploadCallback, false);
        } 		
        return {abort: function () {}};	

    },

    uploadHttpData: function( r, type ) {
        var data = !type;
        data = type == "xml" || data ? r.responseXML : r.responseText;
        // If the type is "script", eval it in global context
        if ( type == "script" )
            jQuery.globalEval( data );
        // Get the JavaScript object, if JSON is used.
        if ( type == "json" )
            eval( "data = " + data );
        // evaluate scripts within html
        if ( type == "html" )
            jQuery("<div>").html(data);
            //jQuery("<div>").html(data).evalScripts();
			//alert($('param', data).each(function(){alert($(this).attr('value'));}));
        return data;
    }
})

$(".ajaxupload").editable("http://www.appelsiini.net/projects/jeditable/php/upload.php", { 
    indicator : "<img src='img/indicator.gif'>",
    type      : 'ajaxupload',
    submit    : 'Upload',
    cancel    : 'Cancel',
    tooltip   : "Click to upload..."
});

*/

</script>

<style type="text/css">
#sidebar {
  width: 0px;
}

#content {
  width: 770px;
}

.editable input[type=submit] {
  color: #F00;
  font-weight: bold;
}
.editable input[type=button] {
  color: #0F0;
  font-weight: bold;
}

</style>


 
 
 
<div>
Admin Tool >> <a href="/content/girlscouts-vtk/en/vtk.admin.home.html">home</a> 
</div>


<div style="float:right;">


<div class="addthis_sharing_toolbox"></div>


<script type="text/javascript" src="//s7.addthis.com/js/300/addthis_widget.js#pubid=ra-53fb53152142ec44"></script>

</div>
<div></div>
