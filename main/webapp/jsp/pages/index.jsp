<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Your Bank | Home Page" />
	</jsp:include>
</head>
<body>
	
	<jsp:include page="/jsp/components/generalNavbar.jsp" />
	
	<main class="container">
		<h1>Welcome to <span class="bank-name">Your Bank</span></h1>
		<section>
			<h2>About <span class="bank-name">Your Bank</span></h2>
			<p> For the past 50 years <span class="bank-name">Your Bank</span> has been the serving
			the people. <span class="bank-name">Your Bank</span> has been a key player in the Digital 
			India initiative.</p>
		</section>
	</main>
	
	
	<jsp:include page="/jsp/components/footer.jsp" />
</body>
</html>