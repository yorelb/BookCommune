document.addEventListener('DOMContentLoaded', () => {
    const userJson = localStorage.getItem('loggedInUser');
    if (!userJson) {
        window.location.href = '../../startSession/login.html';
        return;
    }

    const navItems = document.querySelectorAll('.nav-item');
    const sections = document.querySelectorAll('.settings-content');

    navItems.forEach(item => {
        item.addEventListener('click', () => {
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');
            sections.forEach(sec => sec.classList.add('hidden-section'));
            const targetId = item.getAttribute('data-target');

            if (targetId) {
                document.getElementById(targetId).classList.remove('hidden-section');
            }
        });
    });
});

document.addEventListener('DOMContentLoaded', () => {
    const darkModeToggle = document.getElementById('darkModeToggle');
    if (localStorage.getItem('theme') === 'dark') {
        darkModeToggle.checked = true;
    }

    darkModeToggle.addEventListener('change', (e) => {
        if (e.target.checked) {
            document.body.classList.add('dark-mode');
            localStorage.setItem('theme', 'dark');
        } else {
            document.body.classList.remove('dark-mode');
            localStorage.setItem('theme', 'light');
        }
    });
});