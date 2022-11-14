<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Integrated Banks" />
	</jsp:include>
</head>
<body>	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<h1>Integrated Banks: ${ integratedBanks.size() }</h1>
		<div class="group">
			<c:choose>
				<c:when test="${ integratedBanks.size() == 0 }">
					<h2>No integrated banks</h2>
				</c:when>
				
				<c:when test="${ integratedBanks.size() > 0 }">
					<c:forEach items="${ integratedBanks }" 
						var="integratedBank" varStatus="loop">
						<div class="item">
							<a href="/bank-app/admin/integrated-banks/${ integratedBank.getId() }/view">
								${ loop.index + 1}. ${ integratedBank.getName() }
							</a>
						</div>
					</c:forEach>				
				</c:when>
			</c:choose>
			
		</div>
	</main>
</body>
</html>