  <html>
<script src="http://knockoutjs.com/downloads/knockout-2.1.0.debug.js"></script>

<script>
var viewModel = {
	    // Data
	    entries: ko.observableArray(),
	    numberOfRecords: ko.observable(),
	    dataTimeOfLastCall: ko.observable(),
	    retrieveLogs: function() {
	        $.ajax({
	            type: 'POST',
	            url: 'http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test13.html',
	            data: {
	                json: ko.toJSON([
	                    { Id: 1, Message: 'message one', Machine: 'machine one', UserId: 'user 1', EntryDate: '2012/05/01' },
	                    { Id: 2, Message: 'message two', Machine: 'machine two', UserId: 'user 2', EntryDate: '2012/05/01' },
	                ]),
	                delay: 1
	            },
	            context: this,
	            success: function(data) {
	                this.entries($.map(data, function(item) {
	                    return new logEntry(item);
	                }));
	            },
	            dataType: 'json'
	        });
	    }
	};

	function logEntry(item) {
	    this.Id = ko.observable(item.Id);
	    this.Message = ko.observable(item.Message);
	    this.Machine = ko.observable(item.Machine);
	    this.UserName = ko.observable(item.UserId);
	    this.EntryDate = ko.
	    observable(item.EntryDate);

	    return this;
	}

	ko.applyBindings(viewModel);
	</script>
	
	
	<button data-bind="click: retrieveLogs">Retrieve Logs</button>

<table>
    <thead>
        <tr>
            <th>ID</th><th>Message</th><th>Machine</th><th>UserName</th><th>EntryDate</th>
         </tr>
    </thead>
    <tbody data-bind="foreach: entries">
        <tr>
            <td data-bind="text: Id"></td>
            <td data-bind="text: Message"></td>
            <td data-bind="text: Machine"></td>
            <td data-bind="text: UserName"></td>
            <td data-bind="text: EntryDate"></td>
        </tr>    
    </tbody>
</table>
  </html>