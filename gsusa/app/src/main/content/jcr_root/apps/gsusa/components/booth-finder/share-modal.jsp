<div class="share-modal">
	<a href="#" data-reveal-id="shareModal" class="button">{{buttonCaption}}</a>

	<div id="shareModal" class="reveal-modal share-modal" data-reveal
		aria-labelledby="firstModalTitle" aria-hidden="true" role="dialog">
		<a class="close-reveal-modal icon-button-circle-cross"
			aria-label="Close"></a>
		<div class="float-left">
			{{#if imageFilePath}} <img src="{{imageFilePath}}" alt="" /> {{/if}}
		</div>
		<div class="float-right">
			<h4>{{header}}</h4>
			<p>{{desc}}</p>
			<a class="button" onclick="postToFeed{{uniqueID}}(); return false;">
				Share on Facebook <i class="icon-social-facebook"></i>
			</a> <a class="button" target="_blank"
				href="https://twitter.com/share?text={{tweet}}&hashtags={{hashTags}}">
				Share on Twitter <i class="icon-social-twitter-tweet-bird"></i>
			</a>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		var scriptTag = document.createElement("script");
		scriptTag.type = "text/javascript"
		scriptTag.src="http://connect.facebook.net/en_US/all.js";
		scriptTag.async = true;
		document.getElementsByTagName("head")[0].appendChild(scriptTag);

		scriptTag.onload=initFB;
		scriptTag.onreadystatechange = function () {
		  if (this.readyState == 'complete' || this.readyState == 'loaded') initFB();
		}
	});
	function initFB() {
		FB.init({appId: "{{facebookId}}", status: true, cookie: true});
	}

      function postToFeed{{uniqueID}}() {

        // calling the API ...
        var obj = {
          {{#if imageFilePath}} picture: location.host + "{{imageFilePath}}", {{/if}}
          method: "feed",
          link: "{{url}}",
          name: "{{escapeDoubleQuotes title}}",
          caption: "WWW.GIRLSCOUTS.ORG",
          description: "{{escapeDoubleQuotes desc}}"
        };

        function callback(response) {
        }

        FB.ui(obj, callback);
      }
<{{!}}/script>