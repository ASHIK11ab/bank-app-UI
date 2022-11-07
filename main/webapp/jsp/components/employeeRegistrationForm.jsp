<label>Name:</label>
<input type="text" placeholder="${ param.employeeType } name" name="${ param.employeeType }-name"
	value="${ name }" maxlength="20" required>
<label>Email:</label>
<input type="email" placeholder="email address" name="${ param.employeeType }-email" 
	value="${ email }" maxlength="30" required>
<label>Phone:</label>
<input type="number" placeholder="contact number" name="${ param.employeeType }-phone" 
	value="${ phone }" required>