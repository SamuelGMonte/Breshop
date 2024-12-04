var isInactive = true;
const LOGIN_PAGE_URL = '/';  

// Set token in AJAX requests
$(document).ajaxSend(function(e, xhr, options) {
    const token = Cookies.get('jwtToken');
    if (token) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + token);
        // console.log("Token enviado:", token);
    }
});


$(document).ajaxError(function(event, xhr) {
    if (xhr.status === 401) {
        try {
            const response = JSON.parse(xhr.responseText);
            if (response.message === "Sessão expirada, por favor, logue novamente") {
                Swal.fire({
                    icon: 'error',
                    title: 'Sessão Expirada',
                    text: response.message,
                    confirmButtonText: 'OK'
                }).then(() => {
                    Cookies.remove('jwtToken');
                    window.location.href = LOGIN_PAGE_URL;
                });
            }
        } catch (error) {
            console.error("Error parsing server response:", error);
        }
    } else {
        console.error("AJAX error occurred:", xhr.status, xhr.responseText);
    }
});