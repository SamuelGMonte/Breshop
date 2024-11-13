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

        const formData = new FormData(this);

        const endereco = $("#brechoEndereco").val();

        formData.append('brechoEndereco', isOnlineFlag ? "Online" : endereco);
        
        const fileInput = $('#submitFile')[0];

        if (fileInput && fileInput.files.length > 0) {
            const file = fileInput.files[0];
            console.log("Arquivo selecionado:", file); 
            formData.append('filename', file);
        } else {
            console.log("Nenhum arquivo selecionado.");
        }

        $.ajax({
            url: $(this).attr('action'),
            method: 'POST',
            data: formData,
            processData: false, 
            contentType: false,
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
        addressInput.attr('disabled', true);
        addressInput.val("Online");
    } else {
        addressInput.attr('disabled', false);
        addressInput.val("");
    }
}

function updateFileName() {
    const input = document.getElementById('submitFile');
    const fileName = input.files.length > 0 ? input.files[0].name : 'Nenhum arquivo selecionado';
    $('#fileName').text(fileName);
}


function isJSON(str) {
    try {
        JSON.parse(str);
        return true;
    } catch (e) {
        return false;
    }
}
