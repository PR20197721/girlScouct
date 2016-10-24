<script type="text/template" id="tweetsTemplate">
    <% _.each(tweets, function (tweet) { %>
    <div class="tweet">
        <p>@<%= tweet.from_user %></p>
        <p class="text"><%= tweet.text %></p><p><%= tweet.location %></p>
    </div>
    <% }); %>
</script>