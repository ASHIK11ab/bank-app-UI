<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Add new Employee" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/manager/components/navbar.jsp" />
	
	<main class="container">
		<section class="wrapper">
			<h1>Add Employee</h1>
			<form action="/bank-app/manager/employee/create" method="post">
				
				<jsp:include page="/jsp/components/employeeRegistrationForm.jsp">
					<jsp:param name="employeeType" value="employee" />
				</jsp:include>
				
				<button>Add employee</button>
			</form>
		</section>
	</main>
</body>
</html>