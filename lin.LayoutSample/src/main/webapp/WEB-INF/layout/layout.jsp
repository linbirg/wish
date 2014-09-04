<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<base href="<%=basePath%>">
<head>
<%@include file="/WEB-INF/layout/_include.jsp"%>
<title>花花世界</title>
</head>
<body>
	<%@include file="/WEB-INF/layout/_header.jsp"%>

	<div class="container-fluid">
		<div class="row">
			<%@ include file="/WEB-INF/layout/_menu.jsp"%>
			<div class="page-content">
				<div class="container-fluid" data-role="main">
					<!-- Main component for a primary marketing message or call to action -->

					${yield}

				</div>
				<!-- container-fluid -->
			</div>
			<!-- page-content -->
		</div>


	</div>
	<!-- /container -->

	<%@include file="/WEB-INF/layout/_import.jsp"%>
</body>
</html>
