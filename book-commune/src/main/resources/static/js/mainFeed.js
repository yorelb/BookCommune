// document.addEventListener('DOMContentLoaded', () => {
//     const userJson = localStorage.getItem('loggedInUser');
//     if (!userJson) {
//         window.location.href = '../startSession/login.html';
//         return;
//     }
//     const user = JSON.parse(userJson);
//     const locationString = [user.city, user.country].filter(Boolean).join(', ');
//     const userCity = locationString || 'Unknown';
//
//     //user's city
//     document.getElementById('userCityDisplay').textContent = userCity;
//
//     // TODO: Populayte db with real/mock data
//     // TODO: Two cities with the same name?
//     // eventually turns the condition into stars (actual)
//     const dummyBooks = [
//         { id: 1, title: "The Hobbit", author: "J.R.R. Tolkien", condition: "TWOSTAR", owner: "@FairlyOdd", city: "Sheffield", img: "https://via.placeholder.com/300x450/5b8c85/ffffff?text=The+Hobbit" },
//         { id: 2, title: "1984", author: "George Orwell", condition: "FIVESTAR", owner: "@BookWorm99", city: "Manchester", img: "https://via.placeholder.com/300x450/7da59c/ffffff?text=1984" },
//         { id: 3, title: "Dune", author: "Frank Herbert", condition: "THREESTAR", owner: "@SciFiGuy", city: "Sheffield", img: "https://via.placeholder.com/300x450/a3c9b0/ffffff?text=Dune" },
//         { id: 4, title: "Pride and Prejudice", author: "Jane Austen", condition: "FOURSTAR", owner: "@ClassicReader", city: "Manchester", img: "https://via.placeholder.com/300x450/5b8c85/ffffff?text=Pride+%26+Prejudice" },
//         { id: 5, title: "The Catcher in the Rye", author: "J.D. Salinger", condition: "ONESTAR", owner: "@LitLover", city: "Manchester", img: "https://via.placeholder.com/300x450/7da59c/ffffff?text=Catcher+in+the+Rye" },
//         { id: 6, title: "Brave New World", author: "Aldous Huxley", condition: "FIVESTAR", owner: "@LondonReader", city: "London", img: "https://via.placeholder.com/300x450/5b8c85/ffffff?text=Brave+New+World" }
//     ];
//
//     // Filter
//     //TODO: Hacky fix - books dont have a city field,, they're based on the user's city - can be done with a repo query
//     const availableBooks = dummyBooks.filter(book =>
//         book.city.toLowerCase() === user.city.toLowerCase()
//     );
//
//     const feedContainer = document.getElementById('booksFeed');
//     if (availableBooks.length === 0) {
//         feedContainer.innerHTML = `
//                 <div class="empty-state">
//                     <h3>No books found in ${userCity}</h3>
//                     <p>Be the first to list a book in your area!</p>
//                 </div>
//             `;
//         return;
//     }
//
//     availableBooks.forEach(book => {
//         const card = document.createElement('div');
//         card.className = 'book-card';
//         card.innerHTML = `
//                 <img src="${book.img}" alt="${book.title} cover" class="book-cover">
//                 <h3 class="book-title" title="${book.title}">${book.title}</h3>
//                 <p class="book-author">${book.author}</p>
//
//                 <div class="book-meta">
//                     <span class="meta-tag">${book.condition}</span>
//                     <span class="book-owner">
//                         <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
//                         ${book.owner}
//                     </span>
//                 </div>
//
//                 <button class="borrow-btn" onclick="requestBorrow(${book.id})">Request to Borrow</button>
//             `;
//
//         feedContainer.appendChild(card);
//     });
// });
//
// // Dummy function
// function requestBorrow(bookId) {
//     alert("Borrow request sent for Book ID: " + bookId + "!\n\n(This will be connected to the backend later)");
// }

//Non dummy code

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
    fetch('/api/books/allBooks')
        .then(response => {
            if (!response.ok) throw new Error("Failed to load books");
            return response.json();
        })
        .then(books => {
            // Filter
            const availableBooks = books.filter(book => {
                const bookCity = book.owner?.city || "Unknown";
                return bookCity.toLowerCase() === user.city.toLowerCase();
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
                // defaults
                const title = book.title || "Untitled Book";
                const author = book.author || "Unknown Author";
                const condition = book.condition || "UNSPECIFIED";
                const owner = book.owner || book.ownerUsername || "@Anonymous";
                const imgUrl = book.img || book.coverUrl || "https://via.placeholder.com/300x450/5b8c85/ffffff?text=No+Cover+Available";

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
        
                    <button class="borrow-btn" onclick="requestBorrow(${book.id}, this)">Request to Borrow</button>                `;

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



// Dummy function
async function requestBorrow(bookId, buttonElement) {
    const userJson = localStorage.getItem('loggedInUser');
    if (!userJson) return;
    const user = JSON.parse(userJson);

    try {
        const response = await fetch('/api/borrow/request', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                bookId: bookId,
                requesterId: user.id
            })
        });

        if (response.ok) {
            buttonElement.textContent = "Requested";
            buttonElement.disabled = true;
            buttonElement.style.backgroundColor = "#ccc";
        } else {
            alert("Failed to send request. Please try again.");
        }
    } catch (error) {
        console.error("Error requesting book:", error);
    }
}