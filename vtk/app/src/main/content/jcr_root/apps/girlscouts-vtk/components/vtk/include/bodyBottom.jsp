    </div>

    <script type="text/javascript">
		$(window).load(function() {
			var vtk_err_desc = document.getElementById("vtkErrMsg_hidden");
			var vtk_err_placeHldr = document.getElementById('vtkErrMsg');
			if( vtk_err_desc!=null && vtk_err_placeHldr!=null && vtk_err_desc.innerHTML!=null ){
				vtk_err_placeHldr.innerHTML = vtk_err_desc.innerHTML;
	            vtk_err_desc.innerHTML = "";
			}
		});
    </script>

    <div style="display:none;" id="vtkErrMsg_hidden">
        <%@include file="vtkError.jsp" %>
    </div>