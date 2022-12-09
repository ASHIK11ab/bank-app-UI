/**
 * Handles nominee facility and displays the dropdown
 */
 console.log('loaded nomineeForm.js');
 const nomineeSection = document.getElementById("nominee-section");
 
 // Based on the selected radio button nominee form is displayed.
 document.querySelectorAll(".nominee-facility-input").forEach( (element) => {
	element.onclick = function() {
		if(this.value == 0) {
			nomineeSection.classList.add('hidden');
			// When nominee facility is not required remove required attributes
			// from input elements.
			
			document.querySelectorAll('#nominee-section input, #nominee-section select').forEach( (element) => {
				element.removeAttribute('required');
			} );
						
		}
		else {
			nomineeSection.classList.remove('hidden');
			document.querySelectorAll('#nominee-section input, #nominee-section select').forEach( (element) => {
				element.setAttribute('required', true);
			} );			
		}
	}
} );