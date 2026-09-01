document.addEventListener('DOMContentLoaded', () => {
    const userJson = localStorage.getItem('loggedInUser');
    if (!userJson) {
        window.location.href = '../startSession/login.html';
        return;
    }
    const user = JSON.parse(userJson);

    document.getElementById('changeEmailForm').addEventListener('submit', (e) => {
        e.preventDefault();

        const errorDiv = document.getElementById('errorMessage');
        const successDiv = document.getElementById('successMessage');
        const submitBtn = document.querySelector('.save-btn');

        errorDiv.style.display = 'none';
        successDiv.style.display = 'none';
        submitBtn.textContent = 'Verifying...';
        submitBtn.disabled = true;

        const requestData = {
            currentEmail: document.getElementById('currentEmail').value,
            newEmail: document.getElementById('newEmail').value,
            password: document.getElementById('confirmPassword').value
        };

        fetch(`/api/users/${user.id}/email`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestData)
        })
            .then(async response => {
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || "Failed to update email");
                }
                return response.json();
            })
            .then(updatedUser => {
                const mergedUser = { ...user, ...updatedUser };
                localStorage.setItem('loggedInUser', JSON.stringify(mergedUser));

                successDiv.style.display = 'block';
                setTimeout(() => { window.location.href = 'profile.html'; }, 1500);
            })
            .catch(error => {
                errorDiv.textContent = error.message;
                errorDiv.style.display = 'block';
                submitBtn.textContent = 'Update Email';
                submitBtn.disabled = false;
            });
    });
});