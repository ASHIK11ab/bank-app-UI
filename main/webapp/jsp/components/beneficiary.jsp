<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page 
	import = "constant.BeneficiaryType"
 %>

<div>
	<h3>Name: ${ beneficiary.getName() }</h3>
	
	<c:if test="${ beneficiary.getNickName().length() > 0 }">
		<p>Nick Name: ${ beneficiary.getNickName() }</p>
	</c:if>
	
	<c:if test="${ BeneficiaryType.getType(param.type) == BeneficiaryType.OTHER_BANK }">
		<p>Bank Name: ${ beneficiary.getBankName() }
		<p>IFSC: ${ beneficiary.getIFSC() }
	</c:if>
	
	<p><strong>A/C No: ${ beneficiary.getAccountNo() }</strong></p>	
</div>