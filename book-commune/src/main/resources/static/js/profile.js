document.addEventListener('DOMContentLoaded', () => {
    const userJson = localStorage.getItem('loggedInUser');

    if (!userJson) {
        window.location.href = '../../startSession/login.html';
        return;
    }

    const user = JSON.parse(userJson);

    fetch(`/api/users/${user.id}`)
        .then(response => {
            if (!response.ok) throw new Error("Failed to fetch fresh user data");
            return response.json();
        })
        .then(user => {
            document.getElementById('profileName').textContent = user.name || 'Unknown User';
            document.getElementById('profileUsername').textContent = '@' + (user.username || 'user');
            document.getElementById('profileEmail').textContent = user.email || 'No email provided';
            document.getElementById('profileBio').textContent = user.bio || 'This user has not written a bio yet.';
            document.getElementById('profileRole').textContent = user.roleName || 'Member';
            document.getElementById('borrowedCount').textContent = user.borrowedBooks ?? 0;
            document.getElementById('lentCount').textContent = user.lentBooks ?? 0;
            const locationElement = document.getElementById('profileAddress');
            if (locationElement) {
                const locationString = [user.city, user.country].filter(Boolean).join(', ');
                locationElement.textContent = locationString || 'Unknown';
            }


            localStorage.setItem('loggedInUser', JSON.stringify(user));
        })
        .catch(error => {
            console.error("Error loading profile data from server:", error);
        });
});

// Edit Profile Button Click
const editBtn = document.getElementById('editProfileBtn');
if (editBtn) {
    editBtn.addEventListener('click', () => {
        window.location.href = 'editProfile.html';
    });
}