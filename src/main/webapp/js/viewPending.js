window.onload = () => {
    document.getElementById("viewPending").addEventListener("click", viewPendingReimbursements);
}

function viewPendingReimbursements(){
    console.log("viewPending");
    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () =>{
        console.log(`${xhr.readyState},${xhr.status}`)
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            //Present the data to the user
            presentReimbursements(data);
        }
    };
    
    xhr.open("GET",`viewPending.do?fetch=LIST`);

    xhr.send();
}

function presentReimbursements(data){
    console.log("presentReimbursement");
    if(data.message) {
        //Something went wrong
        var errorMessage = data.message;
        document.getElementById("listMessage").innerHTML = `<span class="label label-danger label-center">${errorMessage}</span>`;
    } else {
        //Display table of all reimbursements
        let reimbursementTable = document.getElementById("table");

        //Setup table with headers
        reimbursementTable.innerHTML = `<thread class='thread-light'><tr>
        <th>ID</th>
        <th>Requested</th>
        <th>Resolved</th>
        <th>Amount</th>
        <th>Description</th>
        <th>Requester</th>
        <th>Approver</th>
        <th>Status</th>
        <th>Type</th>
        </tr></thread>`;

        data.forEach((reimbursement) => {
            console.log("inserting a reimbursement");
            let reimbursementRow = document.createElement("tr");
            
            //ID
            createNodeOnTableRow(reimbursementRow,`${reimbursement.id}`);

            //Requested Time
            createNodeOnTableRow(reimbursementRow,`${dateTimeToString(reimbursement.requested)}`);

            //Resolved Time
            let timeResolved = "N/A";
            //If not null, show the value
            if(reimbursement.resolved){
                timeResolved = dateTimeToString(reimbursement.resolved);
            }
            createNodeOnTableRow(reimbursementRow,timeResolved);

            //Amount
            createNodeOnTableRow(reimbursementRow,`${reimbursement.amount}`)

            let description = "N/A";
            if(reimbursement.description){
                description = reimbursement.description;
            }
            createNodeOnTableRow(reimbursementRow,description);
            
            //Requester
            createNodeOnTableRow(reimbursementRow,`${reimbursement.requester.username}`);

            //Skipping the receipt blob for now...

            //Approver
            let approver = "N/A";
            if(reimbursement.approver){
                approver = reimbursement.approver.username;
            }
            createNodeOnTableRow(reimbursementRow,approver);
            
            //Status
            createNodeOnTableRow(reimbursementRow,`${reimbursement.status.status}`);

            //Type
            createNodeOnTableRow(reimbursementRow,`${reimbursement.type.type}`);
            
            console.log(reimbursementRow.innerHTML);
            reimbursementTable.appendChild(reimbursementRow);
        });
    }
}

function createNodeOnTableRow(rowElement ,dataText){
    let reimbursementDataNode = document.createElement("td");
    reimbursementDataNodeText = document.createTextNode(dataText); 
    console.log("rowElement:" +rowElement.innerHTML);
    reimbursementDataNode.appendChild(reimbursementDataNodeText);
    //reimbursementDataNode.setAttribute("align","center");
    //reimbursementDataNode.className = "table-node"

    rowElement.appendChild(reimbursementDataNode);
}

function dateTimeToString (dateTime){
    if(!dateTime){
        return "";
    } else {
        return `${dateTime.monthValue}-${dateTime.dayOfMonth}-${dateTime.year} ${dateTime.hour}:${dateTime.minute}`;
    }
}