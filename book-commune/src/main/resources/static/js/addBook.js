document.addEventListener('DOMContentLoaded', () => {
    const userJson = localStorage.getItem('loggedInUser');
    if (!userJson) {
        window.location.href = '../startSession/login.html';
        return;
    }
    const user = JSON.parse(userJson);

    const form = document.getElementById('addBookForm');

    form.addEventListener('submit', (e) => {
        e.preventDefault();

        const bookData = {
            title: document.getElementById('title').value,
            author: document.getElementById('author').value,
            isbn: document.getElementById('isbn').value,
            condition: document.getElementById('condition').value,
            bookImageUrl: document.getElementById('imgUrl').value,
            description: document.getElementById('description').value,
            owner: {id: user.id}
        };

        fetch('/api/books', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(bookData)
        })
            .then(async response => {
                if (!response.ok) {
                    const errorMessage = await response.text();
                    throw new Error(errorMessage);
                }
                return response.json();
            })
            .then(savedBook => {
                alert("Book successfully added to your library!");
                window.location.href = '/views/userViews/feed/mainFeed.html';
            })
            .catch(error => {
                console.error("Error saving book:", error);
                alert(error.message || "Something went wrong. Please try again.");
            });
    });
});