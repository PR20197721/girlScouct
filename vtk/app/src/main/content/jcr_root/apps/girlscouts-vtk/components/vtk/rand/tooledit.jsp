
<html>
<head>
	<title>Sample CKEditor Site</title>
	<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/ckeditor/ckeditor.js"></script>
</head>
<body>
	<form method="post">
		<p>
			My Editor:<br />
			<textarea id="editor1" name="editor1">&lt;p&gt;Initial value.&lt;/p&gt;</textarea>
			<script type="text/javascript">
				CKEDITOR.replace( 'editor1' );
			</script>
		</p>
		<p>
			<input type="submit" />
		</p>
	</form>
</body>
</html>