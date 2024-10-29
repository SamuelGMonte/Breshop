$(document).ajaxSend(function(e, xhr, options) {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + token);
        console.log("Token enviado:", token); 
    }
});
