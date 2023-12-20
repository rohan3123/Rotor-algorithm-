$(document).ready(function () {
    $("#checkDatabaseConnectionBtn").on("click", checkDatabaseConnection);
    $("#createDatabaseBtn").on("click", createDatabase);
    $("#addUserBtn").on("click", addUser);
    $("#getAllUsersBtn").on("click", getAllUsers);
    $("#loginBtn").on("click", loginUser);
    $("#registerBtn").on("click", registerUser);
});

function checkDatabaseConnection() {
    fetch('/Rotor/api/orchestrator/checkDatabaseConnection')
        .then(response => response.json())
        .then(data => {
            $('#databaseConnectionDisplay').text('Database Connection Status: ' + data.status);
        })
        .catch(() => {
            $('#databaseConnectionDisplay').text('Error checking database connection');
        });
}

function createDatabase() {
    
    fetch('/Rotor/api/orchestrator/createDatabase')
        .then(response => response.json())
        .then(data => {
            $('#databaseConnectionDisplay').text(data.status);
        })
        .catch(() => {
            $('#databaseConnectionDisplay').text('Error creating database');
        });
}

function addUser() {
    fetch('/Rotor/api/orchestrator/addUser')
        .then(response => response.json())
        .then(data => {
            $('#databaseConnectionDisplay').text(data.status);
        })
        .catch(() => {
            $('#databaseConnectionDisplay').text('Error adding user');
        });
}

function getAllUsers() {
    fetch('/Rotor/api/orchestrator/getAllUsers')
        .then(response => response.json())
        .then(data => {
            var userListHtml = '<h3>All Users</h3><ul>';
            data.forEach(user => {
                userListHtml += `<li>ID: ${user.id} Username: ${user.username} Password: ${user.password}</li>`;
            });
            userListHtml += '</ul>';
            $('#userList').html(userListHtml);
        })
        .catch(() => {
            $('#userList').text('Error getting users');
        });
}

function registerUser() {
    var username = $('#regUsername').val();
    var password = $('#regPassword').val();

    fetch('/Rotor/api/orchestrator/registerUser', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `username=${username}&password=${password}`
    })
        .then(response => response.json())
        .then(data => {
            $('#registrationStatus').text(data.status);
        })
        .catch(() => {
            $('#registrationStatus').text('Error registering user');
        });
}

function loginUser() {
    var username = $("#loginUsername").val();
    var password = $("#loginPassword").val();

    fetch('/Rotor/api/orchestrator/loginUser', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `username=${username}&password=${password}`
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                window.location.href = 'mainmenu.html';
            } else {
                $('#loginStatus').text('Login failed. ' + data.status);
            }
        })
        .catch(() => {
            $('#loginStatus').text('Error logging in');
        });
}


