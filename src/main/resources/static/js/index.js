const token = Cookies.get('jwtToken');
$(document).ready(function() {
    if (token) {
        $('.btn-cadastro').hide();
        $('.login-trigger').hide();
        $('.logout-trigger').show();
        $('.container-cadastro').hide();
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
        Cookies.remove('jwtToken'); 
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

    if(token) {
        try {
            const decodedPayload = jwt_decode(token);
            
            if(decodedPayload.role === "Usuario") {
                return;
            }
        }
        catch (error) {
            console.error('Error decoding JWT:', error.message);
        }
    } 
    
    else {
        console.error('Token JWT não encontrado.');
    }

    

    const brechoContainer = $(".brecho-container");
    const brechoContent = `
     <div class="meus-brechos">
        <h1 class="text-center" style="color: white;">Gerencie seu brechó!</h1>
        <p class="text-center">Clique <a href="vendedor/meu-brecho">Aqui!</span></p>
    </div>
`
    brechoContainer.append(brechoContent);

}