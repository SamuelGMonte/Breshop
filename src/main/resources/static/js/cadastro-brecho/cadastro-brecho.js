var isOnlineFlag = false;
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

        const endereco = $("#brechoEndereco").val();
        
        const formData = $(this).serialize() + "&endereco=" + (isOnlineFlag ? "Online" : endereco);
        
        $.ajax({
            url: $(this).attr('action'),
            method: 'POST',
            data: formData,
            success: function(successMessage) {
                Swal.fire({
                    iconHtml: '<img src="../assets/mail-logo.png" style="height: 50px;">',
                    title: 'Sucesso!',
                    text: successMessage,
                    confirmButtonText: 'OK'
                }).then(() => {
                    // window.location.href = '/';
                });
            },
            error: function(xhr) {
                if (xhr.status === 409) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Erro!',
                        text: isJSON(xhr.responseText) ? JSON.parse(xhr.responseText).error : xhr.responseText,
                        confirmButtonText: 'OK'
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Erro!',
                        text: isJSON(xhr.responseText) ? JSON.parse(xhr.responseText).error : xhr.responseText,
                        confirmButtonText: 'OK'
                    });
                }
            }
        });
    });
});


function toggleAddress() {
    const addressInput = $("#brechoEndereco");
    const isOnlineOnly = $("#checkOnline").is(":checked");

    isOnlineFlag = isOnlineOnly;

    if (isOnlineOnly) {
        addressInput.val("Online");
    } else {
        addressInput.val("");
    }
}



function isJSON(str) {
    try {
        JSON.parse(str);
        return true;
    } catch (e) {
        return false;
    }
}
