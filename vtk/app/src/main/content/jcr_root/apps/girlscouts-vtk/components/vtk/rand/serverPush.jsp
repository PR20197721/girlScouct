<%
    /*
response.setContentType("text/event-stream;charset=UTF-8");
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Connection", "keep-alive");

    java.util.Date date = new java.util.Date();
    out.write("event: server-time\n\n");
    out.write("data: "+date.toString() + "\n\n");
    out.flush();
    try {
        Thread.currentThread().sleep(5000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    */
    
  //content type must be set to text/event-stream
    response.setContentType("text/event-stream");   

    //encoding must be set to UTF-8
    response.setCharacterEncoding("UTF-8");

    java.io.PrintWriter writer = response.getWriter();

    for(int i=0; i<2; i++) {

        writer.write("data: "+ System.currentTimeMillis() +"\n\n");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    writer.close();
%>