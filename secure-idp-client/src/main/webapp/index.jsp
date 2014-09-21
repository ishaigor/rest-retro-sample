<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="org.springframework.security.oauth2.common.OAuth2AccessToken,
    org.springframework.security.core.Authentication,
    org.springframework.security.core.context.SecurityContext,
    org.springframework.security.core.context.SecurityContextHolder,org.shaigor.rest.retro.auth.CustomAuthenticationDetails,
    org.springframework.security.oauth2.client.OAuth2RestTemplate,
    org.springframework.security.oauth2.common.OAuth2AccessToken"%>
<%@ taglib prefix="authz"
        uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
  <html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <% 
     OAuth2RestTemplate template = (OAuth2RestTemplate) request.getAttribute("oauth2RestTemplate");
     OAuth2AccessToken token = template.getAccessToken();
     %>
      <meta name="_bearer" content="<%= token.getValue() %>"/>
      <title>Secure IDP client</title>
    </head>
    <body>
      <script type='text/javascript' src='./js/jquery-1.11.1.min.js'></script>
      <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.2.22/angular.min.js"  type="text/javascript"></script>
      <script type="text/javascript" src="./js/ng-infinite-scroll.min.js"></script>
      <script src="./js/words.js"  type="text/javascript"></script>
      <div>
          <form action="<c:url value="/logout.do"/>" method="POST">
              <button class="btn btn-primary" type="submit">Logout</button>
          </form>
      </div>
      <div ng-app='wordsApp' ng-controller='WordsController'>
        <div infinite-scroll='wordsService.nextPage()' infinite-scroll-disabled='wordsService.busy' infinite-scroll-distance='1'>
          <div ng-repeat='word in wordsService.items'>
            <span class='word'>{{word}}</span>
            <div style='clear: both;'>
          </div>
         </div>
         <div ng-show='wordsService.busy'>Loading data...</div>
       </div>
     </div>
  </body>
</html>

