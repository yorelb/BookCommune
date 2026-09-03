document.addEventListener('DOMContentLoaded', () => {
    const userJson = localStorage.getItem('loggedInUser');
    if (!userJson) {
        window.location.href = '../startSession/login.html';
        return;
    }
    const user = JSON.parse(userJson);
    fetchOutgoingRequests(user.id);
    fetchIncomingRequests(user.id);
});

async function fetchOutgoingRequests(userId) {
    const container = document.getElementById('outgoingRequestsContainer');
    try {
        const response = await fetch(`/api/borrow/outgoing/${userId}`);
        if (!response.ok) throw new Error("Failed to load outgoing requests");

        const requests = await response.json();
        if (requests.length === 0) {
            container.innerHTML = `<p class="request-label">You haven't requested any books yet.</p>`;
            return;
        }

        container.innerHTML = requests.map(req => {
            const owner = req.book.owner?.username || "Anonymous";
            return `
                <div class="request-item">
                    <p class="request-label">Requested from @${owner}</p>
                    <p class="request-value">${req.book.title}</p>
                    <p class="request-label" style="color: var(--first); font-weight: 700;">Status: ${req.status}</p>
                </div>
            `;
        }).join('');
    } catch (error) {
        container.innerHTML = `<p class="request-label" style="color: red;">Error loading requests.</p>`;
    }
}

async function fetchIncomingRequests(userId) {
    const container = document.getElementById('incomingRequestsContainer');
    try {
        const response = await fetch(`/api/borrow/incoming/${userId}`);
        if (!response.ok) throw new Error("Failed to load incoming requests");
        const requests = await response.json();
        const pendingRequests = requests.filter(req => req.status === 'PENDING');

        if (pendingRequests.length === 0) {
            container.innerHTML = `<p class="request-label">No pending requests for your books.</p>`;
            return;
        }

        container.innerHTML = pendingRequests.map(req => {
            const requester = req.borrower?.username || "Anonymous";
            return `
                <div class="request-item" id="request-${req.id}">
                    <p class="request-label">Requested by @${requester}</p>
                    <p class="request-value">${req.book.title}</p>
                    <div class="action-buttons">
                        <button class="btn-approve" onclick="handleRequest(${req.id}, 'approve')">Approve</button>
                        <button class="btn-deny" onclick="handleRequest(${req.id}, 'deny')">Deny</button>
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        container.innerHTML = `<p class="request-label" style="color: red;">Error loading requests.</p>`;
    }
}

async function handleRequest(recordId, action) {
    try {
        const response = await fetch(`/api/borrow/${recordId}/${action}`, {
            method: 'POST'
        });

        if (response.ok) {
            document.getElementById(`request-${recordId}`).remove();
        } else {
            const error = await response.text();
            alert(`Failed to ${action} request: ${error}`);
        }
    } catch (error) {
        console.error(`Error processing ${action}:`, error);
    }
}