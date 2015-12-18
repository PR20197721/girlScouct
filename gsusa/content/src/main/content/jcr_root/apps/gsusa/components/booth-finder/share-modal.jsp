<div class="share-modal">
	<a href="#" data-reveal-id="shareModal" class="button">{{buttonCaption}}</a>

	<div id="shareModal" class="reveal-modal share-modal" data-reveal
		aria-labelledby="firstModalTitle" aria-hidden="true" role="dialog">
		<a class="close-reveal-modal icon-button-circle-cross"
			aria-label="Close"></a>
		<div class="float-left">
			{{#if modFilePath}} <img src="{{modFilePath}}" alt="" /> {{/if}}
		</div>
		<div class="float-right">
			<h4>{{header}}</h4>
			<p>{{desc}}</p>
			<a class="button" onclick="postToFeed{{uniqueID}}(); return false;">
				Share on Facebook
				<i class="icon-social-facebook"></i>
			</a>
			<a class="button" target="_blank" href="https://twitter.com/share?text={{tweet}}&hashtags={{hashTags}}">
				Share on Twitter
				<i class="icon-social-twitter-tweet-bird"></i>
			</a>
		</div>
	</div>
</div>