window.onload = () => {
    document.getElementById("loginButton").addEventListener("click", loginEvent);

    document.body.addEventListener("keydown", (e) => {
        if (e.keyCode == 13) {
            loginEvent();
            console.log("ENTER HAS BEEN PRESSED");
        }
    });
}

function loginEvent() {
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;
    document.getElementById("username").disabled = true;
    document.getElementById("password").disabled = true;

    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        //If the request is DONE (4), and everything is OK
        if (xhr.readyState === 4 && xhr.status === 200) {
            console.log(xhr.responseText);
            let data = JSON.parse(xhr.responseText);
            //console.log(data);

            //Call login response processing
            login(data, username);
            document.getElementById("username").disabled = false;
            document.getElementById("password").disabled = false;
        }
    };

    //Doing a HTTP to a specific endpoint
    xhr.open("POST", `login.do?username=${username}&password=${password}`);

    //Sending our request
    xhr.send();
}

function login(data, username) {
    console.log("login(data,username)");
    //If message is a member of the JSON it was AUTHENTICATION FAILED
    if (data.message) {
        document.getElementById("loginMessage").innerHTML = `<span class="label label-danger label-center">${data.message}</span>`;
    } else {
        //Using session storage of JavaScript
        sessionStorage.setItem("employeeUsername", data.username);
        sessionStorage.setItem("employeeFirstName", data.firstName);
        sessionStorage.setItem("employeeLastName", data.lastName);
        sessionStorage.setItem("employeeEmail", data.email);
        window.location.replace("home.do");
    }
}