document.addEventListener('DOMContentLoaded', () => {
    const userJson = localStorage.getItem('loggedInUser');

    if (!userJson) {
        window.location.href = '../startSession/login.html';
        return;
    }

    const user = JSON.parse(userJson);

    document.getElementById('profileName').textContent = user.name || 'Unknown User';
    document.getElementById('profileUsername').textContent = '@' + (user.username || 'user');

    document.getElementById('editProfilePicture').value = user.profileImageUrl || '';
    document.getElementById('editUsername').value = user.username || '';
    document.getElementById('editCity').value = user.city || '';
    document.getElementById('editCountry').value = user.country || '';
    document.getElementById('editPostcode').value = user.postcode || '';
    document.getElementById('editBio').value = user.bio || '';

    document.getElementById('editProfileForm').addEventListener('submit', (e) => {
        e.preventDefault();

        const errorDiv = document.getElementById('errorMessage');
        const submitBtn = document.querySelector('.save-btn');

        errorDiv.style.display = 'none';
        submitBtn.textContent = 'Saving...';
        submitBtn.disabled = true;

        const updatedData = {
            profileImageUrl: document.getElementById('editProfilePicture').value,
            username: document.getElementById('editUsername').value,
            city: document.getElementById('editCity').value,
            country: document.getElementById('editCountry').value,
            postcode: document.getElementById('editPostcode').value,
            bio: document.getElementById('editBio').value
        };

        fetch(`/api/users/${user.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedData)
        })
            .then(async response => {
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || "Failed to update profile");
                }
                return response.json();
            })
            .then(updatedUser => {
                const mergedUser = { ...user, ...updatedUser };
                localStorage.setItem('loggedInUser', JSON.stringify(mergedUser));
                window.location.href = 'profile.html';
            })
            .catch(error => {
                errorDiv.textContent = error.message;
                errorDiv.style.display = 'block';
                submitBtn.textContent = 'Save Changes';
                submitBtn.disabled = false;
            });
    });
});