<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("index.jsp");
    } else {
        if (session.getAttribute("type") == null) {
            response.sendRedirect("userHome.jsp");
        }
    }
%>