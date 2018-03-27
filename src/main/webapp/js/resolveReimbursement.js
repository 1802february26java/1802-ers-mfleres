window.onload = () => {
    console.log("resolveOnLoad");
    viewOnLoad();
    document.getElementById("approveButton").addEventListener("click", approveReimbursement);
    document.getElementById("declineButton").addEventListener("click", declineReimbursement);

}

function approveReimbursement() {
    console.log("approveReimbursement");
    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`${xhr.readyState},${xhr.status}`);
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            //Present the data to the user
            document.getElementById("listMessage").innerHTML = `<span class="label label-info label-center">${data.message}</span>`;
        }
    };

    let reimbursementId = document.getElementById("reimbursementId").value;
    xhr.open("POST", `resolveReimbursement.do?reimbursementStatus=APPROVED&reimbursementId=${reimbursementId}`);

    xhr.send();
}

function declineReimbursement() {
    console.log("declineReimbursement");
    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`${xhr.readyState},${xhr.status}`)
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            //Present the data to the user
            document.getElementById("listMessage").innerHTML = `<span class="label label-info label-center">${data.message}</span>`;
        }
    };

    let reimbursementId = document.getElementById("reimbursementId").value;
    xhr.open("POST", `resolveReimbursement.do?reimbursementStatus=DECLINED&reimbursementId=${reimbursementId}`);

    xhr.send();
}