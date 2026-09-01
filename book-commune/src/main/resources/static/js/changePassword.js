document.addEventListener('DOMContentLoaded', () => {
    const userJson = localStorage.getItem('loggedInUser');
    if (!userJson) {
        window.location.href = '../startSession/login.html';
        return;
    }
    const user = JSON.parse(userJson);

    document.getElementById('changePasswordForm').addEventListener('submit', (e) => {
        e.preventDefault();

        const errorDiv = document.getElementById('errorMessage');
        const successDiv = document.getElementById('successMessage');
        const submitBtn = document.querySelector('.save-btn');

        const currentPassword = document.getElementById('currentPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmNewPassword = document.getElementById('confirmNewPassword').value;

        if (newPassword !== confirmNewPassword) {
            errorDiv.textContent = "Your new passwords do not match.";
            errorDiv.style.display = 'block';
            return;
        }

        errorDiv.style.display = 'none';
        successDiv.style.display = 'none';
        submitBtn.textContent = 'Verifying...';
        submitBtn.disabled = true;

        const requestData = {
            currentPassword: currentPassword,
            newPassword: newPassword
        };

        fetch(`/api/users/${user.id}/password`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestData)
        })
            .then(async response => {
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || "Failed to update password");
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
                submitBtn.textContent = 'Update Password';
                submitBtn.disabled = false;
            });
    });
});