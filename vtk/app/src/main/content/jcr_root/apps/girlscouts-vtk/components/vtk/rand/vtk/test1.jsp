<html>
<head>
 <script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.custombox.js"></script>
<link rel="stylesheet" href="/etc/designs/girlscouts-vtk/clientlibs/css/alex/jquery.custombox.css">
<style>

.modal-example-content {
    width: 600px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
    background-color: #FFF;
    border: 1px solid rgba(0, 0, 0, 0.2);
    border-radius: 6px;
    outline: 0 none;
}
.modal-example-header {
    border-bottom: 1px solid #E5E5E5;
    padding: 15px;
}
.modal-example-body p,
.modal-example-header h4 {
    margin: 0;
}
.modal-example-body {
    padding: 20px;
}


</style>

</head>
<body>
<form>



 <a href="#modal" id="example1X" onclick="x('http://localhost:4503/vtk/111/troop-3a/assets/1404931506693_0.6870922895305347', 'title xx')">test</a>

<script>
function x(xx, ttl){
	
	var y = document.getElementById('ifr');
	y.src= xx;
	
	 $.fn.custombox( document.getElementById('example1X') );
	
	 document.getElementById('xyz').innerHTML=ttl;
	 
}

</script>
              
             
             
             
             <div id="modal" style="display: none;" class="modal-example-content">
        <div class="modal-example-header" >
            <span id="xyz"></span><button type="button" class="close" onclick="$.fn.custombox('close');">&times;</button>
            
        </div>
        <div class="modal-example-body" >
            <p>
            
            	<iframe id="ifr" height="500" width="550" src="http://localhost:4503/vtk/111/troop-3a/assets/1404931506693_0.6870922895305347"></iframe>
            </p>
        </div>
    </div>
    
    
    
    
    
                </form>
                </body>
                
                
               
                </html>