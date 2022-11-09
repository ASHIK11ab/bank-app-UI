<label>Name:</label>
<input type="text" placeholder="name" name="name" maxlength="30" value="${ bank.getName() }" required>
<label>Email:</label>
<input type="email" placeholder="email address" name="email" maxlength="30" value="${ bank.getEmail() }" required>
<label>Phone:</label>
<input type="number" placeholder="contact number" name="phone" value="${ bank.getPhone() }" required>
<label>Api URL:</label>
<input type="text" placeholder="bank api url" name="api-url" maxlength="70" value="${ bank.getApiURL() }" required>