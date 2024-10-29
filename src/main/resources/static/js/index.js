$(document).ready(function() {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        $('.btn-cadastro').hide();
        $('.btn-login').hide();
        $('.btn-logout').show();
    } else {
        $('.btn-cadastro').show();
        $('.btn-login').show();
        $('.btn-logout').hide();
    }

    $('.btn-logout').on('click', function() {
        localStorage.removeItem('jwtToken'); 
        $('.btn-cadastro').show(); 
        $('.btn-logout').hide();
        Swal.fire({
            icon: 'success',
            title: 'Desconectado',
            text: "Você foi desconectado",
        }).then(() => {
            location.reload()
        });
    });
});
