function viewEmployeeOnLoad() {
    console.log("employeeViewOnLoad");
    document.getElementById("viewAllEmployeesButton").addEventListener("click", viewAllEmployees);
}

function viewAllEmployees() {
    console.log("viewEmployees");
    previousView = 1;
    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`${xhr.readyState},${xhr.status}`);
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            //Present the data to the user
            presentEmployees(data);
        }
    };

    xhr.open("GET", `viewAll.do?fetch=LIST`);

    xhr.send();
}

function presentEmployees(data) {
    console.log("presentEmployees");
    if (data.message) {
        //Something went wrong
        let errorMessage = data.message;
        document.getElementById("listMessage").innerHTML = `<span class="label label-danger label-center">${errorMessage}</span>`;
    } else {
        //Clear Error Message
        document.getElementById("listMessage").innerHTML = `<span class="label label-danger label-center"></span>`
        //Display table of all reimbursements
        let employeeTable = document.getElementById("table");

        //Setup table with headers
        employeeTable.innerHTML = `<thread class='thread-light'><tr>
        <th>ID</th>
        <th>Username</th>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Email</th>
        </tr></thread>`;

        data.forEach((employee) => {
            console.log("inserting an employee");
            let employeeRow = document.createElement("tr");

            //ID
            createNodeOnTableRow(employeeRow, `${employee.id}`);

            //Username
            createNodeOnTableRow(employeeRow, `${employee.username}`);

            //First Name
            createNodeOnTableRow(employeeRow, `${employee.firstName}`);

            //Last Name
            createNodeOnTableRow(employeeRow, `${employee.lastName}`);

            //Email
            createNodeOnTableRow(employeeRow, `${employee.email}`);

            console.log(employeeRow.innerHTML);
            employeeTable.appendChild(employeeRow);
        });
    }
}

function createNodeOnTableRow(rowElement, dataText) {
    let dataNode = document.createElement("td");
    dataNodeText = document.createTextNode(dataText);
    dataNode.appendChild(dataNodeText);

    rowElement.appendChild(dataNode);
}