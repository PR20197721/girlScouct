<html>

<body>
<h2>create categories</h2>
<form action ="http://localhost:4503/content/girlscouts-vtk/controllers/vtk.vtkFin.html" method="POST" > 

Action:<select name="actId">
<option value="CREATE_CATEGORY">Create categ</option>
</select>

path: <input type="text" name="sectionPath" value="" />
<br/> category name: <input type="text" name="categoryName" value="" />
<input type="submit" value="create categ"/>

</form>


<h2>create notes</h2>
<form action ="http://localhost:4503/content/girlscouts-vtk/controllers/vtk.vtkFin.html" method="POST" > 

Action:<select name="actId">
<option value="CREATE_NOTE">Create note</option>
</select>

path: <input type="text" name="parentPath" value="" />
<br/> category name: <input type="text" name="content" value="" />
<input type="submit" value="create note"/>

</form>

</body>
</html>