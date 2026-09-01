document.addEventListener('DOMContentLoaded', () => {
    const newUserJson = localStorage.getItem('newUser');
    if (!newUserJson) {
        window.location.href = 'login.html';
        return;
    }

    const newUser = JSON.parse(newUserJson);

    document.getElementById('onboardingForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const alertBox = document.getElementById('onboardingAlert');
        alertBox.style.display = 'none';

        const username = document.getElementById('username').value.trim();
        const city = document.getElementById('city').value.trim();
        const country = document.getElementById('country').value.trim();
        const postcode = document.getElementById('postcode').value.trim();

        const bio = document.getElementById('bio').value.trim();

        const updatePayload = {
            username: username,
            city: city,
            country: country,
            postcode: postcode,
            bio: bio
        };

        fetch(`http://localhost:8080/api/users/${newUser.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatePayload)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(text => { throw new Error(text) });
                }
            })
            .then(updatedUser => {
                alertBox.style.color = '#88BDA4';
                alertBox.textContent = "Profile complete! Redirecting to login...";
                alertBox.style.display = 'block';

                localStorage.removeItem('newUser');
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 1500);
            })
            .catch(error => {
                console.error('Onboarding error:', error);
                alertBox.style.color = '#88BDA4' ;
                alertBox.textContent = error.message || "Failed to update details. Please try again.";
                alertBox.style.display = 'block';
            });
    });
});