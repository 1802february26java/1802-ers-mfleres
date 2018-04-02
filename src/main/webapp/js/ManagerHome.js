window.onload = () => {
    viewReimbursementOnLoad();
    viewEmployeeOnLoad();
    document.getElementById("managerHeader").innerHTML=sessionStorage.getItem("employeeUsername");
}

function toggleButtons(toggleBool){
    document.getElementById("viewPending").disabled=toggleBool;
    document.getElementById("viewResolved").disabled=toggleBool;
    document.getElementById("viewEmployeeById").disabled=toggleBool;
    document.getElementById("viewAllEmployeesButton").disabled=toggleBool;
}