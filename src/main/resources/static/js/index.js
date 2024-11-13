$(document).ready(function() {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        $('.btn-cadastro').hide();
        $('.login-trigger').hide();
        $('.logout-trigger').show();
        $('.container-cadastro').hide();
    } else {
        $('.btn-cadastro').show();
        $('.login-trigger').show();
        $('.logout-trigger').hide();
        $('.container-cadastro').show();
    }

    $('#usuarioForm').on('submit', function(event) {        
        event.preventDefault();

        const password = $('#usuarioSenha').val();
        const confirmPassword = $('#usuarioConfirmaSenha').val();

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

    $('.logout-trigger').on('click', function() {
        localStorage.removeItem('jwtToken'); 
        $('.btn-cadastro').show(); 
        $('.logout-trigger').hide();
        Swal.fire({
            icon: 'success',
            title: 'Desconectado',
            text: "Você foi desconectado",
        }).then(() => {
            location.reload()
        });
    });
});

function isJSON(str) {
    try {
        JSON.parse(str);
        return true;
    } catch (e) {
        return false;
    }
}


document.querySelector('.login-trigger').addEventListener('click', function() {
    document.querySelector('.dropdown-arrow').classList.toggle('open');
});

document.addEventListener('click', function(event) {
    const loginContainer = document.querySelector('.login-container');
    const loginOptions = document.querySelector('#loginOptions');
    if (!loginContainer.contains(event.target)) {
        loginOptions.classList.remove('show');
        document.querySelector('.dropdown-arrow').classList.remove('open');
    }
});

window.addEventListener('load', function() {
    document.querySelector('.underline-animation').classList.add('active');
});