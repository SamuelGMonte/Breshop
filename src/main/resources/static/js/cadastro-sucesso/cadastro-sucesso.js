var seconds = 7;

$(document).ready(function() {
    countdownTimer();
});

function redirect() {
    document.location.href = '/';
}

function updateSecs() {
    $('.countdown').text(seconds); 
    seconds--;
    if (seconds < 0) {
        clearInterval(timer);
        redirect();
    }
}

function countdownTimer() {
    timer = setInterval(function () {
        updateSecs(); 
    }, 1000)
}
