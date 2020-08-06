$(document).ready(function(){
    var seconds = 15;
    function countdown() {
        seconds = seconds - 1;
        if (seconds < 0) {
            // Chnage your redirection link here
            window.location = "/content/girlscouts-vtk/sso/landing.html";
        } else {
            // Update remaining seconds
            document.getElementById("countdown").innerHTML = seconds;
            // Count down using javascript
            window.setTimeout(countdown, 1000);
        }
    }
    function afterLogout() {
        countdown();
        // Handle removal/deletion of any session cookies
        // Define any additional logic to perform on the logged out user's browser
    }
    afterLogout();
});

