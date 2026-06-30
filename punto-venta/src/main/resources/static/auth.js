/**
 * Utilidades de autenticación JWT para el frontend de Punto de Venta.
 */
const Auth = {
    TOKEN_KEY: 'pv_token',
    USER_KEY: 'pv_username',
    ROLE_KEY: 'pv_role',

    getToken() {
        return localStorage.getItem(this.TOKEN_KEY);
    },

    getUsername() {
        return localStorage.getItem(this.USER_KEY);
    },

    getRole() {
        return localStorage.getItem(this.ROLE_KEY);
    },

    saveSession({ token, username, role }) {
        localStorage.setItem(this.TOKEN_KEY, token);
        localStorage.setItem(this.USER_KEY, username);
        localStorage.setItem(this.ROLE_KEY, role);
    },

    clearSession() {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        localStorage.removeItem(this.ROLE_KEY);
    },

    isLoggedIn() {
        return !!this.getToken();
    },

    requireAuth() {
        if (!this.isLoggedIn()) {
            const page = window.location.pathname.split('/').pop() || 'index.html';
            window.location.href = `login.html?redirect=${encodeURIComponent(page)}`;
            return false;
        }
        return true;
    },

    authHeaders(extraHeaders = {}) {
        const headers = { ...extraHeaders };
        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        return headers;
    },

    async authFetch(url, options = {}) {
        const headers = this.authHeaders(
            options.headers instanceof Headers
                ? Object.fromEntries(options.headers.entries())
                : (options.headers || {})
        );

        const response = await fetch(url, { ...options, headers });

        if (response.status === 401) {
            this.clearSession();
            const page = window.location.pathname.split('/').pop() || 'index.html';
            window.location.href = `login.html?redirect=${encodeURIComponent(page)}&expired=1`;
        } else if (response.status === 403) {
            alert("No tiene permisos de edición");
            throw new Error("No tiene permisos de edición");
        }

        return response;
    },

    logout() {
        this.clearSession();
        window.location.href = 'login.html';
    },

    renderUserBar(containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        const username = this.getUsername() || 'usuario';
        const role = this.getRole() || '';

        container.innerHTML = `
            <span class="auth-user">${username} <small>(${role})</small></span>
            <button type="button" class="auth-logout" onclick="Auth.logout()">Cerrar sesión</button>
        `;
    }
};
