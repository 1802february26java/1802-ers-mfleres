function submitOnLoad() {
    document.getElementById("requestModalClose").addEventListener("click", closeRequestModal);
    document.getElementById("submitRequest").addEventListener("click",submitReimbursement);
    getReimbursementTypes();
}

function submitReimbursement() {
    let amount = document.getElementById("requestAmount").value;
    if(!amount){
        //Either requestAmount is 0 or not inputted
        return;
    }
    let description = document.getElementById("requestDescription").value;
    let type = document.getElementById("requestType").value;

    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`submitReimbursement ajax: ${xhr.readyState},${xhr.status}`)
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            if(data.message){
                //Error has occured
                let errorMessage = data.message;
                document.getElementById("listMessage").innerHTML = `<span class="label label-danger label-center">${errorMessage}</span>`;
            }else {
                //Present the data to the user
                console.log("submitReimbursement error: no message");
            }
        }
    };

    xhr.open("POST", `submitReimbursement.do?amount=${amount}&description=${description}&type=${type}`);

    xhr.send();
    closeRequestModal();
}

function requestReimbursement() {
    console.log("Displaying reimbursement request options...");
    document.getElementById("requestAmount").value="0.00";
    document.getElementById("requestDescription").value="";
    let modalElement = document.getElementById("requestModal");
    modalElement.style.display = "block";
}

function closeRequestModal() {
    document.getElementById("requestModal").style.display = "none";
}

//Get the reimbursement types then place them into the drop-down in the model
function getReimbursementTypes() {
    console.log("getReimbursementTypes()");

    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`getReimbursementTypes: ${xhr.readyState},${xhr.status}`)
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            if(data.message){
                //Error has occured
                let errorMessage = data.message;
                document.getElementById("listMessage").innerHTML = `<span class="label label-danger label-center">${errorMessage}</span>`;
            }else {
                //Present the data to the user
                insertReimbursementTypes(data);
            }
        }
    };

    xhr.open("GET", `reimbursementTypes.do?fetch=LIST`);

    xhr.send();
}

function insertReimbursementTypes(data){
    let dropDownElement = document.getElementById("requestType");

    data.forEach(reimbursementType => {
        let optionElement = document.createElement("option");
        optionElement.setAttribute("value", reimbursementType.type)
        let typeText = document.createTextNode(reimbursementType.type);
        optionElement.appendChild(typeText);
        dropDownElement.appendChild(optionElement);
    });
}