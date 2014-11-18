<!DOCTYPE html>
<html>
<head>
<title>Backbone Application</title>
<h1>test7</h1>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/handlebars.js/1.3.0/handlebars.js"></script>

  <script>
  
  /*! backbone-polling - v1.0.0 - 2014-01-16
  * https://github.com/pedrocatre/backbone-polling
  * Copyright (c) 2014 Pedro Catré; Licensed MIT */
  !function(a,b){"use strict";"function"==typeof define&&define.amd?define(["underscore","jquery"],b):a.BackbonePolling=b(a._,a.jQuery)}(this,function(a,b){"use strict";return{_backbonePollTimeoutId:void 0,_backbonePollEnabled:!1,_backbonePollSettings:{refresh:1e3,fetchOptions:{},retryRequestOnFetchFail:!0},configure:function(a){this._backbonePollSettings=b.extend(!0,{},this._backbonePollSettings,a)},startFetching:function(){return this._backbonePollEnabled=!0,this._refresh(1),this},_refresh:function(b){return this._backbonePollTimeoutId=setTimeout(a.bind(function(){this._backbonePollTimeoutId&&clearTimeout(this._backbonePollTimeoutId),this._backbonePollEnabled&&(this.fetchRequest=this.fetch(this._backbonePollSettings.fetchOptions),this.fetchRequest.done(a.bind(function(){this.trigger("refresh:loaded"),this._refresh(this._backbonePollSettings.refresh)},this)).fail(a.bind(function(){this.trigger("refresh:fail"),this._backbonePollSettings.retryRequestOnFetchFail?this._refresh(this._backbonePollSettings.refresh):this.stopFetching()},this)).always(a.bind(function(){this.trigger("refresh:always")},this)))},this),b),this},abortPendingFetchRequests:function(){return a.isUndefined(this.fetchRequest)||a.isUndefined(this.fetchRequest.abort)||this.fetchRequest.abort(),this},isFetching:function(){return!a.isUndefined(this._backbonePollTimeoutId)},stopFetching:function(){return this._backbonePollEnabled=!1,this.isFetching()&&(this._backbonePollTimeoutId&&clearTimeout(this._backbonePollTimeoutId),this._backbonePollTimeoutId=void 0),this.abortPendingFetchRequests(),this}}});
   
   
  (function (root, factory) {
      'use strict';
      if (typeof define === 'function' && define.amd) {
          // AMD. Register as an anonymous module.
          define(['underscore', 'jquery'], factory);
      } else {
          // Browser globals
          root.BackbonePolling = factory(root._, root.jQuery);
      }
  }(this, function (_, $) {
      'use strict';

      return {

          /**
           * Id returned by the setTimeout function that the plugin uses to specify a delay between fetch requests to the
           * data source
           */
          _backbonePollTimeoutId: undefined,

          /**
           * Control variable used to stop fetch requests
           */
          _backbonePollEnabled: false,

          /**
           * Default settings for the plugin
           */
          _backbonePollSettings: {
              refresh: 1000,                      // rate at which the plugin fetches data
              fetchOptions: {},                   // options for the fetch request
              retryRequestOnFetchFail: true       // automatically retry request on fetch failure
          },

          /**
           * Specify custom options for the plugin
           * @param pollOptions object used to customize the plugins behavior
           */
          configure: function(pollOptions){
              this._backbonePollSettings = $.extend(true, {}, this._backbonePollSettings, pollOptions);
          },

          /**
           * Starts the process of polling data from the server
           * @returns {*}
           */
          startFetching: function() {
              this._backbonePollEnabled = true;
              this._refresh(1);
              return this;
          },

          /**
           * Periodically fetch data from a data source
           * @param refreshRateMs rate in milliseconds at which the plugin fetches data
           * @returns {*}
           * @private
           */
          _refresh: function (refreshRateMs) {
              this._backbonePollTimeoutId = setTimeout(_.bind(function() {
                  if (this._backbonePollTimeoutId) {
                      clearTimeout(this._backbonePollTimeoutId);
                  }
                  // Return if _refresh was called but the fetching is stopped
                  // should not go this far since the timeout is cleared when fetching is stopped.
                  if(!this._backbonePollEnabled) { return; }

                  this.fetchRequest = this.fetch(this._backbonePollSettings.fetchOptions);

                  this.fetchRequest.done(_.bind(function() {
                          this.trigger('refresh:loaded');
                          this._refresh(this._backbonePollSettings.refresh);
                      }, this)).fail(_.bind(function() {
                          this.trigger('refresh:fail');

                          // If retryRequestOnFetchFail is true automatically retry request
                          if(this._backbonePollSettings.retryRequestOnFetchFail) {
                              this._refresh(this._backbonePollSettings.refresh);
                          } else {
                              this.stopFetching();
                          }
                      }, this)).always(_.bind(function() {
                          this.trigger('refresh:always');
                      }, this));
              }, this), refreshRateMs);
              return this;
          },

          /**
           * Abort pending fetch requests
           * @returns {*}
           */
          abortPendingFetchRequests: function() {
              if(!_.isUndefined(this.fetchRequest) && !_.isUndefined(this.fetchRequest['abort'])) {
                  this.fetchRequest.abort();
              }
              return this;
          },

          /**
           * Checks to see if the plugin is polling data from a data source
           * @returns {boolean} true if is fetching, false if it is not fetching
           */
          isFetching: function() {
              return !(_.isUndefined(this._backbonePollTimeoutId));
          },

          /**
           * Stops the process of polling data from the server
           * @returns {*}
           */
          stopFetching: function() {
              this._backbonePollEnabled = false;
              if(this.isFetching()) {
                  if (this._backbonePollTimeoutId) {
                      clearTimeout(this._backbonePollTimeoutId);
                  }
                  this._backbonePollTimeoutId = undefined;
              }
              this.abortPendingFetchRequests();
              return this;
          }

      };
  }));
  </script>
</head>
<body>
  
<div class="content"></div>
 

 
<script id="vtk-template" type="text/x-handlebars-template">
  <div>testasdfadsfasdfasdfasdfasdfasdf</div>
   {{#each []}}
          
 		<fieldset>**{{this.usid}}**</fieldset>
		<div id="adfad">aaaa</div>
    {{/each}}
  </script>
  
<script type="text/javascript">
 
 
var YearPlan = Backbone.Model.extend({
    defaults:{
    	usid: 'New YearPlan',
    	yp_cng:"true"        
    },
    initialize: function() {
    }
});



var YearPlanCollection = Backbone.Collection.extend({
    defaults: {
        model: YearPlan
    },
    model: YearPlan,
	url: 'http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test0.html',
    parse: function(response){
      // var artistListView = new ArtistListView();   	
       return response.items;
    },
    initialize: function() {      
    	/*
        this.fetch().done(function(){
   		 var artistListView = new ArtistListView();
        })   
        */
    }

});

//_.extend(YearPlanCollection.prototype, BackbonePolling);
var y = new YearPlanCollection();
//y.startFetching();
y.fetch().done(function(){
   		 var artistListView = new ArtistListView();
        })  
        
var ArtistListView = Backbone.View.extend({
    el: '.content',
    initialize:function(){
		 this.render();
    },
    render: function () {
    	
    	var source = $('#vtk-template').html();
        var template = Handlebars.compile(source);
        var html = template(y.toJSON());
        this.$el.html(html);    
    }
});



</script>
</body>
</html> 