$(document).ready(function() {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        window.location.href = '/'; 
    }
    $('#vendedorForm').on('submit', function(event) {        
        event.preventDefault();

        const password = $('#vendedorSenha').val();
        const confirmPassword = $('#vendedorConfirmaSenha').val();

        if(password !== confirmPassword) {
            Swal.fire({
                icon: 'error',
                title: 'Erro!',
                text: 'As senhas diferem.',
                confirmButtonText: 'OK'
            });
            return;
        }

        const formData = $(this).serialize(); 

        $.ajax({
            url: $(this).attr('action'),
            method: 'POST',
            data: formData,
            success: function(successMessage) {
                Swal.fire({
                    icon: 'success',
                    title: 'Sucesso!',
                    text: JSON.stringify(successMessage),
                    confirmButtonText: 'OK'
                }).then(() => {
                
                });
            },
            error: function(xhr) {
                if (xhr.status === 409) { 
                    Swal.fire({
                        icon: 'error',
                        title: 'Erro!',
                        text: JSON.stringify(xhr.responseText),
                        confirmButtonText: 'OK'
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Erro!',
                        text: 'Ocorreu um erro ao cadastrar o vendedor.',
                        confirmButtonText: 'OK'
                    });
                }
            }
        });
    });
});
