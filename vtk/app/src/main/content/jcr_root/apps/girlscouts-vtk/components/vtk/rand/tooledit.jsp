<html>
<head>
<script src="//cdn.ckeditor.com/4.4.4/full/ckeditor.js"></script>


<script>
function initializeCKEditor() { 

var customToolBar =
    [
        ['LocalSave','NewPage','-','Templates'],
        ['Cut','Copy','Paste','PasteText','PasteFromWord','-', 'Scayt'],
        ['Undo','Redo','-','SelectAll','RemoveFormat','-','About','Preview'],
        ['Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', ...];

    //Enable Spell as you Type 
	CKEDITOR.config.scayt_autoStartup = true;
    CKEDITOR.config.extraPlugins = 'localSave'; 
    CKEDITOR.config.removePlugins = 'elementspath';
    CKEDITOR.config.toolbar = customToolBar;
    }
    </script>

</head>

<body>
tset

</body>


</html>