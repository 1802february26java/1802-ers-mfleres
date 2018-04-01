function updateEmployeeOnLoad(){
    document.getElementById("profileFirstNameEdit").addEventListener("click",updateEmployeeFirstName);
    document.getElementById("profileLastNameEdit").addEventListener("click",updateEmployeeLastName);
    document.getElementById("profileEmailEdit").addEventListener("click",updateEmployeeEmail);
}

function updateEmployeeFirstName(){
    let newFirstName = document.getElementById("profileFirstName").value;

    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`update profile ajax: ${xhr.readyState},${xhr.status}`)
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            if(data.message){
                let message = data.message;
                
                if(message === "SUCCESS"){
                    document.getElementById("profileModalMessage").innerHTML = `<span class="label label-success label-center">${message}</span>`;
                    sessionStorage.setItem("employeeFirstName",newFirstName);
                    document.getElementById("profileFirstName").setAttribute("placeholder",sessionStorage.getItem("employeeFirstName"));
                    document.getElementById("profileFirstName").value="";
                } else {
                    document.getElementById("profileModalMessage").innerHTML = `<span class="label label-danger label-center">${message}</span>`;
                }
            }else {
                //Present the data to the user
                console.log("updateEmployee error: no message");
            }
        }
    };

    
    if(newFirstName){
        xhr.open("POST", `updateEmployee.do?firstName=${newFirstName}`);
        xhr.send();
    }
}

function updateEmployeeLastName() {
    let newLastName = document.getElementById("profileLastName").value;

    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`update profile ajax: ${xhr.readyState},${xhr.status}`)
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            if(data.message){
                let message = data.message;
                
                if(message === "SUCCESS"){
                    document.getElementById("profileModalMessage").innerHTML = `<span class="label label-success label-center">${message}</span>`;
                    sessionStorage.setItem("employeeLastName",newLastName);
                    document.getElementById("profileLastName").setAttribute("placeholder",sessionStorage.getItem("employeeLastName"));
                    document.getElementById("lastName").value="";
                } else {
                    document.getElementById("profileModalMessage").innerHTML = `<span class="label label-danger label-center">${message}</span>`;
                }
            }else {
                //Present the data to the user
                console.log("updateEmployee error: no message");
            }
        }
    };

    
    if(newLastName){
        xhr.open("POST", `updateEmployee.do?employeeLastName=${newLastName}`);
        xhr.send();
    }
}

function updateEmployeeEmail() {
    let newEmail = document.getElementById("profileEmail").value;

    //AJAX
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        console.log(`update profile ajax: ${xhr.readyState},${xhr.status}`)
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            console.log(data);

            if(data.message){
                let message = data.message;
                
                if(message === "SUCCESS"){
                    document.getElementById("profileModalMessage").innerHTML = `<span class="label label-success label-center">${message}</span>`;
                    sessionStorage.setItem("employeeEmail",newEmail);
                    document.getElementById("profileEmail").setAttribute("placeholder",sessionStorage.getItem("employeeEmail"));
                    document.getElementById("profileEmail").value="";
                } else {
                    document.getElementById("profileModalMessage").innerHTML = `<span class="label label-danger label-center">${message}</span>`;
                }
            }else {
                //Present the data to the user
                console.log("updateEmployee error: no message");
            }
        }
    };
    
    if(newEmail){
        xhr.open("POST", `updateEmployee.do?email=${newEmail}`);
        xhr.send();
    }
}