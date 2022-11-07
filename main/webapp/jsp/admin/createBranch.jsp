<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Create branch" />
	</jsp:include>
</head>
<body>
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<section class="form">
			<h1 class="form-title">Create branch</h1>
			<form action="/bank-app/admin/branches/branch/create" method="post">
				<section class="branch">
					<h3>Branch Details:</h3>
					<label>Name:</label>
					<input type="text" placeholder="branch name" name="name" maxlength="20" 
						value="${ branchName }" required>
					<p>Address:</p>
					<jsp:include page="/jsp/components/addressForm.jsp" />		
				</section>
				
				<section class="manager">
					<h3>Manager Details:</h3>
					
					<jsp:include page="/jsp/components/employeeRegistrationForm.jsp">
						<jsp:param name="employeeType" value="manager" />
					</jsp:include>			
				
				</section>
				
				<button>Create branch</button>
			</form>
		</section>
	</main>
</body>
</html>