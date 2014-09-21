<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Please Log In</title>
</head>
<body>
   <c:if test="${not empty param.authentication_error}">
       <p class="error">Your login attempt was not successful.</p>
   </c:if>
   <c:if test="${not empty param.authorization_error}">
       <p class="error">You are not permitted to access that resource.</p>
   </c:if>

    <form action="<c:url value="/j_spring_security_check"/>" method="post">
        <fieldset>
            <legend>
                <h2>Login</h2>
            </legend>
                <label for="username">Username:</label> <input id="username"
                    type='text' name='j_username'
                    value="Ashlie" />
                <label for="password">Password:</label> <input id="password"
                    class="form-control" type='text' name='j_password' value="123456" />
            <button class="btn btn-primary" type="submit">Login</button>
            <input type="hidden" name="${_csrf.parameterName}"
                value="${_csrf.token}" />
        </fieldset>
    </form>
</body>
</html>
