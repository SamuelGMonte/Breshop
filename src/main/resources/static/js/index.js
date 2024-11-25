const token = localStorage.getItem('jwtToken');
$(document).ready(function() {
    if (token) {
        $('.btn-cadastro').hide();
        $('.login-trigger').hide();
        $('.logout-trigger').show();
        $('.container-cadastro').hide();
        $('.container-content').hide();
        generateBrecho();
    } else {
        $('.btn-cadastro').show();
        $('.login-trigger').show();
        $('.logout-trigger').hide();
        $('.container-cadastro').show();
        $('.container-content').show();
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

        Swal.fire({
            title: 'Carregando...',
            text: 'Aguarde enquanto criamos seu usuário.',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

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


function generateBrecho() {

    const payload = jwt_decode(token);
    const userId = payload.userId;
    console.log("User ID:", userId);
    

    const brechoContainer = $(".brecho-container");
    const brechoContent = `
     <div class="meus-brechos">
        <h1>Navegue pelo seus brechós!</h1>
        <p>Clique <a href="">Aqui!</span></p>
    </div>
`
    brechoContainer.append(brechoContent);

}