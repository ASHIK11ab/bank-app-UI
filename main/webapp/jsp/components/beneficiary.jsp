<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page 
	import = "constant.BeneficiaryType"
	import = "cache.AppCache"
 %>

<div>
	<h3>Name: ${ beneficiary.getName() }</h3>
	<p>Nick Name: ${ beneficiary.getNickName() }</p>
	
	<c:if test="${ BeneficiaryType.getType(param.type) == BeneficiaryType.OTHER_BANK }">
		<p>Bank Name: ${ AppCache.getIntegratedBank(beneficiary.getBankId()).getName() }
		<p>IFSC: ${ beneficiary.getIFSC() }
	</c:if>
	
	<p><strong>A/C No: ${ beneficiary.getAccountNo() }</strong></p>	
</div>