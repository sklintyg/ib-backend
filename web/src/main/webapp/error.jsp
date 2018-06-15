<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"  errorPage="" %>
<%-- error.jsp only purpose now is to redirect to the app launch page, with a possible reason code parameter to show in app.exit view --%>
<!doctype html>
<html class="no-js">
<head>
<meta name="ROBOTS" content="nofollow, noindex"/>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="description" content="">
<meta name="viewport" content="width=device-width">
<script>window.location.href='/index.html?reason=<%out.print(request.getParameter("reason"));%>';</script>
<title>Intygsbeställning - omdirigerar...</title>
</head>
<body>
<!-- dummy content to make IE actually process this page and not show the "friendly" built in variant.
 The magic limit seems to be > 512 bytes of content to make IE happy -->
  <!-- dummy content to make IE actually process this page and not show the "friendly" built in variant.
   The magic limit seems to be > 512 bytes of content to make IE happy -->
  <!-- dummy content to make IE actually process this page and not show the "friendly" built in variant.
 The magic limit seems to be > 512 bytes of content to make IE happy -->
  <!-- dummy content to make IE actually process this page and not show the "friendly" built in variant.
 The magic limit seems to be > 512 bytes of content to make IE happy -->
</body>
</html>
