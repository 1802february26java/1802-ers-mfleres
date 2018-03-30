window.onload = () => {
    document.getElementById("viewEmployeeReimbursements").addEventListener("click", viewMyReimbursements);
    document.getElementById("requestReimbursement").addEventListener("click", requestReimbursement);
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

function requestReimbursement() {
    
}