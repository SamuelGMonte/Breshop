var isLoggedOut = false;
var isInactive = true;
const INACTIVITY_TIMEOUT = 15 * 60 * 1000;  // 15 minutes timeout
let inactivityTimer;
const LOGIN_PAGE_URL = '/';  

// Set token in AJAX requests
$(document).ajaxSend(function(e, xhr, options) {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + token);
        // console.log("Token enviado:", token);
    }
});

// // Handle unauthorized responses (401 errors)
// $(document).ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
//     if (jqXHR.status === 401 && !isLoggedOut) {
//         isLoggedOut = true;
//         console.log("Token expirado");

//         localStorage.removeItem('jwtToken');
//         window.location.href = LOGIN_PAGE_URL;  // Redirect to login page

//         alert("Sessão expirada ou você foi deslogado por inatividade. Logue novamente.");
        
//     }
// });

