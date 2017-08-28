<%-- 
    Document   : jsonBootStrapTest
    Created on : Oct 13, 2016, 5:12:58 PM
    Author     : Tan Ming Kwang & Cristabel Lau
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Json BootStrap Test Page</title>
    </head>
    <body>
        <form action="json/bootstrap"  method="post" enctype="multipart/form-data">
            File:
            <input type="file" name="fileToUpload" /><br />
            <input type="text" name="token"/>
            <!-- substitute the above value with a valid token -->
            <input type="submit" value="Bootstrap" />
        </form>
    </body>
</html>
