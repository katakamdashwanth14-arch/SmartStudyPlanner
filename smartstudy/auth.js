document.addEventListener('DOMContentLoaded', () => {

    // Toggle Elements
    const signUpButton = document.getElementById('signUp');
    const signInButton = document.getElementById('signIn');
    const container = document.getElementById('auth-container');

    // Mobile Toggle Elements
    const mobileToSignup = document.getElementById('mobile-to-signup');
    const mobileToLogin = document.getElementById('mobile-to-login');

    // Auth Forms
    const loginForm = document.getElementById('login-form');
    const signupForm = document.getElementById('signup-form');

    // Auth Errors
    const authError = document.getElementById('auth-error');
    const signupError = document.getElementById('signup-error');

    // Auto-redirect if already logged in
    if (localStorage.getItem('isLoggedIn') === 'true') {
        window.location.replace('dashboard.html');
        return;
    }

    // Pre-fill remembered username
    const savedName = localStorage.getItem('username');
    if (savedName && localStorage.getItem('rememberToken') === 'true') {
        const loginNameE = document.getElementById('login-name');
        if (loginNameE) loginNameE.value = savedName;
        const rememberCE = document.getElementById('remember-me');
        if (rememberCE) rememberCE.checked = true;
    }

    // Toggle Sliding Panels for Desktop
    if (signUpButton && signInButton && container) {
        signUpButton.addEventListener('click', () => {
            container.classList.add("right-panel-active");
            document.querySelectorAll('.sign-up-container input').forEach(el => el.value = '');
        });

        signInButton.addEventListener('click', () => {
            container.classList.remove("right-panel-active");
            document.querySelectorAll('.sign-in-container input[type="password"], .sign-in-container input[type="email"]').forEach(el => el.value = '');
        });
    }

    // Toggle Panels for Mobile Fallback
    if (mobileToSignup && mobileToLogin && container) {
        mobileToSignup.addEventListener('click', () => {
            container.classList.add("right-panel-active");
        });

        mobileToLogin.addEventListener('click', () => {
            container.classList.remove("right-panel-active");
        });
    }

    // Handle Login
    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const name = document.getElementById('login-name').value;
            const email = document.getElementById('login-email').value;
            const pass = document.getElementById('login-password').value;
            const rememberMe = document.getElementById('remember-me').checked;

            if (name.length < 4 || pass.length < 6 || !email) {
                authError.textContent = "Please provide Valid Name, Email ID, and Password (6+ chars).";
                authError.classList.remove('hidden');

                // Shake animation on error
                loginForm.style.animation = 'none';
                void loginForm.offsetWidth;
                loginForm.style.animation = 'bounceIn 0.3s';
                return;
            }

            authError.classList.add('hidden');

            if (name && pass && email) {
                if (rememberMe) {
                    localStorage.setItem('rememberToken', 'true');
                }
                localStorage.setItem('isLoggedIn', 'true');
                localStorage.setItem('username', name);
                window.location.href = 'dashboard.html';
            }
        });
    }

    // Handle Signup
    if (signupForm) {
        signupForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const name = document.getElementById('signup-name').value;
            const email = document.getElementById('signup-email').value;
            const pass = document.getElementById('signup-password').value;

            if (name.length < 4 || pass.length < 6 || !email) {
                signupError.textContent = "Please fill out Name (4+), Email ID, and Password (6+).";
                signupError.classList.remove('hidden');

                signupForm.style.animation = 'none';
                void signupForm.offsetWidth;
                signupForm.style.animation = 'bounceIn 0.3s';
                return;
            }

            // Execute Registration
            localStorage.setItem('isLoggedIn', 'true');
            localStorage.setItem('username', name);
            window.location.href = 'dashboard.html';
        });
    }

    // Handle Mock Google Auth Simulated Backend
    const signupGoogleBtn = document.getElementById('signup-google-btn');
    const loginGoogleBtn = document.getElementById('login-google-btn');
    const googleModal = document.getElementById('google-auth-modal');
    const googleCancelBtn = document.getElementById('google-cancel-btn');
    const googleNextBtn = document.getElementById('google-next-btn');
    const googleEmailInput = document.getElementById('google-email-input');
    const googleAuthError = document.getElementById('google-auth-error');

    const openGoogleModal = (e) => {
        e.preventDefault();
        googleModal.style.display = 'flex';
        googleEmailInput.value = '';
        googleAuthError.style.display = 'none';
        googleEmailInput.focus();
    };

    if (signupGoogleBtn) signupGoogleBtn.addEventListener('click', openGoogleModal);
    if (loginGoogleBtn) loginGoogleBtn.addEventListener('click', openGoogleModal);

    if (googleCancelBtn) {
        googleCancelBtn.addEventListener('click', () => {
            googleModal.style.display = 'none';
        });
    }

    if (googleNextBtn) {
        googleNextBtn.addEventListener('click', () => {
            const email = googleEmailInput.value.trim();
            // Basic regex for email
            if (!email || !/^\S+@\S+\.\S+$/.test(email)) {
                googleAuthError.style.display = 'block';
                return;
            }

            // Extract a display name from the email (everything before @)
            const extractedName = email.split('@')[0];
            const nameToSave = extractedName.charAt(0).toUpperCase() + extractedName.slice(1);

            // Execute Login Backend Mock
            localStorage.setItem('isLoggedIn', 'true');
            localStorage.setItem('username', nameToSave); // Store their Google name
            window.location.href = 'dashboard.html';
        });
    }
});

document.addEventListener('DOMContentLoaded', () => {
    // Auth Theme Toggle Listener appended correctly
    const authCheckbox = document.getElementById('auth-checkbox');
    if (authCheckbox) {
        const sw = localStorage.getItem('theme');
        if (sw === 'dark') {
            document.body.setAttribute('data-theme', 'dark');
            authCheckbox.checked = true;
        }
        authCheckbox.addEventListener('change', (e) => {
            if (e.target.checked) {
                document.body.setAttribute('data-theme', 'dark');
                localStorage.setItem('theme', 'dark');
            } else {
                document.body.setAttribute('data-theme', 'light');
                localStorage.setItem('theme', 'light');
            }
        });
    }
});
