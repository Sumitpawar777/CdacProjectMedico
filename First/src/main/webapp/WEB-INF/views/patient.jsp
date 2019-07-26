<%@page import="com.team.medico.model.User"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="no-cache-store.jsp" %>  
<%@ page errorPage="error.jsp" %>  
<%
	User loggedUser = (User)session.getAttribute("user");
	if(loggedUser !=null && loggedUser.getEmailId()!=null){
	User user  = (User)session.getAttribute("user"); 
%>
 
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h1>Welcome User</h1>
	<h1><%=user.getUserName() %></h1>
	<a href="logout">logout</a>
	<a href="video">video</a>
	<a href="completeProfile">complete Profile</a>
	<form action="bookAppointment" method="post">
    Select a Category:&nbsp;
    <select name="category">
        <c:forEach items="${docList}" var="category">
            <option value="${category.getEmailId()}">${category.getEmailId()}</option>
        </c:forEach>
    </select>
    <br/><br/>
    <input type="submit" value="Book" />
</form>
	
	<a href="bookAppointment">Book Appointment</a>
	
</body>
</html>
<% 
	}else{
	response.sendRedirect("logout");	
	}
%>