<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Add new employee</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/manager/components/navbar.jsp" />
	
	<main class="container">
		<section class="wrapper">
			<h1>Add Employee</h1>
			<form action="/bank-app/manager/employees/employee/create" method="post">
				
				<jsp:include page="/jsp/components/employeeRegistrationForm.jsp">
					<jsp:param name="employeeType" value="employee" />
				</jsp:include>
				
				<button>Add employee</button>
			</form>
		</section>
	</main>
</body>
</html>