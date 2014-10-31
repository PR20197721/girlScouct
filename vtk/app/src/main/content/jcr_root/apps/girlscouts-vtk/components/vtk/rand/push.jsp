



<!DOCTYPE html>
<html>
    <body onload = "test()">
          <h1>dsdGetting server updates</h1>
          <div id="result"></div>
          <script>
              function test()
              {
                  alert("wtf");
                
                  if(typeof(EventSource) != "undefined")
                  {
                      var source = new EventSource ("http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.serverPush.html");
                      source.onmessage = function(event)
                      {
                          document.getElementById("result").innerHTML += event.data + "<br/>";
                      };
                  }
                  else
                  {
                      document.getElementById("result").innerHTML = "Sorry, your browser does not support   server-sent events...";
                  }
                  
              }
          </script>
    </body>
    </html>