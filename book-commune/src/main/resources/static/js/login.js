document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const usernameInput = document.getElementById('username').value;
    const passwordInput = document.getElementById('password').value;
    const alertBox = document.getElementById('loginAlert');

    alertBox.style.display = 'none';

    const loginData = {
        username: usernameInput,
        password: passwordInput
    };

    fetch('http://localhost:8080/api/users/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginData)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text().then(text => { throw new Error(text) });
            }
        })
        .then(user => {
            console.log("Logged in user:", user);

            localStorage.setItem("loggedInUser", JSON.stringify(user));
            window.location.href = '../userViews/userProfile/profile.html';
        })
        .catch(error => {
            console.error('Login error:', error);
            alertBox.textContent = error.message || "Login failed. Please try again.";
            alertBox.style.display = 'block';
        });
});