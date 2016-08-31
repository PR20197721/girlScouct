String fileName = request.getParameter("fileName");
37
        if(fileName == null || fileName.equals("")){
38
            throw new ServletException("File Name can't be null or empty");
39
        }
40
        File file = new File(request.getServletContext().getAttribute("FILES_DIR")+File.separator+fileName);
41
        if(!file.exists()){
42
            throw new ServletException("File doesn't exists on server.");
43
        }
44
        
45
        ServletContext ctx = getServletContext();
46
        InputStream fis = new FileInputStream(file);
47
        String mimeType = ctx.getMimeType(file.getAbsolutePath());
48
        response.setContentType(mimeType != null? mimeType:"application/octet-stream");
49
        response.setContentLength((int) file.length());
50
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
51
 
52
        ServletOutputStream os       = response.getOutputStream();
53
        byte[] bufferData = new byte[1024];
54
        int read=0;
55
        while((read = fis.read(bufferData))!= -1){
56
            os.write(bufferData, 0, read);
57
        }
58
        os.flush();
59
        os.close();
60
        fis.close();
61
        
