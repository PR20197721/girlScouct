<%
long currentTime = 0;
if (session.getAttribute("inkoo") == null) {
	session.setAttribute("inkoo", new java.util.Date());
} else {
	session.setAttribute("inkoo", new java.util.Date());
	currentTime = ((java.util.Date)session.getAttribute("inkoo")).getTime();
}
if (currentTime == 0) {
%>
[	
	{
      "yp_cng": "Jane",
      "usid": "test",
      "age": "32",
      "photo": "test_photo",
      "video": "test_video"
    },
    {
      "yp_cng": "James",
      "usid": "Hamm",
      "age": "56",
      "photo": "test_photo",
      "video": "test_video"
    }
]
<%
} else {
%>
[
	{
      "yp_cng": "Jane",
      "usid": "test",
      "age": "32",
      "photo": "test_photo",
      "video": "test_video"
    },
        {
      "yp_cng": "James",
      "usid": "<%= currentTime %> Hamm",
      "age": "<%= currentTime %>",
      "photo": "test_photo",
      "video": "test_video"
    }
]
<%
}
%>