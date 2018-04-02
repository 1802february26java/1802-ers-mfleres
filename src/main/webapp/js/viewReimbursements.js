let previousView = 0;
let previouslyViewedEmployeeId = 0;

function viewReimbursementOnLoad() {
    console.log("viewOnLoad");
    document.getElementById("viewPending").addEventListener("click", viewPendingReimbursements);
    document.getElementById("viewResolved").addEventListener("click", viewResolvedReimbursements);
    document.getElementById("viewEmployeeById").addEventListener("click", viewEmployeeReimbursementsById);
    document.getElementById("resolveModalClose").addEventListener("click",closeModal);
    document.getElementById("modalApprove").addEventListener("click",modalApproveReimbursement);
    document.getElementById("modalDecline").addEventListener("click",modalDeclineReimbursement);
}



function closeModal(){
    document.getElementById("resolveModal").style.display = "none";
}

function viewPendingReimbursements() {
    console.log("viewPending");
    previousView = 1;
    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`${xhr.readyState},${xhr.status}`);
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            //Present the data to the user
            presentReimbursements(data);
        }
    };
    document.getElementById("listMessage").innerHTML = `<span class="label label-info label-center">Processing...</span>`;
    toggleButtons(true);
    xhr.open("GET", `viewPending.do?fetch=LIST`);

    xhr.send();
}

function viewResolvedReimbursements() {
    console.log("viewResolved");
    previousView = 2;
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
    document.getElementById("listMessage").innerHTML = `<span class="label label-info label-center">Processing...</span>`;
    toggleButtons(true);
    xhr.open("GET", `viewResolved.do?fetch=LIST`);

    xhr.send();
}

function viewEmployeeReimbursements(requesterId){
    previousView = 3;
    previouslyViewedEmployeeId = requesterId;
    console.log(`requesterId: ${requesterId}`);
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
        document.getElementById("listMessage").innerHTML = `<span class="label label-default label-center">Processing...</span>`;
        toggleButtons(true);
        xhr.open("GET", `viewEmployeeReimbursements.do?employeeId=${requesterId}`);

        xhr.send();
        
    }
}

function viewEmployeeReimbursementsFromTable(){
    let requesterId = event.target.innerHTML;
    viewEmployeeReimbursements(requesterId);
}

function viewEmployeeReimbursementsById() {
    let requesterId = document.getElementById("employeeInfo").value;
    viewEmployeeReimbursements(requesterId);
}

function presentReimbursements(data) {
    console.log("presentReimbursement");
    if (data.message) {
        //Something went wrong
        let errorMessage = data.message;
        document.getElementById("listMessage").innerHTML = `<span class="label label-danger label-center">${errorMessage}</span>`;
        
    } else {
        //Clear Error Message
        document.getElementById("listMessage").innerHTML = `<span class="label label-danger label-center"></span>`
        //Display table of all reimbursements
        let reimbursementTable = document.getElementById("displayTable");

        //Setup table with headers
        reimbursementTable.innerHTML = `<thread class="mdb-color darken-3"><tr class="text-white">
        <th scope="col">ID</th>
        <th scope="col">Requested</th>
        <th scope="col">Resolved</th>
        <th scope="col">Amount</th>
        <th scope="col">Description</th>
        <th scope="col">Requester</th>
        <th scope="col">Approver</th>
        <th scope="col">Status</th>
        <th scope="col">Type</th>
        </tr></thread>`;
        
        let tableBody= document.createElement("tbody");
        data.forEach((reimbursement) => {
            console.log("inserting a reimbursement");
            let reimbursementRow = document.createElement("tr");

            //ID
            createNodeOnTableRow(reimbursementRow, `${reimbursement.id}`);

            //Requested Time
            createNodeOnTableRow(reimbursementRow, `${dateTimeToString(reimbursement.requested)}`);

            //Resolved Time
            let timeResolved = "N/A";
            //If not null, show the value
            if (reimbursement.resolved) {
                timeResolved = dateTimeToString(reimbursement.resolved);
            }
            createNodeOnTableRow(reimbursementRow, timeResolved);

            //Amount
            createNodeOnTableRow(reimbursementRow, `$${(reimbursement.amount).toFixed(2)}`)

            let description = "N/A";
            if (reimbursement.description) {
                description = reimbursement.description;
            }
            createNodeOnTableRow(reimbursementRow, description);

            //Requester
            let requester = "N/A";
            if (reimbursement.requester) {
                requester = reimbursement.requester.username;
            }
            createRequesterNodeOnTableRow(reimbursementRow, requester);

            //Skipping the receipt blob for now...

            //Approver
            let approver = "N/A";
            if (reimbursement.approver) {
                approver = reimbursement.approver.username;
            }
            createNodeOnTableRow(reimbursementRow, approver);

            //Status
            createResolveStatusNodeOnTableRow(reimbursementRow, `${reimbursement.status.status}`);

            //Type
            createNodeOnTableRow(reimbursementRow, `${reimbursement.type.type}`);

            console.log(reimbursementRow.innerHTML);
            tableBody.appendChild(reimbursementRow);
        });
        reimbursementTable.appendChild(tableBody);
        document.getElementById("listMessage").innerHTML = `<span class="label label-default label-center"></span>`;
    }
    toggleButtons(false);
}

function createNodeOnTableRow(rowElement, dataText) {
    let dataNode = document.createElement("td");
    dataNodeText = document.createTextNode(dataText);
    dataNode.appendChild(dataNodeText);

    rowElement.appendChild(dataNode);
}

function createRequesterNodeOnTableRow(rowElement, dataText, employeeId) {
    let reimbursementDataNode = document.createElement("td");
    let anchorNode = document.createElement("a");
    anchorNode.setAttribute("employeeId",employeeId);
    anchorNode.addEventListener("click",viewEmployeeReimbursementsFromTable);
    let reimbursementDataNodeText = document.createTextNode(dataText);
    anchorNode.appendChild(reimbursementDataNodeText);
    reimbursementDataNode.appendChild(anchorNode);

    rowElement.appendChild(reimbursementDataNode);
}

function createResolveStatusNodeOnTableRow(rowElement, dataText) {
    let reimbursementDataNode = document.createElement("td");
    let anchorNode = document.createElement("a");
    anchorNode.addEventListener("click",updateStatusModal);
    let reimbursementDataNodeText = document.createTextNode(dataText);
    anchorNode.appendChild(reimbursementDataNodeText);
    reimbursementDataNode.appendChild(anchorNode);

    rowElement.appendChild(reimbursementDataNode);
}

function updateStatusModal(){
    let reimbursementIdOfRow = event.target.parentElement.parentElement.firstChild.innerHTML;
    console.log(`Reimbursement ID of row: ${reimbursementIdOfRow}`);
    sessionStorage.setItem("reimbursementID",reimbursementIdOfRow);
    let modalElement = document.getElementById("resolveModal");
    modalElement.style.display="block";
}

function modalApproveReimbursement() {
    console.log("approveReimbursement");
    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`${xhr.readyState},${xhr.status}`);
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            //Present the data to the user
            document.getElementById("listMessage").innerHTML = `<span class="label label-default label-center">${data.message}</span>`;
            refreshReimbursementsTable();
        }
    };

    let reimbursementId = sessionStorage.getItem("reimbursementID");
    xhr.open("POST", `resolveReimbursement.do?reimbursementStatus=APPROVED&reimbursementId=${reimbursementId}`);

    xhr.send();
    closeModal();
}

function modalDeclineReimbursement() {
    console.log("declineReimbursement");
    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`${xhr.readyState},${xhr.status}`)
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            //Present the data to the user
            document.getElementById("listMessage").innerHTML = `<span class="label label-default label-center">${data.message}</span>`;
            refreshReimbursementsTable();
        }
    };

    let reimbursementId = sessionStorage.getItem("reimbursementID");
    xhr.open("POST", `resolveReimbursement.do?reimbursementStatus=DECLINED&reimbursementId=${reimbursementId}`);

    xhr.send();
    closeModal();
}

function refreshReimbursementsTable()
{
    switch(previousView){
        case 1:
        viewPendingReimbursements();
        break;
        case 2:
        viewResolvedReimbursements();
        break;
        case 3:
        viewEmployeeReimbursements(previouslyViewedEmployeeId);
    }
}

function dateTimeToString(dateTime) {
    if (!dateTime) {
        return "";
    } else {
        return `${dateTime.monthValue}-${dateTime.dayOfMonth}-${dateTime.year} ${dateTime.hour}:${dateTime.minute}`;
    }
}