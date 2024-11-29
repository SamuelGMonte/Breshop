
$(document).ready(function() {
    const token = Cookies.get('jwtToken');
    if (token) {
        window.location.href = '/'; 
    }

    $('#usuarioLoginForm').on('submit', function(event) {
        event.preventDefault();

        const email = $('#email').val(); 
        const senha = $('#usuarioSenha').val(); 

        const loginData = {
            email: email,
            senha: senha
        };

        $.ajax({
            url: $(this).attr('action'), 
            method: 'POST',
            contentType: 'application/json', 
            data: JSON.stringify(loginData),
            success: function(response) {
                Swal.fire({
                    icon: 'success',
                    title: 'Sucesso!',
                    text: 'Login realizado com sucesso!',
                    confirmButtonText: 'OK'
                }).then(() => {
                    Cookies.set('jwtToken', response.token, { path: '/' });
                    window.location.href = '/';
                });
            },
            error: function(xhr) {
                let errorMessage = 'Ocorreu um erro ao fazer login.';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                }

                Swal.fire({
                    icon: 'error',
                    title: 'Erro!',
                    text: errorMessage,
                    confirmButtonText: 'OK'
                });
            }
        });
    });
});
