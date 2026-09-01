document.getElementById('signUpForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const alertBox = document.getElementById('signUpAlert');
    alertBox.style.display = 'none';

    const firstName = document.getElementById('firstName').value.trim();
    const lastName = document.getElementById('lastName').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        alertBox.style.color = '#88BDA4';
        alertBox.textContent = "Passwords do not match!";
        alertBox.style.display = 'block';
        return;
    }

    const userPayload = {
        forename: firstName.charAt(0).toUpperCase() + firstName.slice(1).trim(),
        surname: lastName.charAt(0).toUpperCase() + lastName.slice(1).trim(),
        email: email,
        username: email,
        password: password
    };

    fetch('http://localhost:8080/api/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userPayload)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text().then(text => { throw new Error(text) });
            }
        })
        .then(data => {
            localStorage.setItem('newUser', JSON.stringify(data));
            alertBox.style.color = '#88BDA4';
            alertBox.textContent = "Account created successfully! Taking you to profile setup...";
            alertBox.style.display = 'block';

            setTimeout(() => {
                window.location.href = 'completeProfile.html';
            }, 2000);
        })
        .catch(error => {
            console.error('Sign up error:', error);
            alertBox.style.color = 'red';
            alertBox.textContent = error.message || "Failed to create account. Please try again.";
            alertBox.style.display = 'block';
        });
});