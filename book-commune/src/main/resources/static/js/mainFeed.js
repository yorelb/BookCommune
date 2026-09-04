document.addEventListener('DOMContentLoaded', () => {
    const userJson = localStorage.getItem('loggedInUser');
    if (!userJson) {
        window.location.href = '../startSession/login.html';
        return;
    }
    const user = JSON.parse(userJson);
    const locationString = [user.city, user.country].filter(Boolean).join(', ');
    const userCity = locationString || 'Unknown';

    // user's city
    document.getElementById('userCityDisplay').textContent = userCity;
    const feedContainer = document.getElementById('booksFeed');

    // get books
    Promise.all([
        fetch('/api/books/allBooks').then(response => {
            if (!response.ok) throw new Error("Failed to load books");
            return response.json();
        }),
        fetch(`/api/borrow/outgoing/${user.id}`).then(response => response.ok ? response.json() : [])
    ])
        .then(([books, outgoingRequests]) => {
            const requestedBookIds = new Set(
                outgoingRequests
                    .filter(req => req.status === 'PENDING' || req.status === 'ACTIVE')
                    .map(req => req.book.id)
            );

            // Filter
            const availableBooks = books.filter(book => {
                const bookCity = book.owner?.city || "Unknown";
                const inSameCity = bookCity.toLowerCase() === user.city.toLowerCase();
                const isNotOwnBook = book.owner?.id !== user.id;
                return inSameCity && isNotOwnBook;
            });

            if (availableBooks.length === 0) {
                feedContainer.innerHTML = `
                <div class="empty-state">
                    <h3>No books found in ${userCity}</h3>
                    <p>Be the first to list a book in your area!</p>
                </div>
            `;
                return;
            }

            availableBooks.forEach(book => {
                const title = book.title || "Untitled Book";
                const author = book.author || "Unknown Author";
                const condition = book.condition || "UNSPECIFIED";
                const owner = book.owner?.username ? `@${book.owner.username}` : "@Anonymous";
                const imgUrl = book.bookImageUrl || book.img || "https://via.placeholder.com/300x450/5b8c85/ffffff?text=No+Cover+Available";
                //const imgUrl = book.img || book.coverUrl || "https://via.placeholder.com/300x450/5b8c85/ffffff?text=No+Cover+Available";

                const isAlreadyRequested = requestedBookIds.has(book.id);
                const buttonHtml = isAlreadyRequested
                    ? `<button class="borrow-btn" disabled style="background-color: #88BDA4;">Requested</button>`
                    : `<button class="borrow-btn" onclick="requestBorrow(${book.id}, this)">Request to Borrow</button>`;

                const card = document.createElement('div');
                card.className = 'book-card';
                card.innerHTML = `
                <img src="${imgUrl}" alt="${title} cover" class="book-cover">
                <h3 class="book-title" title="${title}">${title}</h3>
                <p class="book-author">${author}</p>

                <div class="book-meta">
                    <span class="meta-tag">${condition}</span>
                    <span class="book-owner">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                        ${owner}
                    </span>
                </div>
    
                ${buttonHtml}                `;

                feedContainer.appendChild(card);
            });
        })
        .catch(error => {
            console.error("Error fetching feed:", error);
            feedContainer.innerHTML = `
            <div class="empty-state">
                <h3>Oops! Could not load the library.</h3>
                <p>Our servers might be taking a quick nap. Try refreshing the page.</p>
            </div>
        `;
        });
});

async function requestBorrow(bookId, buttonElement) {
    const userJson = localStorage.getItem('loggedInUser');
    if (!userJson) return;
    const user = JSON.parse(userJson);

    try {
        const response = await fetch(`/api/borrow/request?bookId=${bookId}&borrowerId=${user.id}`, {
            method: 'POST'
        });

        if (response.ok) {
            buttonElement.textContent = "Requested";
            buttonElement.disabled = true;
            buttonElement.style.backgroundColor = "#88BDA4";
        } else {
            const errorText = await response.text();
            alert(errorText || "Failed to send request. Please try again."); //TODO: Dont list own books
        }
    } catch (error) {
        console.error("Error requesting book:", error);
    }
}