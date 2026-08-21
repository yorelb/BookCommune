// Get the navbar
fetch('/views/components/navbar.html')
    .then(response => {
        if (!response.ok) throw new Error("Navbar not found!");
        return response.text();
    })
    .then(html => {
        document.getElementById('navbar-placeholder').innerHTML = html;

        const currentUrl = window.location.href;
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.nav-link');

        navLinks.forEach(link => {
            const activeKeyword = link.getAttribute('data-active');
            if (activeKeyword && currentPath.toLowerCase().includes(activeKeyword.toLowerCase())) {
                link.classList.add('active-box');
            }
        });
    })
    .catch(error => console.error('Error loading navbar:', error));