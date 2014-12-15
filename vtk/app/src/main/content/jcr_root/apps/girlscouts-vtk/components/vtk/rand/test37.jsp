<!DOCTYPE html>
<html>
  <head>
    <title>To Do</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="http://fb.me/react/react-with-addons.min.js"></script>
    <script src="http://fb.me/react/JSXTransformer.js"></script>
    <link rel="stylesheet" href="https://netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap-theme.min.css">
    <script src="https://code.jquery.com/jquery.js"></script>
    <script src="https://netdna.bootstrapcdn.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
    <style type="text/css">
      .light {opacity: 0.15;}
      .light:hover {opacity: 0.25;}
      .glyphicon-unchecked {cursor: pointer;}
      .glyphicon-check {cursor: pointer;}
      .glyphicon-eye-open {cursor: pointer;}
      .glyphicon-info-sign {cursor: pointer;}
      .glyphicon-fire {cursor: pointer;}
      .glyphicon-calendar {cursor: pointer;}
      .glyphicon-pencil {cursor: pointer;}
      .glyphicon-minus {cursor: pointer;}
      .glyphicon-cloud {cursor: pointer;}
      .glyphicon-th-list {cursor: pointer;}
      .glyphicon-cloud-download {cursor: pointer;}
      .glyphicon-cloud-upload {cursor: pointer;}
      .glyphicon-gbp {cursor: pointer;}
      .cursor {cursor: pointer;}
      .bullet-input {width: 40%;}
      .blurred {opacity: 0.25;}
      .moving {opacity: 0.25;}
      .trash-activated {color: red;}
      .collection-input {width: 100%;}
      .hiddenFileInput {  
        display: block;
        visibility: hidden;
        width: 0;
        height: 0;
      }
      .handle {
        cursor: move;
        background-image: url("images/handle.png");
        display: inline-block;
        width: 14px;
        height: 12px;
      }
    </style>
  </head>
  <body>
    <div id="content"></div>
    <script type="text/jsx">
      /**
       * @jsx React.DOM
       */
      React.initializeTouchEvents(true); 
      // GUID function
      function s4() {
        return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
      };
     
      function guid() {
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
      }
      // End of GUID function

      function isDate (x) { 
        return (null != x) && !isNaN(x) && ("undefined" !== typeof x.getDate); 
      }
      // End of function to test if date    
      
      if (!localStorage.collections || !localStorage.activeCollection || !localStorage.bullets || !localStorage.filters) {
        var initCollections = [{id: 0, title: "Everything"}];
        localStorage.collections = JSON.stringify(initCollections);
        var initActiveCollection = 0;      
        localStorage.activeCollection = initActiveCollection;
        var initBullets = [];
        localStorage.bullets = JSON.stringify(initBullets);
        var initFilters = {important: true, explore: true, idea: true, task: true, note: true, event: true, deal: true, unmarked:true, hidedone: false, cluster: false};
        localStorage.filters = JSON.stringify(initFilters);
      } else {
        var initCollections = JSON.parse(localStorage.collections);
        var initActiveCollection = localStorage.activeCollection;
        var initBullets = JSON.parse(localStorage.bullets);
        var initFilters = JSON.parse(localStorage.filters);
      }
      
      
      var App = React.createClass({
        render: function() {
          return (
           <div className="container">
             <h1>To Do</h1>
             <CollectionsBar collections={initCollections} activeCollection={initActiveCollection} bullets={initBullets}/>
           </div>
          );
        }
      });

      var CollectionsBar = React.createClass({
        getInitialState: function() {
          return {collections: this.props.collections, activeCollection: this.props.activeCollection, editingNewCollection: false, bullets: this.props.bullets};
        },
        componentDidMount: function() {
          var that = this; // to propagate the scope in the below .click() callback
          $(document).click(function(event) { 
              if($(event.target).parents().index($('#addCollection')) == -1) {
                  that.setState({collections: that.state.collections, activeCollection: that.state.activeCollection, editingNewCollection: false, bullets: that.state.bullets});
              }        
          });     
        },
        switchCollection : function(e) {
          this.setState({collections: this.state.collections, activeCollection: e.target.id, editingNewCollection: false, bullets: this.state.bullets});
          localStorage.activeCollection = e.target.id;
        },
        onNewCollection : function(newCollection) {
          var collections = this.state.collections;
          collections.push(newCollection);
          localStorage.collections = JSON.stringify(collections);
          localStorage.activeCollection = newCollection.id; 
          this.setState({collections: collections, activeCollection:newCollection.id, editingNewCollection: false, bullets: this.state.bullets});
        },
        editNewCollection: function(e) {
          this.setState({collections: this.state.collections, activeCollection: this.state.activeCollection, editingNewCollection: true, bullets: this.state.bullets});
        },
        onDragStart: function(e) {
          $(e.target).addClass('moving');
          e.nativeEvent.dataTransfer.effectAllowed = "move";
          e.nativeEvent.dataTransfer.setData("text/plain", JSON.stringify({id: e.target.id, type:'collection'})); 
        },
        onDragEnd: function(e) {
          $(e.target).removeClass('moving');
        },
        onDragEnterTrash: function(e) {
          $(".glyphicon-trash").addClass('trash-activated'); // switch trash color 
        },
        onDragLeaveTrash: function(e) {
          $(".glyphicon-trash").removeClass('trash-activated'); // restore trash color to default
        },
        onDragOverTrash: function(e) {
          $(".glyphicon-trash").addClass('trash-activated'); // switch trash color 
          e.nativeEvent.dataTransfer.dropEffect = "move";
          e.preventDefault();
          return false;
        },
        dropOnTrash: function(e) {
          $(".glyphicon-trash").removeClass('trash-activated'); // restore trash color to blue
          try{
            var toDelete = JSON.parse(e.nativeEvent.dataTransfer.getData("text/plain"));
          } catch(e) {
            var toDelete = {id:"0", type:"noType"}; // workaround so that the first tab ("Everything") doesn't get deleted
          }
          if (toDelete.type == 'collection') {
	      this.deleteCollection(toDelete.id);
          } else if (toDelete.type == 'bullet') {
            this.deleteBullet(toDelete.id);  
          } 
        },
        deleteCollection: function(id) {
          var newCollections = this.state.collections.filter(function(collection){
            return collection.id != id;
          });
          localStorage.collections = JSON.stringify(newCollections);
          this.setState({collections: newCollections, activeCollection: 0, editingNewCollection: false, bullets: this.state.bullets});
        },
        deleteBullet: function(id) {
          var newBullets = this.state.bullets.filter(function(bullet){
            return bullet.id != id;
          });
          localStorage.bullets = JSON.stringify(newBullets);
          this.setState({collections: this.state.collections, activeCollection: this.state.activeCollection, editingNewCollection: false, bullets: newBullets});
        },
        render: function() {
          var formatedCollection = this.state.collections.map(function(collection) {
               var cx = React.addons.classSet;
               var collectionClasses = cx({
                 'active': (this.state.activeCollection == collection.id)
              });
              return (
                <li className={collectionClasses} draggable={collection.id !=0 ? "true" : "false"} onDragStart={collection.id !=0 ? this.onDragStart : ""} onDragEnd={collection.id !=0 ? this.onDragEnd : ""}>
                  <a href="#" id={collection.id} onClick={this.switchCollection}>{collection.title}</a>
                </li>
              );
          }, this); // see definition of array.map to understand why we have to pass the scope of -this- via the second arg (otherwise would be global)

          return (
           <div className="CollectionsBar">
             <ul className="nav nav-tabs">
               {formatedCollection}
               {!this.state.editingNewCollection ? <li><a href="#" onClick={this.editNewCollection} id="addCollection"><span className="glyphicon glyphicon-plus"></span></a></li> : <NewCollectionEditor onNewCollection={this.onNewCollection}/>}
               <li className="tip" data-placement="top" data-toggle="tooltip" data-original-title="Drag and drop here to delete" dropzone="move" onDragEnter={this.onDragEnterTrash} onDragLeave={this.onDragLeaveTrash} onDragOver={this.onDragOverTrash} onDrop={this.dropOnTrash}><a href="#"><span className="glyphicon glyphicon-trash light"></span></a></li>
             </ul>
             <FilterBar filters={initFilters} collections={this.state.collections} activeCollection={this.state.activeCollection} bullets={this.state.bullets}/>
           </div>
          );
        }
      });
      
      var NewCollectionEditor = React.createClass({
        getInitialState: function() {
          return {input: '', newCollection: {id: guid(), title:''}};
        },
        componentDidMount: function() {
          this.refs.collectionInput.getDOMNode().focus();
        },
        onInputChange: function(e) {
          this.setState({input: e.target.value, newCollection: {id: this.state.newCollection.id, title:e.target.value}});
        },
        saveCollection: function(e) {
          if (this.state.newCollection.title != '') {
            this.props.onNewCollection(this.state.newCollection);
            this.setState({input: '', newCollection: {id: guid(), title:''}});            
          }
        },
        render: function() {
            return(
              <li>
                <form className="form-inline" onSubmit={this.saveCollection}>
                  <input type="text" ref="collectionInput" className="collection-input form-control" onChange={this.onInputChange} value={this.state.input} />
                </form>
              </li>
            );
        }
      });

      var FilterBar = React.createClass({
        getInitialState: function() {
          return {filters: this.props.filters};
        },
        toggleFilter:  function(e){
          var filters = this.state.filters;
          filters[e.target.id] = !filters[e.target.id];
          localStorage.filters = JSON.stringify(filters);
          this.setState({filters: filters});
        },
        downloadBackup: function(e){
          var backup = JSON.stringify({collections: JSON.parse(localStorage.collections), activeCollection: localStorage.activeCollection, bullets: JSON.parse(localStorage.bullets), filters: JSON.parse(localStorage.filters)});
          var backupAsBlob = new Blob([backup], {type: "application/json"});
          var downloadLink = document.createElement("a");
	    downloadLink.download = "todo_backup_" + (new Date()).toLocaleString() + ".json";
	    downloadLink.innerHTML = "Download File";
	    if (window.webkitURL != null){
		// Chrome allows the link to be clicked
		// without actually adding it to the DOM.
		downloadLink.href = window.webkitURL.createObjectURL(backupAsBlob);
          } else {
            // Firefox requires the link to be added to the DOM
            // before it can be clicked.
            downloadLink.href = window.URL.createObjectURL(backupAsBlob);
            downloadLink.id = "tempDownload";
            downloadLink.style.display = "none";
            document.body.appendChild(downloadLink);
          }
          downloadLink.click();
          $('#tempDownload').remove();
        },
        uploadBackup: function(e) {
          $('#files').change(function(e){
            var fileToLoad = ($("#files"))[0].files[0];
            if (fileToLoad != undefined) {
              // First we make a backup of the current view in case the upload fails...
              var currentCollections = JSON.parse(localStorage.collections);
              var currentActiveCollection = localStorage.activeCollection;
              var currentBullets = JSON.parse(localStorage.bullets);
              var currentFilters = JSON.parse(localStorage.filters);
              try {
                var fileReader = new FileReader();
	          fileReader.onload = function(fileLoadedEvent) {
                var backupToJSON = JSON.parse(fileLoadedEvent.target.result);
                localStorage.collections = JSON.stringify(backupToJSON.collections);
                localStorage.activeCollection = backupToJSON.activeCollection; 
                localStorage.bullets = JSON.stringify(backupToJSON.bullets); 
                localStorage.filters = JSON.stringify(backupToJSON.filters); 
                window.location.reload();
	          }
	          fileReader.readAsText(fileToLoad, "UTF-8");
              }
              catch (e) {
                alert("Invalid file - uploading aborted");
                // We restore to the initial state
                localStorage.collections = JSON.stringify(currentCollections);
                localStorage.activeCollection = JSON.stringify(currentActiveCollection);
                localStorage.bullets = JSON.stringify(currentBullets);
                localStorage.filters = JSON.stringify(currentFilters);
                window.location.reload();
              }
            }          
          });
          $('#files').click();
        },
        xTooltip: function(title, x) {
          if (x == "deal") {
            var allDeals = this.props.bullets.filter(function(bullet){
              return (bullet.type[x] && !bullet.status.finished);
            });
            var dealValues = allDeals.map(function(deal){
              return parseFloat(deal.title.substring(deal.title.lastIndexOf("�")+1,deal.title.lastIndexOf("m")));
            });
            var pnl = 0;
		for (i=0, n=dealValues.length; i<n; i++) {
              if (!isNaN(dealValues[i])) {
                pnl += dealValues[i];
              }
            } 
            if (pnl > 0) {
              return title + "<br />(total deals: �" + pnl + "m)";
            } else {
              return title;
            }
          } else {
            var pendingX = this.props.bullets.filter(function(bullet){
              return (bullet.type[x] && !bullet.status.finished);
            });
            var doneX = this.props.bullets.filter(function(bullet){
              return (bullet.type[x] && bullet.status.finished);
            });
            return title + "<br />(pending: "+ pendingX.length + ", done: " + doneX.length + ")";
          }
        },
        render: function() {
          var cx = React.addons.classSet;
          var taskClasses = cx({
            'glyphicon': true,
            'glyphicon-check': true,
            'light': !this.state.filters.task, 
            'tip': true 
          });
          var noteClasses = cx({
            'glyphicon': true,
            'glyphicon-pencil': true,
            'light': !this.state.filters.note,
            'cursor': true, 
            'tip': true 
          });
          var eventClasses = cx({
            'glyphicon': true,
            'glyphicon-calendar': true,
            'light': !this.state.filters.event ,
            'cursor': true, 
            'tip': true
          });
          var dealClasses = cx({
            'glyphicon': true,
            'glyphicon-gbp': true,
            'light': !this.state.filters.deal ,
            'cursor': true, 
            'tip': true
          });
          var importantClasses = cx({
            'glyphicon': true,
            'glyphicon-fire': true,
            'light': !this.state.filters.important, 
            'tip': true 
          });
          var exploreClasses = cx({
            'glyphicon': true,
            'glyphicon-eye-open': true,
            'light': !this.state.filters.explore, 
            'tip': true
          });
          var ideaClasses = cx({
            'glyphicon': true,
            'glyphicon-info-sign': true,
            'light': !this.state.filters.idea, 
            'tip': true 
          });
          var unmarkedClasses = cx({
            'glyphicon': true,
            'glyphicon-minus': true,
            'light': !this.state.filters.unmarked, 
            'tip': true 
          });
          var hidedoneClasses = cx({
            'glyphicon': true,
            'glyphicon-cloud': true,
            'light': !this.state.filters.hidedone, 
            'tip': true 
          });
          var clusterClasses = cx({
            'glyphicon': true,
            'glyphicon-th-list': true,
            'light': !this.state.filters.cluster, 
            'tip': true 
          });
          var downloadClasses = cx({
            'glyphicon': true,
            'glyphicon-cloud-download': true,
            'tip': true 
          });
          var uploadClasses = cx({
            'glyphicon': true,
            'glyphicon-cloud-upload': true,
            'tip': true 
          });
          var backupTooltip = "Save backup to drive (" + (JSON.stringify(localStorage).length*2/5000000*100).toFixed(1) + "% used)";
          return (
            <div className="FiltersBar">
              <p>
                <br />
                Filters:
                {' '}
                <span className={taskClasses} onClick={this.toggleFilter} id="task" data-placement="top" data-toggle="tooltip" data-html="true" data-original-title={this.xTooltip("Display tasks", "task")}></span>
                {' '}
                <span className={noteClasses} onClick={this.toggleFilter} id="note" data-placement="top" data-toggle="tooltip" data-html="true" data-original-title={this.xTooltip("Display notes", "note")}></span>
                {' '}
                <span className={eventClasses} onClick={this.toggleFilter} id="event" data-placement="top" data-toggle="tooltip" data-html="true" data-original-title={this.xTooltip("Display events", "event")}></span>
                {' '}
                <span className={dealClasses} onClick={this.toggleFilter} id="deal" data-placement="top" data-toggle="tooltip" data-html="true" data-original-title={this.xTooltip("Display deals", "deal")}></span>
                {'  | '}
                <span className={importantClasses} onClick={this.toggleFilter} id="important" data-placement="top" data-toggle="tooltip" data-original-title="Display important"></span>
                {' '}
                <span className={exploreClasses} onClick={this.toggleFilter} id="explore" data-placement="top" data-toggle="tooltip" data-original-title="Display to be explored"></span>
                {' '}
                <span className={ideaClasses} onClick={this.toggleFilter} id="idea" data-placement="top" data-toggle="tooltip" data-original-title="Display ideas"></span>
                {' '}
                <span className={unmarkedClasses} onClick={this.toggleFilter} id="unmarked" data-placement="top" data-toggle="tooltip" data-original-title="Display unmarked"></span>
                {'  | '}
                <span className={hidedoneClasses} onClick={this.toggleFilter} id="hidedone" data-placement="top" data-toggle="tooltip" data-original-title="Hide finished bullets"></span>
                {' '}
                <span className={clusterClasses} onClick={this.toggleFilter} id="cluster" data-placement="top" data-toggle="tooltip" data-original-title="Group and sort bullets"></span>
                {'  �� Backup: '}
                <span className={downloadClasses} onClick={this.downloadBackup} id="download" data-placement="top" data-toggle="tooltip" data-original-title={backupTooltip}></span>
                {' '}
                <span className={uploadClasses} onClick={this.uploadBackup} id="upload" data-placement="top" data-toggle="tooltip" data-original-title="Load backup from drive"></span>
             </p>
             <input type="file" id="files" name="files[]" className="hiddenFileInput" />
             <BulletsList bullets={this.props.bullets} collections={this.props.collections} activeCollection={this.props.activeCollection} filters={this.state.filters}/>
            </div>	
          );
        }
      });

      var BulletsList = React.createClass({
        getInitialState: function() {
          return {bullets: this.props.bullets};
        },
        componentWillReceiveProps: function(nextProps) {
          this.setState({bullets: nextProps.bullets}); // update bullets when changed from within parent component
        },
        componentDidMount: function() { // in order to prevent line breaks when pressing enter, and blur to trigger save event
          $(".editableBullet").on('keydown', function(e) {
            if (e.keyCode == 13 && !e.shiftKey){
              e.preventDefault();
              this.blur();
            }
          });
        },
        componentDidUpdate: function() { // in order to prevent line breaks when pressing enter, and blur to trigger save event
          $(".editableBullet").on('keydown', function(e) {
            if (e.keyCode == 13 && !e.shiftKey){
              e.preventDefault();
              this.blur();
            }
          });
        },
        toggleFinished : function(e) {
          var bullets = this.state.bullets;
          for (var i=0; i<bullets.length; i++) {
            if(bullets[i].id == e.target.id) {
              var bullet = bullets[i];
              bullet.status.finished = !bullet.status.finished;
              bullets[i] = bullet;
            }
          }
          localStorage.bullets = JSON.stringify(bullets);
          this.setState({bullets: bullets});
        },
        toggleIdea : function(e) {
          var bullets = this.state.bullets;
          for (var i=0; i<bullets.length; i++) {
            if(bullets[i].id == e.target.id) {
              var bullet = bullets[i];
              bullet.signifier.idea = !bullet.signifier.idea;
              bullets[i] = bullet;
            }
          }
          localStorage.bullets = JSON.stringify(bullets);
          this.setState({bullets: bullets});
        },
        toggleExplore : function(e) {
          var bullets = this.state.bullets;
          for (var i=0; i<bullets.length; i++) {
            if(bullets[i].id == e.target.id) {
              var bullet = bullets[i];
              bullet.signifier.explore = !bullet.signifier.explore;
              bullets[i] = bullet;
            }
          }
          localStorage.bullets = JSON.stringify(bullets);
          this.setState({bullets: bullets});
        },
        toggleImportant : function(e) {
          var bullets = this.state.bullets;
          for (var i=0; i<bullets.length; i++) {
            if(bullets[i].id == e.target.id) {
              var bullet = bullets[i];
              bullet.signifier.important = !bullet.signifier.important;
              bullets[i] = bullet;
            }
          }
          localStorage.bullets = JSON.stringify(bullets);
          this.setState({bullets: bullets});
        },
        saveNewBullet: function(newBullet){
          var newBullets = this.state.bullets;
          newBullets.unshift(newBullet);
          this.setState({bullets: newBullets});
          localStorage.bullets = JSON.stringify(newBullets);
        },
        onDragStart: function(e) {
          $(e.target).addClass('moving');
          e.nativeEvent.dataTransfer.effectAllowed = "move";
          e.nativeEvent.dataTransfer.setData("text/plain", JSON.stringify({id: e.target.id, type:'bullet'})); 
        },
        onDragEnd: function(e) {
          $(e.target).removeClass('moving');
        },
        changeBulletTitle: function(e) {
          var bullets = this.state.bullets;
          for (var i=0; i<bullets.length; i++) {
            if(bullets[i].id == e.target.id) {
              var bullet = bullets[i];
              bullet.title = e.target.innerText;
              bullets[i] = bullet;
            }
          }
          localStorage.bullets = JSON.stringify(bullets);
          this.setState({bullets: bullets});
        },
        render: function() {
          var cx = React.addons.classSet;

          // Here we cluster the bullets by meetings (sorted by date) then everything else
          if (this.props.filters.cluster) {
            var events = this.state.bullets.filter(function(bullet) {
              return (bullet.type.event);
            });
            events.sort(function(a,b){
              aDate = new Date(a.title.substring(0,10).split("/").reverse().join("-"));
              bDate = new Date(b.title.substring(0,10).split("/").reverse().join("-"));
              return (aDate - bDate);
            });
            var deals = this.state.bullets.filter(function(bullet) {
              return (bullet.type.deal);
            });
            deals.sort(function(a,b){
              aDate = new Date(a.title.substring(0,10).split("/").reverse().join("-"));
              bDate = new Date(b.title.substring(0,10).split("/").reverse().join("-"));
              return (aDate - bDate);
            });
            var importantTasks = this.state.bullets.filter(function(bullet) {
              return (bullet.type.task && bullet.signifier.important);
            });
            var otherTasks = this.state.bullets.filter(function(bullet) {
              return (bullet.type.task && !bullet.signifier.important);
            });
            var importantNotes = this.state.bullets.filter(function(bullet) {
              return (bullet.type.note && bullet.signifier.important);
            });
            var otherNotes = this.state.bullets.filter(function(bullet) {
              return (bullet.type.note && !bullet.signifier.important);
            });
            var toFormat = events.concat(deals,importantTasks,otherTasks,importantNotes,otherNotes);
          } else {
            var toFormat = this.state.bullets;
          }
          var formatedBulletsList = toFormat.map(function(bullet) {
            if ((this.props.activeCollection == bullet.collection  || this.props.activeCollection == 0) && ((this.props.filters.task && bullet.type.task) || (this.props.filters.event && bullet.type.event) || (this.props.filters.note && bullet.type.note) || (this.props.filters.deal && bullet.type.deal)) && (this.props.filters.unmarked && (!bullet.signifier.explore && !bullet.signifier.idea && !bullet.signifier.important) || (this.props.filters.explore && bullet.signifier.explore) || (this.props.filters.important && bullet.signifier.important) || (this.props.filters.idea && bullet.signifier.idea)) && !(this.props.filters.hidedone && bullet.status.finished)) {
              var findCollectionNameById = function(collections,id) {
                if (id == 0) return;
                for (var i=0; i<collections.length; i++) {
                  if(collections[i].id == id) {
                    return collections[i].title;
                  }
                }
              };
              var bulletClasses = cx({
                'list-group-item': true,
                'blurred': bullet.status.finished
              });
              var typeClasses = cx({
                'glyphicon': true,
                'glyphicon-unchecked': bullet.type.task && !bullet.status.finished,
                'glyphicon-check': bullet.type.task && bullet.status.finished,
                'glyphicon-calendar': bullet.type.event,
                'glyphicon-pencil': bullet.type.note,
                'glyphicon-gbp': bullet.type.deal
              });
              var importantClasses = cx({
                'glyphicon': true,
                'glyphicon-fire': true,
                'light': !bullet.signifier.important 
              });
              var exploreClasses = cx({
                'glyphicon': true,
                'glyphicon-eye-open': true,
                'light': !bullet.signifier.explore 
              });
              var ideaClasses = cx({
                'glyphicon': true,
                'glyphicon-info-sign': true,
                'light': !bullet.signifier.idea 
              });
              var bulletCollectionName = findCollectionNameById(this.props.collections, bullet.collection);
              
              return ( 
                  <li className={bulletClasses} id={bullet.id} draggable="true" onDragStart={this.onDragStart} onDragEnd={this.onDragEnd}><span className="handle"></span>{' '}<span className={typeClasses} onClick={this.toggleFinished} id={bullet.id}></span>{' '}<span className="editableBullet" id={bullet.id} contentEditable="true" onBlur={this.changeBulletTitle}>{bullet.title}</span>{' '}<span className="label label-default">{bulletCollectionName}</span>{' '}<span className={importantClasses} onClick={this.toggleImportant} id={bullet.id}></span>{' '}<span className={exploreClasses} onClick={this.toggleExplore} id={bullet.id}></span>{' '}<span className={ideaClasses} onClick={this.toggleIdea} id={bullet.id}></span></li>
              );
            }
          }, this); // again to make sure we have the right scope on -this-
          
          return (
            <div className="BulletsList">
              <ul className="list-group">
                <BulletForm collections={this.props.collections} activeCollection={this.props.activeCollection} onNewBullet={this.saveNewBullet}/>
                {formatedBulletsList}
              </ul>
            </div>
          );
        }
      });      

      var BulletForm = React.createClass({
        getInitialState: function() {
          return({newBullet: {id:guid(), title: "", type: { task: true, note: false, event: false, deal: false}, status: { finished: false, irrelevant: false}, signifier: {important: false, explore: false, idea: false}, collection: this.props.activeCollection}, input:""});
        },
        componentDidMount: function() {
          this.refs.bulletInput.getDOMNode().focus();
        },
        setType: function(e) {
          var newBullet = this.state.newBullet;
          if (!newBullet.type[e.target.id]) { // no toggle if type already set
            newBullet.type.task = false;
            newBullet.type.note = false;
            newBullet.type.event = false;
            newBullet.type.deal = false;
            newBullet.type[e.target.id] = true;      
            this.setState({newBullet: newBullet});
          }
        },
        setSignifier: function(e) {
          var newBullet = this.state.newBullet;
           newBullet.signifier[e.target.id] = !newBullet.signifier[e.target.id];
          this.setState({newBullet: newBullet});
        },
        onInputChange : function(e){
          this.setState({newBullet: this.state.newBullet, input: e.target.value});
        },
        saveBullet: function(e) {
          e.preventDefault();
          if (this.state.input != "") {
            var newBullet = this.state.newBullet;
            newBullet.title = this.state.input;
            this.props.onNewBullet(newBullet);
            this.setState({newBullet: {id:guid(), title: "", type: { task: true, note: false, event: false, deal: false}, status: { finished: false, irrelevant: false}, signifier: {important: false, explore: false, idea: false}, collection: this.props.activeCollection}, input:""});
          }
        },
        render: function() {
          var cx = React.addons.classSet;
          var taskClasses = cx({
            'glyphicon': true,
            'glyphicon-unchecked': true,
            'light': !this.state.newBullet.type.task 
          });
          var noteClasses = cx({
            'glyphicon': true,
            'glyphicon-pencil': true,
            'light': !this.state.newBullet.type.note,
            'cursor': true 
          });
          var eventClasses = cx({
            'glyphicon': true,
            'glyphicon-calendar': true,
            'light': !this.state.newBullet.type.event,
            'cursor': true
          });
          var dealClasses = cx({
            'glyphicon': true,
            'glyphicon-gbp': true,
            'light': !this.state.newBullet.type.deal,
            'cursor': true
          });
          var importantClasses = cx({
            'glyphicon': true,
            'glyphicon-fire': true,
            'light': !this.state.newBullet.signifier.important
          });
          var exploreClasses = cx({
            'glyphicon': true,
            'glyphicon-eye-open': true,
            'light': !this.state.newBullet.signifier.explore
          });
          var ideaClasses = cx({
            'glyphicon': true,
            'glyphicon-info-sign': true,
            'light': !this.state.newBullet.signifier.idea 
          });
          
          var findCollectionNameById = function(collections,id) {
            if (id == 0) return;
            for (var i=0; i<collections.length; i++) {
              if(collections[i].id == id) {
                return collections[i].title;
              }
            }
          };
          
          this.state.newBullet.collection = this.props.activeCollection;
          var bulletCollectionName = findCollectionNameById(this.props.collections, this.state.newBullet.collection);
          
          return (
            <li className="list-group-item">
              <form className="form-inline" onSubmit={this.saveBullet}>
                <span className={taskClasses} onClick={this.setType} id="task"></span>
                {' '}
                <span className={noteClasses} onClick={this.setType} id="note"></span>
                {' '}
                <span className={eventClasses} onClick={this.setType} id="event"></span>
                {' '}
                <span className={dealClasses} onClick={this.setType} id="deal"></span>
                {' '}
                <input type="text" ref="bulletInput" className="bullet-input form-control" onChange={this.onInputChange} value={this.state.input} />
                {' '}
                <span className="label label-default">{bulletCollectionName}</span>
                {' '}
                <span className={importantClasses} onClick={this.setSignifier} id="important"></span>
                {' '}
                <span className={exploreClasses} onClick={this.setSignifier} id="explore"></span>
                {' '}
                <span className={ideaClasses} onClick={this.setSignifier} id="idea"></span>
              </form>
            </li>
          );
        }
      });

      React.renderComponent(
        <App />,
        document.getElementById('content')
      );
      
      $('.tip').tooltip();
    </script>
  </body>
</html>