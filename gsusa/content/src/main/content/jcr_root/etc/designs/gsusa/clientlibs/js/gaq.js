   var _gaq = _gaq || [];

   // if this is cookie site also track on the separate cookie account
   // this has to be firing first before main one, otherwise all event clicks will be tied to "-35" account and not "-1" account
   if (window.location.pathname.substring(0,('/program/gs_cookies').length) == '/program/gs_cookies') {
		_gaq.push(
		  ['_setAccount', 'UA-2646810-35'],
		  ['_trackPageview']
		  );	  
	}	  

   _gaq.push(
      ['_setAccount', 'UA-2646810-1'],
      ['_trackPageview']
      );
	
   var gaAddons = gaAddons || [];
   gaAddons.push(
      ['_trackOutbound'],
      ['_trackDownloads'],
      ['_trackMailto'],
      ['_trackRealBounce']
      );

   (function() {
      var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
      ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  
      var gaAddons = document.createElement('script'); gaAddons.type = 'text/javascript'; gaAddons.async = true;
      gaAddons.src = '/etc/designs/gsusa/clientlibs/js/gaAddons-2.0.8.min.js';
      s.parentNode.insertBefore(gaAddons, s);
   })();
