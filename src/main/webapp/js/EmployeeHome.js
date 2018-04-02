window.onload = () => {
    document.getElementById("viewEmployeeReimbursements").addEventListener("click", viewMyReimbursements);
    document.getElementById("requestReimbursement").addEventListener("click", requestReimbursement);
    document.getElementById("profile").addEventListener("click",viewProfile);
    document.getElementById("profileModalClose").addEventListener("click",closeProfileModal);
    submitOnLoad();
    document.getElementById("profileUsername").innerHTML = sessionStorage.getItem("employeeUsername");
    document.getElementById("profileFirstName").setAttribute("placeholder",sessionStorage.getItem("employeeFirstName"));
    document.getElementById("profileLastName").setAttribute("placeholder",sessionStorage.getItem("employeeLastName"));
    document.getElementById("profileEmail").setAttribute("placeholder",sessionStorage.getItem("employeeEmail"));
    document.getElementById("employeeHeader").innerHTML=sessionStorage.getItem("employeeUsername");
    updateEmployeeOnLoad();
}

function toggleButtons(toggleBool){
    document.getElementById("viewEmployeeReimbursements").disabled = toggleBool;
    document.getElementById("requestReimbursement").disabled = toggleBool;
}

function viewMyReimbursements(){
    console.log("viewMyReimbursements()");
    requesterId = sessionStorage.getItem("employeeUsername");
    console.log(`employeeId: ${requesterId}`);
    if (requesterId != null) {
        console.log("viewEmployeeReimbursements, id = " + requesterId);
        //AJAX
        let xhr = new XMLHttpRequest();

        xhr.onreadystatechange = () => {
            console.log(`${xhr.readyState},${xhr.status}`)
            if (xhr.readyState === 4 && xhr.status === 200) {
                let data = JSON.parse(xhr.responseText);
                console.log(data);
                
                //Present the data to the user
                presentReimbursements(data);
            }
        };

        xhr.open("GET", `viewEmployeeReimbursements.do?employeeId=${requesterId}&status=PENDING`);

        xhr.send();
    }
}

function viewProfile() {
    document.getElementById("profileModal").style.display="block";
}

function closeProfileModal(){
    document.getElementById("profileModal").style.display="none";
}